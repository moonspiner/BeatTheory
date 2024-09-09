package com.fw.listenup.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import jakarta.servlet.http.HttpServletRequest;

public class AuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    private static final String AUTH_TOKEN = "s54&5s9(4}_V2ro6X1@Rv$[he%j,!613";

    public static Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey == null || !apiKey.equals(AUTH_TOKEN)) {
            throw new BadCredentialsException("UNAUTHORIZED");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}