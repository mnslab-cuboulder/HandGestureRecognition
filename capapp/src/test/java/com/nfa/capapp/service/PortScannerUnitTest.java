package com.nfa.capapp.service;

import org.junit.Before;
import org.junit.Test;

import javax.bluetooth.BluetoothStateException;
import java.util.List;

/**
 * Created by cory on 1/30/16.
 */
public class PortScannerUnitTest {

    private PortScanner portScanner;

    @Before
    public void setup() {
        portScanner = new PortScanner();
    }

    @Test
    public void testGetConnectedPorts() throws BluetoothStateException {
        List<String> ports = portScanner.getConnectedPorts();
        System.out.println(ports);
    }

    @Test
    public void testGetPortNames() throws BluetoothStateException {
        List<String> ports = portScanner.getConnectedPorts("tty.HC-05*");
        System.out.println(ports);
    }


}

