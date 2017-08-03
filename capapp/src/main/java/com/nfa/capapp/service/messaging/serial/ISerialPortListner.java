package com.nfa.capapp.service.messaging.serial;

import jssc.SerialPort;
import jssc.SerialPortEventListener;

/**
 * Created by cory on 2/3/16.
 */
public interface ISerialPortListner extends SerialPortEventListener {
    public void setSerialPort(SerialPort serialPort);
}
