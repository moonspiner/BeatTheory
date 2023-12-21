package com.fw.listenup.util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MailUtil {

    public static void sendEmail(){
        String senderEmail = "ericboland12@gmail.com";
        String senderPassword = "ezab dbti ydog hdhk";

        // Recipient's email address
        String recipientEmail = "foolishwizard25@protonmail.com";

        // SMTP server host and port
        String smtpHost = "smtp.googlemail.com";
        int smtpPort = 465;

        try {
            // Create the email message
            Email email = new SimpleEmail();
            email.setHostName(smtpHost);
            email.setSmtpPort(smtpPort);
            email.setAuthenticator(new DefaultAuthenticator(senderEmail, senderPassword));
            email.setSSLOnConnect(true); // Use SSL
            email.setFrom(senderEmail);
            email.addTo(recipientEmail);
            email.setSubject("Apache Commons Email Example");
            email.setMsg("Hello, this is a test email sent using Apache Commons Email.");

            // Send the email
            email.send();

            System.out.println("Email sent successfully!");

        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
    
}
