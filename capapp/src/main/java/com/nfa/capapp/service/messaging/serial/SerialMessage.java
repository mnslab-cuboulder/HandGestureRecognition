package com.nfa.capapp.service.messaging.serial;

import java.util.List;

/**
 * Created by cory on 2/2/16.
 */
public class SerialMessage {
    public enum MessageType {
        ECHO, BASELINE, DATA, STOP, TRAINING_COMPLETE;
    }

    private MessageType messageType;
    private List<Integer> data;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public List<Integer> getData() {
        return data;
    }
    
    public void setData(List<Integer> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MessageType: " + messageType.toString() + (data == null ? "": (", data: " + data.toString()));
    }
}
