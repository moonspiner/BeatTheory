package com.fw.listenup.models.auth;

//POJO class for encapsulating 
public class UserAuthenticationDetail {
    private int userId;
    private String email;
    private String username;
    private String password;
    private String salt;

    public UserAuthenticationDetail(int mUserId, String mEmail, String mUsername, String mPassword, String mSalt){
        this.userId = mUserId;
        this.email = mEmail;
        this.username = mUsername;
        this.password = mPassword;
        this.salt = mSalt;
    }

    public int getUserId(){
        return this.userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt(){
        return this.salt;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }


}
