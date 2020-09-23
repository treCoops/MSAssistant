package com.example.msassistant.Model;

public class RegistrationModel {

    String userName;
    String lastName;
    String emailAddress;
    String imageUrl;
    int phoneNumber;
    String uid;

    public RegistrationModel(String userName, String lastName, String emailAddress, String imageUrl, int phoneNumber, String uid) {
        this.userName = userName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
    }

    public RegistrationModel(){}

    public String getUserName() {
        return userName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getUid() {
        return uid;
    }
}
