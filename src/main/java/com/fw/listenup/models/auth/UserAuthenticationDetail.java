package com.fw.listenup.models.auth;

//POJO class for encapsulating 
public class UserAuthenticationDetail {
    private String email;
    private String password;

    public UserAuthenticationDetail(String mEmail, String mPassword){
        this.email = mEmail;
        this.password = mPassword;
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

}
