package com.atik_faysal.diualumni.models;

public class MessageModel
{
    private String sender,receiver,text,time,messageId;

    public MessageModel(String value1,String value2,String value3,String value4,String value5)
    {
        this.messageId = value1;
        this.sender = value2;
        this.receiver = value3;
        this.text = value4;
        this.time = value5;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
