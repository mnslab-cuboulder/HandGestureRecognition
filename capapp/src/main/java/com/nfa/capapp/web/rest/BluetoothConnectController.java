package com.nfa.capapp.web.rest;

import com.nfa.capapp.service.PortScanner;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by cory on 1/30/16.
 */
@RestController
@RequestMapping(value = "/api/bluetoothConnect", produces = MediaType.APPLICATION_JSON_VALUE)
public class BluetoothConnectController {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BluetoothConnectController.class);

    @Autowired
    private PortScanner portCom;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public List<String> getConnectedPorts() {
        log.info("GET /api/bluetoothConnect get connected ports");
        return portCom.getConnectedPorts();
    }
}
