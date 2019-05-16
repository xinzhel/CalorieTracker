package com.example.calorietracker.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncodeHelper {
    public static String encodeURIComponent(String str){
        return str.replaceAll("/","%2F")
                .replaceAll(":","%3A")
                .replaceAll("=", "%3D")
                .replaceAll("&", "%26");
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String hmacsha1(String data, String key)
    {
        String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);

        mac.init(signingKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString((mac.doFinal(data.getBytes())));
    }


}
