package com.atik_faysal.diualumni.models;

public class ChatModel
{
    private String personId,personName,message,counter,imageUrl;

    public ChatModel(String value1,String value2,String value3,String value4,String value5)
    {
        this.personId = value1;
        this.personName = value2;
        this.message = value3;
        this.counter = value4;
        this.imageUrl = value5;
    }


    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
