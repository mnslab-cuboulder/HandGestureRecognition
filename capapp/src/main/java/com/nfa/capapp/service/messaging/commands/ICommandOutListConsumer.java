package com.nfa.capapp.service.messaging.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.core.Message;

/**
 * Created by cory on 3/6/16.
 */
public interface ICommandOutListConsumer {

    public void onMessage(Message message);

    public void add(String msg);

    public void clearList();

    public String getCommandList(Integer fromIndex) throws JsonProcessingException;

}
