package com.fw.listenup.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

//Util class for create and signing JWTs
public class JwtUtil {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(String subject, long expiration){
        return Jwts.builder()
                    .setSubject(subject)
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(key)
                    .compact();
    }
}
