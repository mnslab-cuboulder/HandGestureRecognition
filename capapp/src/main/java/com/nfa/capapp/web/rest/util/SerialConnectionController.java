package com.nfa.capapp.web.rest.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nfa.capapp.service.messaging.serial.SerialPortConnectionClient;

import jssc.SerialPortException;

/**
 * Created by cory on 2/6/16.
 */
@RestController
@RequestMapping(value = "/api/serialPort", produces = MediaType.APPLICATION_JSON_VALUE)
public class SerialConnectionController {
    private static final Logger log = LoggerFactory.getLogger(SerialConnectionController.class);
    private final ApplicationEventPublisher publisher;

    @Autowired
    private SerialPortConnectionClient serialPortConnectionClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public SerialConnectionController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @RequestMapping(method = RequestMethod.PUT, params = {"portName"})
    public ResponseEntity<JSONObject> connect(
                                              @RequestParam(value = "portName") String portName) throws JSONException {
        String message;
        HttpStatus status;

        boolean success = false;
        try {
            if(serialPortConnectionClient == null) {
                message = "Cannot connect to port, connection client is null";
                status = HttpStatus.CONFLICT;

                // Can we just disconnect if already connected instead?
                // } else if(serialPortConnectionClient.isConnected()) {
                //   message = "Cannot connect to port, already connected. Please disconnect";
                //   status = HttpStatus.CONFLICT;
            } else {
                serialPortConnectionClient.connect(portName);
                success = true;
                status = HttpStatus.CREATED;
                message = "Connected to port: " + portName;
            }
        } catch (SerialPortException e) {
            message = "Could not connect to port: " + portName + "\n" + e.getMessage();
            log.error(message, e);
            status = HttpStatus.CONFLICT;
        }

        JSONObject json = new JSONObject();
        json.put("Success", success);
        json.put("Status", message);
        return new ResponseEntity<>(json, status);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<JSONObject> disconnect() throws JSONException {
        String message;
        HttpStatus status;
        boolean success = false;
        try {
            if(serialPortConnectionClient.isConnected()) {
                serialPortConnectionClient.close();
            }
            success = true;
            status = HttpStatus.CREATED;
            message = "Disconnected from port";
        } catch (SerialPortException e) {
            message = "Could not disconnect from port: ";
            log.error(message + "\nMessage: " + e.getMessage(), e);
            status = HttpStatus.CONFLICT;
        }
        JSONObject json = new JSONObject();
        json.put("Success", success);
        json.put("Status", message);
        return new ResponseEntity<>(json, status);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<JSONObject> connectionStatus() throws JSONException {
        String message;
        HttpStatus status;
        JSONObject json = new JSONObject();
        try {
            json.put("Status", serialPortConnectionClient.isConnected());
            status = HttpStatus.OK;
        } catch (JSONException e) {
            message = "Error retrieving port status: ";
            log.error(message + "\nMessage: " + e.getMessage(), e); e.printStackTrace();
            json.put("Status", message);
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(json, status);
    }

    @RequestMapping(path="/echo", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> sendEcho() throws JSONException {
        String message;
        HttpStatus status;
        boolean success = false;

        try {
            serialPortConnectionClient.echo();
            success = true;
            status = HttpStatus.OK;
            message = "Echo sent successfully";
        } catch (SerialPortException e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Echo failed";
        }

        JSONObject json = new JSONObject();
        json.put("Status", message);
        json.put("Success", success);

        return new ResponseEntity<>(json, status);
    }

    @RequestMapping(path = "/train", method = RequestMethod.POST, params = {"gesture", "count"})
    public ResponseEntity<JSONObject>
        sendTrain(@RequestParam(value = "gesture") int gesture,
                  @RequestParam(value = "count") int count)
        throws JSONException {

        try {
            serialPortConnectionClient.train(gesture, count);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        json.put("message", "Training started.");

        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }

    @RequestMapping(path = "/start", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> start() throws JSONException {
        try {
            serialPortConnectionClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JSONObject json = new JSONObject();
        json.put("message", "Now streaming sweet, hot, training data.");

        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }

    @RequestMapping(path = "/stop", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> stop() throws JSONException {
        try {
            serialPortConnectionClient.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JSONObject json = new JSONObject();
        json.put("message", "No longer streaming sweet, hot, training data.");

        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }
    
    @RequestMapping(path = "/clear", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> clear() throws JSONException {
        try {
            serialPortConnectionClient.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JSONObject json = new JSONObject();
        json.put("message", "Model cleared.");

        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }
}
