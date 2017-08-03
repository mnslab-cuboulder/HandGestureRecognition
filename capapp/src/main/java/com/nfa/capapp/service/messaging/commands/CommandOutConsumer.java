package com.nfa.capapp.service.messaging.commands;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Created by cory on 2/24/16.
 */
@Component
public class CommandOutConsumer implements MessageListener {

    //is called when command messages are received, intend to use this for logging or web app
    public void onMessage(Message message) {

    }
}
