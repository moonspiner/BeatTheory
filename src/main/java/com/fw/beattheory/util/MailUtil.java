package com.fw.beattheory.util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.fw.beattheory.models.auth.EmailVerificationDetail;
import com.fw.beattheory.models.auth.PasswordVerificationDetail;

import ch.qos.logback.classic.Logger;

@Component
public class MailUtil {
    private static Logger logger = (Logger) LoggerFactory.getLogger(MailUtil.class);
    
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.password}")
    private String senderPassword;
    
    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.port}")
    private int smtpPort;

    @Value("${mail.verification.link}")
    private String verificationAccountUrl;

    @Value("${verify.password.link}")
    private String resetPasswordUrl;


    public void sendRegistraionEmail(EmailVerificationDetail evd){
        logger.info("Sending verification email");

        //Thymleaf context
        String helloUser = "Hello " + evd.getUsername() + ",";
        String link = verificationAccountUrl + evd.getUid();
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
            emailTemplate.setSubject("beattheory - Verify your Account");
            emailTemplate.setHtmlMsg(processedHtml);

            // Send the email
            emailTemplate.send();

            logger.info("Registration email sent successfully!");

        } catch (EmailException e) {
            logger.error("There was an error with sending the verification email: " + e.toString());
        }
    }
    
    public boolean sendPasswordResetEmail(PasswordVerificationDetail pvd){
        logger.info("Sending password reset email");

        //Thymleaf context
        String link = resetPasswordUrl + pvd.getUid(); //CHANGE THIS LATER
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
            emailTemplate.setSubject("beattheory - Reset your Password");
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
    private TemplateEngine initThymeTemplate(){
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
