package com.tezamess.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FileUtils {

//    private final String path = "http://192.168.0.104:8080/profile/";
//    private final String path = "http://172.16.26.173:8080/profile/";
//    private final String root = "/tezamess/src/main/resources/static/profile";
    
    private final String path = "http://tezamess-tezamess.7e14.starter-us-west-2.openshiftapps.com/profile/";
    private final String root = "/home/jboss/profile";

    public String uploadAvatar(String valueBase64, String name) {
        byte[] value = Base64.getDecoder().decode(valueBase64);
      
        File file = new File(root);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(root + File.separator + name))) {
            fileOutputStream.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path + name;
    }
}
