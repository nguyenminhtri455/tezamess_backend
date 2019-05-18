package com.tezamess.serviceimpl;

import com.tezamess.exception.InvalidateException;
import com.tezamess.model.ResultModel;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.UserRepositoryImpl;
import com.tezamess.service.JwtService;
import com.tezamess.service.UserService;
import com.tezamess.validator.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.io.IOException;
import java.util.List;
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

    @Override
    public List<UserModel> findAll() {
        List<UserModel> list = userRepositoryImpl.findAll();
        list.stream().forEach(t -> t.setPassword(new String(Base64.getDecoder().decode(t.getPassword()))));
        return list;
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
            return new ResponseEntity<>(new ResultModel(environment.getProperty("json.invalid")), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModel(ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("error.server")), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //kiem tra account trong database
        UserModel userResponse = userRepositoryImpl.login(user);
        if (userResponse == null) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("login.error")), HttpStatus.BAD_REQUEST);
        }

        // tao header tra ve token va account info
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = jwtService.generateTokenLogin(userResponse.getPhone());
        httpHeaders.add("token", token);
        return ResponseEntity.ok().headers(httpHeaders).body(userResponse);
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
            return new ResponseEntity<>(new ResultModel(environment.getProperty("json.invalid")), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModel(ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("error.server")), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserModel userModel = userRepositoryImpl.register(user);
        //neu userModel la null thi tai khoan da ton tai
        if (userModel == null) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("error.register.exists")), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(userModel, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> updateUser(String token, String json) {
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
            String validate = userValidator.validateUpdate(user);
            if (validate != null) {
                throw new InvalidateException(validate);
            }

            //kiem tra phone number co trung voi phone number cua token khong
            if (!jwtService.getPhoneFromToken(token).equals(user.getPhone())) {
                return new ResponseEntity<>(new ResultModel(environment.getProperty("error.denied")), HttpStatus.UNAUTHORIZED);
            }

        } catch (IOException ex) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("json.invalid")), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModel(ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("error.server")), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        UserModel userModel = userRepositoryImpl.updateUser(user);
        //neu userModel la null thi tai khoan da ton tai
        if (userModel == null) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("error.update.unexists")), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(userModel, HttpStatus.OK);
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

            //kiem tra validate UserModel
            String validate = userValidator.validatePhoneUser(user);
            if (validate != null) {
                throw new InvalidateException(validate);
            }

        } catch (IOException ex) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("json.invalid")), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModel(ex.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("error.server")), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserModel userModel = userRepositoryImpl.findUserByPhone(user.getPhone());
        //neu userModel la null thi tai khoan khong ton tai
        if (userModel == null) {
            return new ResponseEntity<>(new ResultModel(environment.getProperty("error.update.unexists")), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(userModel, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> findUserById(int id) {
        UserModel user = userRepositoryImpl.findUserById(id);
        user.setPassword(new String(Base64.getDecoder().decode(user.getPassword())));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public UserModel getUserByPhone(String phone) {
        return userRepositoryImpl.findUserByPhone(phone);
    }

}
