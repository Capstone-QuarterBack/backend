package com.example.quaterback.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true); //https일때
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
