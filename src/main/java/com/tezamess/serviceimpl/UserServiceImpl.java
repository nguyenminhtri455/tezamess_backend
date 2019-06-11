package com.tezamess.serviceimpl;

import com.tezamess.exception.InvalidateException;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.UserRepositoryImpl;
import com.tezamess.service.JwtService;
import com.tezamess.service.UserService;
import com.tezamess.validator.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tezamess.map.MappedUserModel;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.ResultModelV2.Status;
import com.tezamess.utils.FileUtils;
import java.util.Base64;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:message.properties")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Environment environment;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public List<Map<String, Object>> findAll() {
        List<UserModel> list = userRepositoryImpl.findAll();

        if (list != null) {
            List<Map<String, Object>> l = new ArrayList<>();
            list.stream().forEach(t -> {
                t.setPassword(new String(Base64.getDecoder().decode(t.getPassword())));
                Map<String, Object> convertToMap = MappedUserModel.convertToMap(t);
                l.add(convertToMap);
            });
            list.clear();
            return l;
        }
        return null;
    }

    @Override
    public ResponseEntity<Object> login(String json) {
        UserModel user;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }

            //convert input data thanh UserModel
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(json, UserModel.class);

            //kiem tra validate UserModel
            String validate = userValidator.validateLogin(user);
            if (validate != null) {
                throw new InvalidateException(validate);
            }

        } catch (IOException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_JSON.getStatus(), null, environment.getProperty("json.invalid"), new Date()), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_VALIDATE.getStatus(), null, ex.getMessage(), new Date()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_SERVER.getStatus(), null, environment.getProperty("error.server"), new Date()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //kiem tra account trong database
        UserModel userResponse = userRepositoryImpl.login(user);
        if (userResponse == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_FAILED.getStatus(), null, environment.getProperty("login.error"), new Date()), HttpStatus.BAD_REQUEST);
        }

        // tao header tra ve token va account info
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = jwtService.generateTokenLogin(userResponse.getPhone());
        httpHeaders.add("token", token);
        return ResponseEntity.ok().headers(httpHeaders).body(new ResultModelV2(Status.SUCCESS.getStatus(), MappedUserModel.convertToMap(userResponse), Status.SUCCESS.name(), new Date()));
    }

    @Override
    public ResponseEntity<Object> register(String json) {
        UserModel user;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }

            //convert input data thanh UserModel
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(json, UserModel.class);

            //kiem tra validate UserModel
            String validate = userValidator.validateRegister(user);
            if (validate != null) {
                throw new InvalidateException(validate);
            }

        } catch (IOException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_JSON.getStatus(), null, environment.getProperty("json.invalid"), new Date()), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_VALIDATE.getStatus(), null, ex.getMessage(), new Date()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_SERVER.getStatus(), null, environment.getProperty("error.server"), new Date()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserModel userModel = userRepositoryImpl.register(user);
        //neu userModel la null thi tai khoan da ton tai
        if (userModel == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_FAILED.getStatus(), null, environment.getProperty("error.register.exists"), new Date()), HttpStatus.BAD_REQUEST);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        String token = jwtService.generateTokenLogin(userModel.getPhone());
        httpHeaders.add("token", token);
        return ResponseEntity.ok().headers(httpHeaders).body(new ResultModelV2(Status.SUCCESS.getStatus(), MappedUserModel.convertToMap(userModel), Status.SUCCESS.name(), new Date()));

    }

    @Override
    public ResponseEntity<Object> updateUser(String token, String json) {
//        regex base64
        String regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";
        UserModel user;
        String urlAvatar;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            System.out.println(json);
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("avatar")) {
                Object object = jsonObject.get("avatar");
                JSONObject avatar = new JSONObject(object.toString());
                if (avatar.getString("valueBase64").matches(regex)) {
                    urlAvatar = fileUtils.uploadAvatar(avatar.getString("valueBase64"), avatar.getString("name"), jsonObject.getString("phone"));
                    jsonObject.put("urlavatar", urlAvatar);
                }
                jsonObject.remove("avatar");
            }

            long time = jsonObject.getLong("birthday");
            Date date = new Date(time);

            //convert Date to Timestamp in db
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            jsonObject.put("birthday", format.format(date));

            //convert input data thanh UserModel
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(jsonObject.toString(), UserModel.class);
            //kiem tra validate UserModel
            String validate = userValidator.validateUpdate(user);
            if (validate != null) {
                throw new InvalidateException(validate);
            }

            //kiem tra phone number co trung voi phone number cua token khong
            if (!jwtService.getPhoneFromToken(token).equals(user.getPhone())) {
                return new ResponseEntity<>(new ResultModelV2(Status.ERROR_AUTHORICATION.getStatus(), null, environment.getProperty("error.denied"), new Date()), HttpStatus.UNAUTHORIZED);
            }

        } catch (IOException | JSONException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_JSON.getStatus(), null, environment.getProperty("json.invalid"), new Date()), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_VALIDATE.getStatus(), null, ex.getMessage(), new Date()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_SERVER.getStatus(), null, environment.getProperty("error.server"), new Date()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserModel userModel = userRepositoryImpl.updateUser(user);
        //neu userModel la null thi tai khoan khong ton tai
        if (userModel == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_FAILED.getStatus(), null, environment.getProperty("error.update.unexists"), new Date()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), MappedUserModel.convertToMap(userModel), Status.SUCCESS.name(), new Date()), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Object> findUserByPhone(String token, String json) {
        UserModel user;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }

            //convert input data thanh UserModel
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(json, UserModel.class);

            //kiem tra validate phone number
            String validate = userValidator.validatePhoneUser(user.getPhone());
            if (validate != null) {
                throw new InvalidateException(validate);
            }

        } catch (IOException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_JSON.getStatus(), null, environment.getProperty("json.invalid"), new Date()), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_VALIDATE.getStatus(), null, ex.getMessage(), new Date()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_SERVER.getStatus(), null, environment.getProperty("error.server"), new Date()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserModel userModel = userRepositoryImpl.findUserByPhone(user.getPhone());
        //neu userModel la null thi tai khoan khong ton tai
        if (userModel == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_FAILED.getStatus(), null, environment.getProperty("error.update.unexists"), new Date()), HttpStatus.BAD_REQUEST);
        }
        userModel.setPassword(null);
        return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), MappedUserModel.convertToMap(userModel), Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> findUserById(int id) {
        UserModel user = userRepositoryImpl.findUserById(id);
        if (user != null) {
            user.setPassword(new String(Base64.getDecoder().decode(user.getPassword())));
            return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), MappedUserModel.convertToMap(user), Status.SUCCESS.name(), new Date()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResultModelV2(Status.ERROR_FAILED.getStatus(), null, environment.getProperty("error.unexists"), new Date()), HttpStatus.BAD_REQUEST);
    }

    @Override
    public UserModel getUserByPhone(String phone) {
        return userRepositoryImpl.findUserByPhone(phone);
    }

    @Override
    public ResponseEntity<Object> userUsingApp(String token, String json) {
        JSONArray array;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            array = new JSONArray(json);

        } catch (IOException | JSONException ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_JSON.getStatus(), null, environment.getProperty("json.invalid"), new Date()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_SERVER.getStatus(), null, environment.getProperty("error.server"), new Date()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String phoneFromToken = jwtService.getPhoneFromToken(token);
        if (phoneFromToken == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_ACCESS_DENIED.getStatus(), null, environment.getProperty("error.denied"), new Date()), HttpStatus.BAD_REQUEST);
        }

        UserModel findUserByPhone = userRepositoryImpl.findUserByPhone(phoneFromToken);
        if (findUserByPhone == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_ACCESS_DENIED.getStatus(), null, environment.getProperty("error.denied"), new Date()), HttpStatus.BAD_REQUEST);
        }

        List<Object[]> listUserModel = userRepositoryImpl.checkUserUsingApp(findUserByPhone.getId(), array.toList());
        if (listUserModel == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), null, Status.SUCCESS.name(), new Date()), HttpStatus.BAD_REQUEST);
        }

        int size = listUserModel.size();
        List<Map<String, Object>> list = new ArrayList<>();
        listUserModel
                .stream()
                .filter(t -> t[4] == null)
                .forEach(t -> {
                    Map<String, Object> map = new HashMap();
                    map.put("id", t[0]);
                    map.put("phone", t[1]);
                    map.put("name", t[2]);
                    map.put("urlavatar", t[3]);
                    map.put("relationship", false);
                    list.add(map);
                });
        return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), list, Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }
}
