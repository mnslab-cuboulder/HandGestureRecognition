package com.nfa.capapp.service.messaging.commands;

/**
 * Created by cory on 2/7/16.
 */
public class CommandMessage {
    private String command;
    private String senderName;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
