package com.fw.beattheory.models.auth;

import java.sql.Timestamp;

//Model class for encapsulating a password verification attempt record
public class PasswordVerificationDetail {
    private String email;
    private String uid;
    private Timestamp expiresBy;

    
    public PasswordVerificationDetail(String mEmail, String mUid, Timestamp mTimestamp){
        this.email = mEmail;
        this.uid = mUid;
        this.expiresBy = mTimestamp;
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

    public Timestamp getExpiresBy() {
        return this.expiresBy;
    }

    public void setExpiresBy(Timestamp expiresBy) {
        this.expiresBy = expiresBy;
    }


    @Override
    public String toString() {
        return "{" +
            " email='" + getEmail() + "'" +
            ", uid='" + getUid() + "'" +
            ", expiresBy='" + getExpiresBy() + "'" +
            "}";
    }
}
