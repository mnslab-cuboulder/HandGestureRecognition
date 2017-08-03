package com.nfa.capapp.web.rest;

import java.awt.AWTException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nfa.capapp.domain.Key;
import com.nfa.capapp.service.KeyPress;

@RestController
@RequestMapping(value = "/api/keyPress", produces = MediaType.APPLICATION_JSON_VALUE)
public class KeyPressController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(KeyPressController.class);
    
    @Autowired
    private KeyPress keyPress;
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> postKeyPress(@RequestBody Key keyBinding) throws AWTException {
        log.info("POST /api/keyPress post key press");
        keyPress.anyKey(keyBinding.getKeyPress());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "/hold")
    public ResponseEntity<Void> postKeyHold(@RequestBody Key keyBinding) throws AWTException {
        log.info("POST /api/keyPress/hold post key press (hold)");
        keyPress.press(keyBinding.getKeyPress());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, value = "/release")
    public ResponseEntity<Void> postKeyRelease() throws AWTException {
        log.info("POST /api/keyPress/release post key press (hold)");
        keyPress.release();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}