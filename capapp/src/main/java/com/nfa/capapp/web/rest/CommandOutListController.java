package com.nfa.capapp.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nfa.capapp.service.messaging.commands.ICommandOutListConsumer;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cory on 3/6/16.
 */

@RestController
@RequestMapping(value = "/api/commandList", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommandOutListController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CommandOutListController.class);

    private ICommandOutListConsumer commandOutLoggingConsumer;

    @RequestMapping(method = RequestMethod.GET, params = {"startIndex"})
    public ResponseEntity<String> getCommandList(
        @RequestParam(value = "startIndex") Integer startIndex) throws JsonProcessingException {
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(commandOutLoggingConsumer.getCommandList(startIndex), status);
    }

    public void setCommandOutLoggingConsumer(ICommandOutListConsumer commandOutLoggingConsumer) {
        this.commandOutLoggingConsumer = commandOutLoggingConsumer;
    }
}
