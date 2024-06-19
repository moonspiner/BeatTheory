package com.fw.listenup.services;


import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fw.listenup.dao.AuthDAO;
import com.fw.listenup.entity.Image;
import com.fw.listenup.models.auth.EmailVerificationDetail;
import com.fw.listenup.models.auth.RegistrationLookupDetail;
import com.fw.listenup.models.auth.UserAuthenticationDetail;
import com.fw.listenup.util.CommonUtil;
import com.fw.listenup.util.MailUtil;

import ch.qos.logback.classic.Logger;

//Service class responsible for handling auth logic and proxying controller and dao
@Service
public class AuthService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private ResourceLoader resourceLoader;

    //Test connection to the db
    public boolean testConnection(){
        AuthDAO dao = new AuthDAO();
        return dao.testConnection();
    }
    //Retrieves authentication details for login attempt
    public UserAuthenticationDetail getUserAuthenticationDetail(String email){
        AuthDAO dao = new AuthDAO();
        return dao.getUserAuthenticationDetail(email);
    }

    //Lookups pre-existing user
    public RegistrationLookupDetail lookupExistingUser(String email, String username){
        AuthDAO dao = new AuthDAO();
        boolean emailTaken = false;
        boolean usernameTaken = false;

        //Check if email and username are taken.  If so, set error flags to true
        try{
            Map<String, String> credMap = dao.getExistingEmailAndUsername(email, username);
            if(credMap.containsKey("email")){
                if(!credMap.get("email").equals("")){
                    logger.error(email + " has been taken");
                    emailTaken = true;
                }
            }
            if(credMap.containsKey("username")){
                if(!credMap.get("username").equals("")){
                    logger.error(username + " has been taken");
                    usernameTaken = true;
                }
            }
        } catch(NullPointerException e){
            logger.error("The credential map returned empty: " + e.toString());
        }

        RegistrationLookupDetail rld = new RegistrationLookupDetail(emailTaken, usernameTaken);
        return rld;
        
    }

    //Overloaded method for looking up exisiting user
    private boolean lookupExistingUser(String email){
        AuthDAO dao= new AuthDAO();

        try{
            Map<String, String> credMap = dao.getExistingEmailAndUsername(email, "");
            if(credMap.containsKey("email")){
                if(!credMap.get("email").equals("")){
                    logger.info("User with email " + email + " exists in the database");
                    return true;
                }
            }
            
        } catch(NullPointerException e){
            logger.error("The credential map returned empty: " + e.toString());
        }
        return false;
    }

    //Registers a new user
    public boolean registerNewUser(String email, String username, String pw){
        AuthDAO dao = new AuthDAO();
        boolean isRegistered = false;
        
        //Before registering, hash the password
        try{
            byte[] salt = generateSalt();
            String saltString = bytesToHex(salt);
            String saltedPw = pw + saltString;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPw.getBytes());
            String hashedPw = bytesToHex(hashedBytes);
            if(CommonUtil.isEmpty(hashedPw)){
                logger.error("Hashing of password failed, returning false");
            }

            //Prepare default profile picture before calling DAO method
            Resource imgResource = resourceLoader.getResource("classpath:static/treble-clef.png");
            byte[] imageData = Files.readAllBytes(imgResource.getFile().toPath());

            Image image = new Image();
            image.setData(imageData);
            isRegistered = dao.registerNewUser(email, username, hashedPw, saltString, image.getData());

        } catch(Exception e){
            logger.error("Error with registering new user: " + e.toString());
        }

        return isRegistered;
        
    }

    //Generate random salt for the hashed pw
    private byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

     // Function to convert byte array to hexadecimal string
     private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    //Logs authentication attempt in db
    public boolean logAuthAttempt(String email, boolean valid){
        AuthDAO dao = new AuthDAO();
        boolean userExists = lookupExistingUser(email); //Check if user exists
        boolean res = dao.logAuthAttempt(email, valid, userExists);

        return res;


    }

    //Generates an email verification token
    public boolean generateEmailVerificationToken(String email){
        boolean res = false; 

        AuthDAO dao = new AuthDAO();
        try{
            boolean tokenGenerated = dao.generateEmailVerificationToken(email);
            if(tokenGenerated){
                logger.info("Token is generated, proceeding to send email");
                String uid = dao.getVerificationToken(email);
                if(!CommonUtil.isEmpty(uid)){
                    res = true;
                }
            }
        } catch(SQLException e){
            logger.error(e.toString());
        }
        
        return res;
    }

    //Sends verification email to a user if the email verification details return populated
    public EmailVerificationDetail sendVerificationEmail(String email){
        AuthDAO dao = new AuthDAO();
        EmailVerificationDetail evd = dao.getEmailVerificationDetail(email);
        if(evd != null){
            MailUtil.sendRegistraionEmail(evd);
        }
        return evd;
    }

    //Completes registration if uid matches and is within timerange
    public EmailVerificationDetail completeRegistration(String uid){
        AuthDAO dao = new AuthDAO();
        EmailVerificationDetail evd = dao.getEmailToken(uid);
        logger.info(evd.toString());
        if(evd != null){
            if(!uid.equals(evd.getUid())){
                logger.error("The uids do not match");
                return null;
            } 
            if(!CommonUtil.tokenIsValid(evd.getExpiresBy())){
                logger.error("The timestamp has expired");
                return null;
            }

            //Both potential error condition have passed, now we can update
            //the user's verification status!
            boolean verificationUpdated = verificationStatusUpdated(evd.getEmail());
            if(!verificationUpdated) return null;
        }

        return evd;
    }

    //After registration status is updated, remove current record
    public boolean removeEmailVerificationRecord(String uid){
        AuthDAO dao = new AuthDAO();
        return dao.removeEmailVerificationRecord(uid);
    }

    //Updates the newly registered user's verification status
    private boolean verificationStatusUpdated(String email){
        AuthDAO dao = new AuthDAO();
        return dao.updateUserVerificationStatus(email);
    }

    //Checks user verification status
    public String checkUserVerificationStatus(String email){
        AuthDAO dao = new AuthDAO();
        return dao.checkUserVerificationStatus(email);
    }

    //Checks if a username is taken in the db
    public boolean checkIfUsernameTaken(String username){
        AuthDAO dao = new AuthDAO();
        return dao.checkIfUsernameTaken(username);
    }

    //Update a user's username
    public boolean updateUsername(String id, String username){
        //Check that the username is <= 16 characters
        if(username.length() > 16){
            logger.error("The length of the new username exceeds the 16 character limit");
            return false;
        }
        
        AuthDAO dao = new AuthDAO();
        return dao.updateUsername(id, username);
    }
}
