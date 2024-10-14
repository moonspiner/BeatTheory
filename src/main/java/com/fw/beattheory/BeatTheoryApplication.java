package com.fw.beattheory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;



@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class BeatTheoryApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
         // Get the value of the custom environment variable "ENV"
         String env = System.getenv("ENV");
         System.out.println("ENV IS " + env);

         // Set the active profile based on the environment variable
         SpringApplication app = new SpringApplication(BeatTheoryApplication.class);
         if (env != null) {
             app.setAdditionalProfiles(env); // Set the profile to the value of ENV
        } else {
             app.setAdditionalProfiles("DEV"); // Default to "dev" if ENV is not set
        }

         
        app.run(args);

	}
}
