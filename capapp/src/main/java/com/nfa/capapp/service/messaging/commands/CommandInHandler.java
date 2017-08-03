package com.nfa.capapp.service.messaging.commands;

import org.springframework.stereotype.Component;

/**
 * Created by cory on 2/6/16.
 */
@Component
public class CommandInHandler {
    public void handleMessage(CommandMessage commandMessage) {
        System.out.println("Received command in message: " + commandMessage.getCommand());
        System.out.println("Sender: " + commandMessage.getSenderName());
    }
}
