package com.example.society_app.models;

public class ModelGroup {
    String message,sender,timeStamp,type;

    public ModelGroup() {
    }

    public ModelGroup(String message, String sender, String timeStamp, String type) {
        this.message = message;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
