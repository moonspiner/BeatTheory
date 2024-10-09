package com.fw.beattheory.services;

import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fw.beattheory.dao.AuthDAO;
import com.fw.beattheory.entity.Image;
import com.fw.beattheory.models.auth.EmailVerificationDetail;
import com.fw.beattheory.models.auth.PasswordVerificationDetail;
import com.fw.beattheory.models.auth.RegistrationLookupDetail;
import com.fw.beattheory.models.auth.UserAuthenticationDetail;
import com.fw.beattheory.util.CommonUtil;
import com.fw.beattheory.util.MailUtil;

import ch.qos.logback.classic.Logger;

//Service class responsible for handling auth logic and proxying controller and dao
@Service
public class AuthService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthService.class);
    private final AuthDAO dao;
    
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    public AuthService(AuthDAO dao){
        this.dao = dao;
    }

    //Test connection to the db
    public boolean testConnection(){
        return dao.testConnection();
    }

    //Retrieves authentication details for login attempt
    public UserAuthenticationDetail getUserAuthenticationDetail(String email){
        return dao.getUserAuthenticationDetail(email);
    }

    //Lookups pre-existing user
    public RegistrationLookupDetail lookupExistingUser(String email, String username){
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
        boolean userExists = lookupExistingUser(email); //Check if user exists
        boolean res = dao.logAuthAttempt(email, valid, userExists);

        return res;
    }

    //Generates an email verification token
    public boolean generateEmailVerificationToken(String email){
        boolean res = false;   
        try{
            boolean tokenGenerated = dao.generateEmailVerificationToken(email);
            if(tokenGenerated){
                logger.info("Token is generated, proceeding to send account verification email");
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
        EmailVerificationDetail evd = dao.getEmailVerificationDetail(email);
        if(evd != null){
            mailUtil.sendRegistraionEmail(evd);
        }
        return evd;
    }

    //Generates a password reset email verification token
    public boolean generatePasswordResetEmailVerificationToken(String email){
        boolean res = false;
        try{
            boolean tokenGenerated = dao.generatePasswordResetEmailVerificationToken(email);
            if(tokenGenerated){
                logger.info("Token is generated, proceeding to send password reset email");
                String uid = dao.getPasswordResetVerificationToken(email);
                if(!CommonUtil.isEmpty(uid)){
                    res = true;
                }
            }
        } catch(SQLException e){
            logger.error(e.toString());
        }
        return res;
    }

    //Send a password reset email to the specified user
    public boolean sendPasswordResetEmail(String email){
        boolean mailSent = false;
        PasswordVerificationDetail pvd = dao.getPasswordVerificationDetail(email);
        if(pvd != null){
            //Password verification detail exists, proceed to send email
            mailSent = mailUtil.sendPasswordResetEmail(pvd);
        }

        return mailSent;
    }

    //Completes registration if uid matches and is within timerange
    public EmailVerificationDetail completeRegistration(String uid){
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
        return dao.removeEmailVerificationRecord(uid);
    }

    //Updates the newly registered user's verification status
    private boolean verificationStatusUpdated(String email){  
        return dao.updateUserVerificationStatus(email);
    }

    //Checks user verification status
    public String checkUserVerificationStatus(String email){
        return dao.checkUserVerificationStatus(email);
    }

    //Checks if a username is taken in the db
    public boolean checkIfUsernameTaken(String username){
        return dao.checkIfUsernameTaken(username);
    }

    //Update a user's username
    public boolean updateUsername(String id, String username){
        //Check that the username is <= 16 characters
        if(username.length() > 16){
            logger.error("The length of the new username exceeds the 16 character limit");
            return false;
        }
        
        return dao.updateUsername(id, username);
    }

    //Update a user's password
    public boolean updatePassword(String token, String pw){
        //Verify that the token is valid
        if(CommonUtil.isEmpty(token)){
            logger.error("Token is empty, password cannot be updated...");
            return false;
        }

        //Look for matching record in password reset table
        PasswordVerificationDetail pvd = dao.getPasswordVerificationRecord(token); 
        if(pvd != null){
            logger.info("Password verification record is populated");
            Timestamp expiresBy = pvd.getExpiresBy();
            long expiresByToUnix = expiresBy.getTime();
            long currentTime = System.currentTimeMillis();

            //Check if the password reset link has expired
            if(currentTime > expiresByToUnix){
                logger.error("Password reset token has expired");
                return false;
            }

            try{
                //Token is good, proceed to update the password
                //Hash and salt the password
                byte[] salt = generateSalt();
                String saltString = bytesToHex(salt);
                String saltedPw = pw + saltString;
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashedBytes = md.digest(saltedPw.getBytes());
                String hashedPw = bytesToHex(hashedBytes);

                //Call update statement in the database
                String email = pvd.getEmail();
                boolean isUpdated = dao.updatePassword(email, hashedPw, saltString);
                if(!isUpdated) logger.error("There was an error with updating the new password in the database");
                return isUpdated;
            } catch(NoSuchAlgorithmException e){
                logger.error("There was an error with hashing the new password: " + e.toString());
                return false;
            }
            
        }

        //Password verification object is null, returning false
        logger.error("Password verification record is null");
        return false;
    }

    //Update user's password given their id
    public boolean updatePasswordWithID(String id, String pw){
        try{
            //Hash and salt the password
            byte[] salt = generateSalt();
            String saltString = bytesToHex(salt);
            String saltedPw = pw + saltString;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPw.getBytes());
            String hashedPw = bytesToHex(hashedBytes);

            //Call update statement in the database
            boolean isUpdated = dao.updatePasswordWithID(id, hashedPw, saltString);
            if(!isUpdated) logger.error("There was an error with updating the new password in the database");
            return isUpdated;
        } catch(NoSuchAlgorithmException e){
            logger.error("There was an error with hashing the new password: " + e.toString());
            return false;
        }
    }

    //Verify user's email
    public boolean verifyUserEmail(String id){
        boolean verificationSuccessful = dao.verifyUserEmail(id);
        return verificationSuccessful;
    }

    //Delete the old password reset token
    public boolean deletePassworeResetToken(String token){
        return dao.deletePasswordResetEmailTokenAfterSuccess(token);
    }

    //Attempt to authenticate a user for admin access
    public ArrayList<String> authenticateAdmin(String username){
        int isAdmin = dao.adminAuthenticate(username);
        if(isAdmin == 1){
            //User is marked as an admin, retrieve password from db and return
            logger.info("User is an admin, returning hashed pw and salt");
            return dao.getPasswordDetails(username);
        }
        logger.info("User is not an admin, returning empty string...");
        return null;
    }
}
