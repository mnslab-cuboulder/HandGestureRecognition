package com.nfa.capapp.service.messaging.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cory on 3/6/16.
 */
@Component
public class CommandOutLoggingConsumer implements ICommandOutListConsumer, MessageListener {
    private List<String> commandList;
    private ObjectMapper mapper;

    public CommandOutLoggingConsumer() {
        commandList = Collections.synchronizedList(new ArrayList<>());
        mapper = new ObjectMapper();
    }

    public void onMessage(Message message) {
        commandList.add(new String(message.getBody()));
    }

    public String getCommandList(Integer fromIndex) throws JsonProcessingException {
        String list;
        if(fromIndex < 0 || fromIndex > commandList.size()) {
            list = "[]";
        } else {
            list = convertToJsonString(fromIndex);
        }
        return list;
    }

    public void clearList(){
        commandList.clear();
    }

    public void add(String message) {
        commandList.add(message);
    }

    private synchronized String convertToJsonString(Integer fromIndex) throws JsonProcessingException {
        return mapper.writeValueAsString(commandList.subList(fromIndex, commandList.size()));
    }
}
