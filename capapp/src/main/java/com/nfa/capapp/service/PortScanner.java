package com.nfa.capapp.service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import jssc.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by cory on 2/2/16.
 */

@Service
public class PortScanner {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PortScanner.class);

    public List<String> getConnectedPorts() {
       List<String> portList = Arrays.asList(SerialPortList.getPortNames());
       logIfDebug(portList, null);
       return portList;
    }

    //TODO: verify window and linux port searches with name prefix will return the correct port, may need to pass in os for specific searches
    public List<String> getConnectedPorts(String regexString) {
//      Integer osTypeEnum = SerialNativeInterface.getOsType();
        List<String> portList = Arrays.asList(SerialPortList.getPortNames(Pattern.compile(regexString)));
        logIfDebug(portList, regexString);
        return portList;
    }

    private void logIfDebug(List<String> portList, String matcherString) {
       if(log.isDebugEnabled()){
           if(portList == null) {
               log.debug("Connected Port List: null\nSearch Term: " + matcherString);
           } else {
               log.debug("Connected Port List: " + portList + "\nSearch Term: " + matcherString);
           }
       }
    }
}


