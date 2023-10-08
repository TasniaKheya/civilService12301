package com.example.society_app.models;

public class ModelChat {
    String message,sender,receiver,timeStamp,type;
    boolean isSeen;

    public  ModelChat()
    {

    }

    public ModelChat(String message, String sender, String receiver, String timeStamp, String type, boolean isSeen) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timeStamp = timeStamp;
        this.type = type;
        this.isSeen = isSeen;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
