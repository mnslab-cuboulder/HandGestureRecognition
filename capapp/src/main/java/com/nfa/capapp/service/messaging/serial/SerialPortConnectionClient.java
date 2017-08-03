package com.nfa.capapp.service.messaging.serial;

import java.util.List;
import java.util.ArrayList;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.nfa.capapp.service.SvmController;

/**
 * Created by cory on 2/2/16.
 */
public class SerialPortConnectionClient {
    private enum Mode {
        ECHO, BASELINE, TRAIN, START, STOP, NONE
    }

    private static final Logger log = LoggerFactory
        .getLogger(SerialPortConnectionClient.class);
    private volatile Mode mode;
    private Integer baudRate;
    private Integer numberOfBits;
    private Integer stopBits;
    private Integer parity;
    private SerialPort serialPort;
    private ISerialPortListner eventListener;
    private List<List<Integer>> trainingData;

    @Autowired
    private SvmController svmController;

    public synchronized void close() throws SerialPortException {
        if (serialPort != null) {
            if (serialPort.isOpened()) {
                serialPort.closePort();
                serialPort = null;
            }
        } else {
            log.error("Cannot close port, it is null");
        }
    }

    public void connect(String portId) throws SerialPortException {
        // Close here to ensure the port is free
        close();

        serialPort = new SerialPort(portId);
        serialPort.openPort();
        log.debug("Baud:" + baudRate + " Bits:" + numberOfBits + " stopbit:" + stopBits + " parity:" + parity);
        serialPort.setParams(baudRate, numberOfBits, stopBits, parity);
        eventListener.setSerialPort(serialPort);
        serialPort.addEventListener(eventListener, getMask());
    }

    public void echo() throws SerialPortException {
        serialPort.writeByte((byte) 0xE0);
    }

    public void train(int gestureId, int iterations) throws SerialPortException {
        byte[] b = new byte[3];
        b[0] = (byte) 0xE2;
        b[1] = (byte) (iterations >> 8);
        b[2] = (byte) (iterations & 0xFF);

        svmController.setCurrentGesture(gestureId);
        svmController.setIterationCount(iterations);
        svmController.setTrainMode();

        serialPort.writeBytes(b);
    }

    public void start() {
        svmController.train();

        try {
            serialPort.writeByte((byte) 0xE3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            serialPort.writeByte((byte) 0xE4);
            svmController.setStopMode();
        } catch (SerialPortException ex) {
            System.err.println("Stop was unsuccessful!");
        }
    }
    
    public void clear() {
		svmController.clearModel();		
	}

    public Mode getmode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Boolean isConnected() {
        if(serialPort == null) {
            return false;
        }

        return serialPort.isOpened();
    }

    private int getMask() {
        return SerialPort.MASK_RXCHAR +
            SerialPort.MASK_CTS +
            SerialPort.MASK_DSR;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }


    public void setNumberOfBits(Integer numberOfBits) {
        this.numberOfBits = numberOfBits;
    }

    public void setBaudRate(Integer baudRate) {
        this.baudRate = baudRate;
    }

    public void setStopBits(Integer stopBits) {
        this.stopBits = stopBits;
    }

    public void setParity(Integer parity) {
        this.parity = parity;
    }

    public void setEventListener(ISerialPortListner eventListener) {
        this.eventListener = eventListener;
    }	
}
