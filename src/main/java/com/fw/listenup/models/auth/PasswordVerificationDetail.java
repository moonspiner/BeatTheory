package com.fw.listenup.models.auth;

//Model class for encapsulating a password verification attempt record
public class PasswordVerificationDetail {
    private String email;
    private String uid;
    
    public PasswordVerificationDetail(String mEmail, String mUid){
        this.email = mEmail;
        this.uid = mUid;
    }


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    @Override
    public String toString() {
        return "{" +
            " email='" + getEmail() + "'" +
            ", uid='" + getUid() + "'" +
            "}";
    }

}
