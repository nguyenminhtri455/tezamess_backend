package com.tezamess.serviceimpl;

import com.tezamess.exception.InvalidateException;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.UserRepositoryImpl;
import com.tezamess.service.JwtService;
import com.tezamess.service.UserService;
import com.tezamess.validator.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tezamess.map.MappedUserModel;
import static com.tezamess.map.MappedUserModel.convertToMapBy4Record;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.ResultModelV2.Status;
import com.tezamess.repository.FriendRepository;
import com.tezamess.utils.FileUtils;
import java.util.Base64;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:message.properties")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Environment environment;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static Message message;

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
        Map<String, Object> convertToMapWithRooms = MappedUserModel.convertToMapWithRooms(userResponse);

        List<UserModel> listUserSentRequestAddFriend = friendRepository.getUserSentRequestAddFriend(userResponse.getId());
        List<Map<String, Object>> convertToListBy5Record = MappedUserModel.convertToListBy5Record(listUserSentRequestAddFriend);

        convertToMapWithRooms.put("request", convertToListBy5Record);

        // tao header tra ve token va account info
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = jwtService.generateTokenLogin(userResponse.getPhone());
        httpHeaders.add("token", token);
        return ResponseEntity.ok().headers(httpHeaders).body(new ResultModelV2(Status.SUCCESS.getStatus(), convertToMapWithRooms, Status.SUCCESS.name(), new Date()));
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
        System.out.println(user.getName());
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
            if (array.length() == 0) {
                return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), new ArrayList(), Status.SUCCESS.name(), new Date()), HttpStatus.OK);
            }

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
            return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), new ArrayList(), Status.SUCCESS.name(), new Date()), HttpStatus.BAD_REQUEST);
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

    @Override
    public UserModel getUserById(int id) {
        return userRepositoryImpl.findUserById(id);
    }

    @Override
    public void notifyOnlineToRoomAndFriend(int userId, boolean login) {
        // lay danh sach ban be
        List<UserModel> friends = friendRepository.getFriends(userId);
        // ket ban voi admin
        UserModel admin = null;
        if (userId != 1000 && !friends.contains(new UserModel(1000))) {
            friendRepository.addFriendAdmin(userId, 1000);
            admin = userRepositoryImpl.findUserById(1000);
            friends.add(admin);
        }
        //thong bao online den ban be
        Map<String, Object> mapFriend = new HashMap<>();
        mapFriend.put("createdate", new Date().getTime());
        mapFriend.put("body", "Online");
        mapFriend.put("user", userId);
        mapFriend.put("type", "Notify");
        mapFriend.put("status", -1);
        friends.stream().forEach(t -> {
            mapFriend.put("friend", t.getId());
            messagingTemplate.convertAndSend("/room/user/" + t.getId(), mapFriend);
        });

        //thong bao online den cac room co tham gia
        UserModel user = userRepositoryImpl.findUserByIdWithRoom(userId);
        Map<String, Object> mapRoom = new HashMap<>();
        mapRoom.put("createdate", new Date().getTime());
        mapRoom.put("body", "Online");
        mapRoom.put("user", userId);
        mapRoom.put("type", "Notify");
        mapRoom.put("status", "Online");
        user.getParticipationModels().stream().forEach(t -> {
            mapRoom.put("room", t.getRoom().getId());
            messagingTemplate.convertAndSend("/room/" + t.getRoom().getId(), mapRoom);
        });

        if (!login) {
            List<Map<String, Object>> rooms = new ArrayList<>();
            user.getParticipationModels().stream()
                    .forEach(t -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", t.getRoom().getId());
                        m.put("creator", t.getRoom().getCreator().getId());
                        m.put("name", t.getRoom().getName());
                        m.put("type", t.getRoom().getTypeRoomModel().getId());
                        m.put("size", t.getRoom().getParticipationModels().size());

                        //Danh sach thanh vien trong phong
                        List<Map<String, Object>> members = new ArrayList<>();
                        t.getRoom().getParticipationModels().stream().forEach(s -> {
                            Map<String, Object> member = convertToMapBy4Record(s.getUser());
                            members.add(member);
                        });
                        m.put("members", members);
                        rooms.add(m);
                    });

            List<Map<String, Object>> mappingFriends = new ArrayList<>();
            if (friends.size() > 0) {
                mappingFriends = friends.stream().map(t -> MappedUserModel.convertToMapBy4Record(t)).collect(Collectors.toList());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("rooms", rooms);
            map.put("friends", mappingFriends);
            messagingTemplate.convertAndSend("/room/user/" + userId,
                    new ResultModelV2(ResultModelV2.Status.GET_ROOMS_AND_FRIENDS.getStatus(),
                            map, ResultModelV2.Status.GET_ROOMS_AND_FRIENDS.name(),
                            new Date()));
        } else {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> users = new ArrayList();
            Map<String, Object> convertToMapBy4Record;

            if (admin != null) {
                convertToMapBy4Record = MappedUserModel.convertToMapBy4Record(admin);
                users.add(convertToMapBy4Record);
            }
            map.put("rooms", new ArrayList());
            map.put("friends", users);
            messagingTemplate.convertAndSend("/room/user/" + userId,
                    new ResultModelV2(ResultModelV2.Status.GET_ROOMS_AND_FRIENDS.getStatus(),
                            map, ResultModelV2.Status.GET_ROOMS_AND_FRIENDS.name(),
                            new Date()));
        }
    }

    @Override
    public ResponseEntity<Object> changePassword(String token, String json) {
        UserModel user;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            System.out.println(json);
            JSONObject jsonObject = new JSONObject(json);

            //convert input data thanh UserModel
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(jsonObject.toString(), UserModel.class);

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

        UserModel userModel = userRepositoryImpl.changePassword(user);
        //neu userModel la null thi tai khoan khong ton tai
        if (userModel == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_FAILED.getStatus(), null, environment.getProperty("error.update.unexists"), new Date()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), MappedUserModel.convertToMap(userModel), Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> updateEmail(String token, String json) {
        UserModel user;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            System.out.println(json);
            JSONObject jsonObject = new JSONObject(json);

            //convert input data thanh UserModel
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(jsonObject.toString(), UserModel.class);

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

        UserModel userModel = userRepositoryImpl.updateEmail(user);
        //neu userModel la null thi tai khoan khong ton tai
        if (userModel == null) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_FAILED.getStatus(), null, environment.getProperty("error.update.unexists"), new Date()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), MappedUserModel.convertToMap(userModel), Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getResetCode(String json) {
        String email;
        String resetCode;
        String subject = "Recover Password";
        String msg = " là mã reset mật khẩu của bạn.";
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            System.out.println(json);
            JSONObject jsonObject = new JSONObject(json);
            Random random = new Random();
            resetCode = String.valueOf(random.nextInt(999999 - 100000) + 100000);
            email = jsonObject.getString("email");

            msg = resetCode + msg;
            sendEmail(email, subject, msg);

        } catch (IOException | JSONException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_JSON.getStatus(), null, environment.getProperty("json.invalid"), new Date()), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_VALIDATE.getStatus(), null, ex.getMessage(), new Date()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_SERVER.getStatus(), null, environment.getProperty("error.server"), new Date()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                new ResultModelV2(Status.SUCCESS.getStatus(), resetCode, Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> recoverPassword(String json) {
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            System.out.println(json);
            JSONObject jsonObject = new JSONObject(json);

            //convert input data thanh UserModel
            ObjectMapper mapper = new ObjectMapper();
            UserModel user = mapper.readValue(json, UserModel.class);

            userRepositoryImpl.recoverPassword(user);

        } catch (IOException | JSONException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_JSON.getStatus(), null, environment.getProperty("json.invalid"), new Date()), HttpStatus.BAD_REQUEST);
        } catch (InvalidateException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_VALIDATE.getStatus(), null, ex.getMessage(), new Date()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_SERVER.getStatus(), null, environment.getProperty("error.server"), new Date()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                new ResultModelV2(Status.SUCCESS.getStatus(), null, Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }

    @Override
    public void sendEmail(String to, String subject, String msg) {

        String SMTP_HOST_NAME = "smtp.gmail.com"; //can be your host server smtp@yourdomain.com
        String SMTP_AUTH_USER = "zoro53831@gmail.com"; //your login username/email
        String SMTP_AUTH_PWD = "Gmail.com@111111"; //password/secret

        String username = SMTP_AUTH_USER;
        String password = SMTP_AUTH_PWD;

        // Assuming you are sending email through relay.jangosmtp.net
        String host = SMTP_HOST_NAME;

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
//            Create a default MimeMessage object.
            message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(username, "Tezamess"));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setContent(msg, "text/html; charset=utf-8");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            Thread thread = new Thread(() -> {
                try {
                    // Send message
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        } catch (UnsupportedEncodingException | MessagingException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
