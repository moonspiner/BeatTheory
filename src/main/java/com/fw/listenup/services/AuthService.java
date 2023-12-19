package com.fw.listenup.services;


import java.util.Map;

import org.springframework.stereotype.Service;

import com.fw.listenup.crypto.SHA256;
import com.fw.listenup.dao.AuthDAO;
import com.fw.listenup.models.auth.RegistrationLookupDetail;
import com.fw.listenup.models.auth.UserAuthenticationDetail;
import com.fw.listenup.util.CommonUtil;

//Service class responsible for handling auth logic and proxying controller and dao
@Service
public class AuthService {

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
                    emailTaken = true;

                }
            }
            if(credMap.containsKey("username")){
                if(!credMap.get("username").equals("")){
                    usernameTaken = true;
                }
            }
        } catch(NullPointerException e){
            System.out.println("The credential map returned empty: " + e.toString());
        }

        RegistrationLookupDetail rld = new RegistrationLookupDetail(emailTaken, usernameTaken);
        return rld;
        
    }

    //Registers a new user
    public boolean registerNewUser(String email, String username, String pw){
        AuthDAO dao = new AuthDAO();

        //Before registering, hash the password:
        String hashedPw = SHA256.hash(pw);
        if(CommonUtil.isEmpty(hashedPw)){
            System.out.println("Hashing of password failed, returning false");
            return false;
        }
        boolean isRegistered = dao.registerNewUser(email, username, hashedPw);
        return isRegistered;
    }
}
