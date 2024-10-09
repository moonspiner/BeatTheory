package com.fw.beattheory.models.auth;

import java.sql.Timestamp;

public class EmailVerificationDetail {
    private String email;
    private String username;
    private String uid;
    private Timestamp expiresBy;


    public EmailVerificationDetail(String email, String username, String uid) {
        this.email = email;
        this.username = username;
        this.uid = uid;
    }

    public EmailVerificationDetail(String email, String uid, Timestamp expiresBy){
        this.email = email;
        this.uid = uid;
        this.expiresBy = expiresBy;
    }


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
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


}
