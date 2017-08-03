package com.nfa.capapp.service.messaging.serial;

import jssc.SerialPortException;

/**
 * Created by cory on 2/3/16.
 */
public interface ISerialPortConnectionClient {
    void close() throws SerialPortException;

    void connect(String portId) throws SerialPortException;

    Boolean isConnected();
}
