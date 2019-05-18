package com.tezamess.validator;

import com.tezamess.model.UserModel;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class UserValidator {

    //so dien thoai bat dau la 0 va co 10 ky tu
    private final String REGEX_PHONE = "0[0-9]{9}";

    //password tu 6 -> 30 ki tu va khong co khoang trang
    private final String REGEX_PASSWORD = "[^\\s]{6,30}";

    @Autowired
    Environment environment;

    public String validateLogin(UserModel userModel) {
        Pattern pattern = Pattern.compile(REGEX_PHONE);

        //kiem tra input data khac null
        if (userModel.getPhone() == null || userModel.getPassword() == null) {
            return environment.getProperty("errer.null");
        }

        userModel.setPhone(userModel.getPhone().trim());
        userModel.setPassword(userModel.getPassword().trim());

        //kiem tra khac rong
        if (userModel.getPhone().isEmpty() || userModel.getPassword().isEmpty()) {
            return environment.getProperty("errer.isempty");
        }

        //kiem tra dinh dang phone
        if (!pattern.matcher(userModel.getPhone()).matches()) {
            return environment.getProperty("errer.phoneinvalid");
        }
        return null;
    }

    public String validateRegister(UserModel userModel) {
        Pattern pattern = Pattern.compile(REGEX_PHONE);

        //kiem tra input data khac null
        if (userModel.getPhone() == null || userModel.getName() == null || userModel.getPassword() == null) {
            return environment.getProperty("errer.null");
        }

        userModel.setPhone(userModel.getPhone().trim());
        userModel.setName(userModel.getName().trim());
        userModel.setPassword(userModel.getPassword().trim());

        //kiem tra khac rong
        if (userModel.getPhone().isEmpty() || userModel.getName().isEmpty() || userModel.getPassword().isEmpty()) {
            return environment.getProperty("errer.isempty");
        }

        //kiem tra length name
        if (userModel.getName().length() <= 0 || userModel.getName().length() > 30) {
            return environment.getProperty("errer.length.name");
        }

        //kiem tra dinh dang password
        if (!userModel.getPassword().matches(REGEX_PASSWORD)) {
            return environment.getProperty("error.passwordinvalid");
        }

        //kiem tra dinh dang phone
        if (!pattern.matcher(userModel.getPhone().trim()).matches()) {
            return environment.getProperty("errer.phoneinvalid");
        }

        return null;
    }

    public String validateUpdate(UserModel userModel) {
        Pattern pattern = Pattern.compile(REGEX_PHONE);

        //kiem tra input data khac null
        if (userModel.getPhone() == null || userModel.getName() == null || userModel.getBirthday() == null) {
            return environment.getProperty("errer.null");
        }

        userModel.setPhone(userModel.getPhone().trim());
        userModel.setName(userModel.getName().trim());

        //kiem tra khac rong
        if (userModel.getPhone().isEmpty() || userModel.getName().isEmpty()) {
            return environment.getProperty("errer.isempty");
        }

        //kiem tra dinh dang phone
        if (!pattern.matcher(userModel.getPhone().trim()).matches()) {
            return environment.getProperty("errer.phoneinvalid");
        }

        return null;

    }

    public String validatePhoneUser(UserModel userModel) {
        Pattern pattern = Pattern.compile(REGEX_PHONE);

        //kiem tra input data khac null
        if (userModel.getPhone() == null) {
            return environment.getProperty("errer.null");
        }

        //kiem tra khac rong
        if (userModel.getPhone().isEmpty()) {
            return environment.getProperty("errer.isempty");
        }

        //kiem tra dinh dang phone
        if (!pattern.matcher(userModel.getPhone().trim()).matches()) {
            return environment.getProperty("errer.phoneinvalid");
        }

        return null;

    }

}
