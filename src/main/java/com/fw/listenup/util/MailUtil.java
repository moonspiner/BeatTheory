package com.fw.listenup.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fw.listenup.models.auth.EmailVerificationDetail;

import ch.qos.logback.classic.Logger;

public class MailUtil {
    private static Logger logger = (Logger) LoggerFactory.getLogger(MailUtil.class);
    public static void sendEmail(EmailVerificationDetail evd){
        logger.info("Sending verification email");
        String senderEmail = "ericboland12@gmail.com";
        String senderPassword = "ezab dbti ydog hdhk";

        // SMTP server host and port
        String smtpHost = "smtp.googlemail.com";
        int smtpPort = 465;

        //Init thymeleaf
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);

        //Thymleaf context
        String helloUser = "Hello " + evd.getUsername() + ",";
        String link = "http://localhost:8080/api/v1/auth/registerToken?uid=" + evd.getUid();
        Context context = new Context();
        context.setVariable("helloUser", helloUser);
        context.setVariable("link", link);
        try {
            
            //HTML template
            String templateFilePath = "templates/email_registration_template.html";
            String processedHtml = templateEngine.process(templateFilePath, context);

            // Create the email message
            HtmlEmail email = new HtmlEmail();
            email.setHostName(smtpHost);
            email.setSmtpPort(smtpPort);
            email.setAuthenticator(new DefaultAuthenticator(senderEmail, senderPassword));
            email.setSSLOnConnect(true); // Use SSL
            email.setFrom(senderEmail);
            email.addTo(evd.getEmail());
            email.setSubject("ListenUp - Verify your Account");
            email.setHtmlMsg(processedHtml);

            // Send the email
            email.send();

            logger.info("Email sent successfully!");

        } catch (EmailException e) {
            logger.error("There was an error with sending the verification email: " + e.toString());
        }
    }    
}
