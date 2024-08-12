package com.fw.listenup.models.user;

//Model class used for listing records in admin console
public class UserRecord {
    private int id;
    private String username;
    private String email;
    private int verificationStatus;

    public UserRecord(int id, String username, String email, int verificationStatus){
        this.id = id;
        this.username = username;
        this.email = email;
        this.verificationStatus = verificationStatus;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getVerificationStatus() {
        return this.verificationStatus;
    }

    public void setVerificationStatus(int verificationStatus) {
        this.verificationStatus = verificationStatus;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            ", verificationStatus='" + getVerificationStatus() + "'" +
            "}";
    }

}
