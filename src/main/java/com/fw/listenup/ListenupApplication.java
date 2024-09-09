package com.fw.listenup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class ListenupApplication {

	public static void main(String[] args) {
         // Get the value of the custom environment variable "ENV"
         String env = System.getenv("ENV");
         System.out.println("Environment variable is " + env);

         // Set the active profile based on the environment variable
         SpringApplication app = new SpringApplication(ListenupApplication.class);
         if (env != null) {
             app.setAdditionalProfiles(env); // Set the profile to the value of ENV
        } else {
             app.setAdditionalProfiles("DEV"); // Default to "dev" if ENV is not set
        }

         
        app.run(args);

	}
}
