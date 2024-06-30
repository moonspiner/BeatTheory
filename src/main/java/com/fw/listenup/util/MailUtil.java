package com.fw.listenup.util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fw.listenup.models.auth.EmailVerificationDetail;
import com.fw.listenup.models.auth.PasswordVerificationDetail;

import ch.qos.logback.classic.Logger;

public class MailUtil {
    private static Logger logger = (Logger) LoggerFactory.getLogger(MailUtil.class);
    private static String senderEmail = "ericboland12@gmail.com";
    private static String senderPassword = "ezab dbti ydog hdhk";
    private static String smtpHost = "smtp.googlemail.com";
    private static int smtpPort = 465;
    public static void sendRegistraionEmail(EmailVerificationDetail evd){
        logger.info("Sending verification email");

        //Thymleaf context
        String helloUser = "Hello " + evd.getUsername() + ",";
        String link = "http://localhost:4200/finish-verification?token=" + evd.getUid(); //CHANGE THIS LATER
        Context context = new Context();
        context.setVariable("helloUser", helloUser);
        context.setVariable("link", link);
        try {
            //HTML template
            TemplateEngine templateEngine = initThymeTemplate();
            String templateFilePath = "templates/email_registration_template.html";
            String processedHtml = templateEngine.process(templateFilePath, context);

            // Create the email message
            HtmlEmail emailTemplate = new HtmlEmail();
            emailTemplate.setHostName(smtpHost);
            emailTemplate.setSmtpPort(smtpPort);
            emailTemplate.setAuthenticator(new DefaultAuthenticator(senderEmail, senderPassword));
            emailTemplate.setSSLOnConnect(true); // Use SSL
            emailTemplate.setFrom(senderEmail);
            emailTemplate.addTo(evd.getEmail());
            emailTemplate.setSubject("ListenUp - Verify your Account");
            emailTemplate.setHtmlMsg(processedHtml);

            // Send the email
            emailTemplate.send();

            logger.info("Registration email sent successfully!");

        } catch (EmailException e) {
            logger.error("There was an error with sending the verification email: " + e.toString());
        }
    }
    
    public static boolean sendPasswordResetEmail(PasswordVerificationDetail pvd){
        logger.info("Sending password reset email");

        //Thymleaf context
        String link = "http://localhost:4200/verify-password-reset?token=" + pvd.getUid(); //CHANGE THIS LATER
        Context context = new Context();
        context.setVariable("link", link);
        try {
            //HTML template
            TemplateEngine templateEngine = initThymeTemplate();
            String templateFilePath = "templates/password_reset_template.html";
            String processedHtml = templateEngine.process(templateFilePath, context);

            // Create the email message
            HtmlEmail emailTemplate = new HtmlEmail();
            emailTemplate.setHostName(smtpHost);
            emailTemplate.setSmtpPort(smtpPort);
            emailTemplate.setAuthenticator(new DefaultAuthenticator(senderEmail, senderPassword));
            emailTemplate.setSSLOnConnect(true); // Use SSL
            emailTemplate.setFrom(senderEmail);
            emailTemplate.addTo(pvd.getEmail());
            emailTemplate.setSubject("ListenUp - Reset your Password");
            emailTemplate.setHtmlMsg(processedHtml);

            // Send the email
            emailTemplate.send();
            logger.info("Email sent successfully!");
            return true;

        } catch (EmailException e) {
            logger.error("There was an error with sending the password reset email: " + e.toString());
            return false;
        }
    }
    
    //Init Thymeleaf
    private static TemplateEngine initThymeTemplate(){
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
