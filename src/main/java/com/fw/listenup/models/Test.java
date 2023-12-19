package com.fw.listenup.models;

public class Test {
    private int id;
    private String name;
    private String email;

    public Test(int mId, String mName, String mEmail){
        this.id = mId;
        this.name = mName;
        this.email = mEmail;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
