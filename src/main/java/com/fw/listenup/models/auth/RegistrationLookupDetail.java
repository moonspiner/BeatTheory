package com.fw.listenup.models.auth;

//POJO class that returns information about backend registration attempt
public class RegistrationLookupDetail { 
    private boolean registrationFailed_emailTaken;
    private boolean registrationFailed_usernameTaken;
    


    public RegistrationLookupDetail(boolean registrationFailed_emailTaken, boolean registrationFailed_usernameTaken) {

        this.registrationFailed_emailTaken = registrationFailed_emailTaken;
        this.registrationFailed_usernameTaken = registrationFailed_usernameTaken;
    }
    

    public boolean isRegistrationFailed_emailTaken() {
        return this.registrationFailed_emailTaken;
    }

    public boolean getRegistrationFailed_emailTaken() {
        return this.registrationFailed_emailTaken;
    }

    public void setRegistrationFailed_emailTaken(boolean registrationFailed_emailTaken) {
        this.registrationFailed_emailTaken = registrationFailed_emailTaken;
    }

    public boolean isRegistrationFailed_usernameTaken() {
        return this.registrationFailed_usernameTaken;
    }

    public boolean getRegistrationFailed_usernameTaken() {
        return this.registrationFailed_usernameTaken;
    }

    public void setRegistrationFailed_usernameTaken(boolean registrationFailed_usernameTaken) {
        this.registrationFailed_usernameTaken = registrationFailed_usernameTaken;
    }




}
