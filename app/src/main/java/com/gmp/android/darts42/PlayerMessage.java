package com.gmp.android.darts42;

import java.util.Map;

public class PlayerMessage {

    private Integer messageType;
    private String payload;
    private String sender;
    private Long timestamp;
    ;

    public PlayerMessage() {
    }

    public PlayerMessage(Integer messageType, String payload, String sender, Long timestamp) {

        this.messageType = messageType;
        this.payload = payload;
        this.sender = sender;
        this.timestamp = timestamp;

    }


    public Integer getMessageType()
    {
        return messageType;
    }
    public String getPayload()
    {
        return payload;
    }
    public String getSender()
    {
        return sender;
    }
    public Long getTimestamp() {
        return timestamp;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}