package com.fw.beattheory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {

    // Non-static variable to hold the AUTH_TOKEN from the properties file
    @Value("${api.key}")
    private String authToken;

    // Static variable that will hold the non-static value after initialization
    private static String AUTH_TOKEN;


    // Method to set the static AUTH_TOKEN after the Spring context initializes
    @jakarta.annotation.PostConstruct
    public void init() {
        AUTH_TOKEN = this.authToken.replaceAll("\\s", "");
    }

    // Static method that needs the AUTH_TOKEN value
    public static String getAuthentication() {
        return AUTH_TOKEN;
    }
}
