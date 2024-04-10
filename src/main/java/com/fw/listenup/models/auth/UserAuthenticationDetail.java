package com.fw.listenup.models.auth;

//POJO class for encapsulating 
public class UserAuthenticationDetail {
    private String email;
    private String password;
    private String salt;

    public UserAuthenticationDetail(String mEmail, String mPassword, String mSalt){
        this.email = mEmail;
        this.password = mPassword;
        this.salt = mSalt;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
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
