package com.nfa.capapp.service.messaging.serial;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.nfa.capapp.service.messaging.serial.SerialMessage.MessageType;

//import com.nfa.capapp.service.messaging.serial.SerialMessage.MessageType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Created by cory on 2/2/16.
 */
@Component
class SerialPortListener implements ISerialPortListner {
	
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SerialPortListener.class);
    private SerialPort serialPort;
	private LinkedList<Byte> queue;
	ApplicationEventPublisher publisher;

    @Autowired
    public SerialPortListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        this.queue = new LinkedList<Byte>();
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }    

    @Override
    public void serialEvent(SerialPortEvent event) {
        //TODO: get event value from serial port config file
        if (event.isRXCHAR()) {
            if (event.getEventValue() > 0) {
                try {
                    byte buffer[] = serialPort.readBytes();
                    
					for (int i = 0; i < buffer.length; ++i) {
						queue.add(buffer[i]);
					}
					
					processQueue();
					
                } catch (SerialPortException ex) {
                    log.error("Error reading bytes from serial port: " + ex.getMessage(), ex);
                }
            }
        }
    }
    
    private void processQueue() {
    	ListIterator<Byte> iter = queue.listIterator();
    	
    	if (findHead(iter)) {
    		// we have sensor data
    		if (processDataFrame(iter)) {
	    		processQueue();
	    	}
    		
    	} else {
    		// we have something else (or only the first byte of head)
    		SerialMessage message = new SerialMessage();
    		
    		if (queue.size() == 0) return;
    		
    		boolean command = false;
    		switch (queue.getLast() & 0xFF) {
    		case 0xE0:
    			message.setMessageType(MessageType.ECHO);
    			command = true;
    			break;
    		case 0xE1:
    			// possibly deprecated
    			message.setMessageType(MessageType.BASELINE);
    			command = true;
    			break;
    		case 0xE4:
    			message.setMessageType(MessageType.STOP);
    			command = true;
    			break;
			default:
    			// bad news, looks like bad data
				return;
    		}
    		
    		if (command) queue.clear();
    		publisher.publishEvent(new SerialEvent(message));
    	}
    }
    
	private boolean findHead(ListIterator<Byte> iter) {
		boolean found1 = false;
		
		while (iter.hasNext()) {
			int next = iter.next() & 0xFF;
			
			if (found1 && next == 0xAA) {
				return true;
			}
			
			found1 = (next == 0x55);
		}

		return false;
	}
	
	private boolean processDataFrame(ListIterator<Byte> iter) {
		
		try {

			int checksum = 0x55 + 0xAA;
			byte curByte;
			
			// Temporary testing
			List<Byte> byteList = new ArrayList<Byte>();
			
			// Read data length
			curByte = iter.next();
			byteList.add(curByte);
			checksum += (curByte & 0xFF);
			int dataLength = (curByte & 0xFF);

			int sensorVal = 0;
			List<Integer> data = new ArrayList<Integer>();

			// Read sensor values
			for (int j = 0; j < dataLength - 1; ++j) {
				curByte = iter.next();
				byteList.add(curByte);
				checksum += (curByte & 0xFF);

				switch (j % 3) {
				case 0:
					// Sensor number
					data.add(curByte & 0xFF);
					break;
				case 1:
					// Sensor value high byte
					sensorVal = (curByte & 0xFF) << 8;
					break;
				case 2:
					// Sensor value low byte
					sensorVal += (curByte & 0xFF);
					data.add(sensorVal);
					break;
				}
			}

			// Read checksum
			curByte = iter.next();
			byteList.add(curByte);
			if ((checksum & 0xFF) != (curByte & 0xFF)) {
				
				// Checksum doesn't match
				System.out.println("Checksum: " + (checksum & 0xFF)
						+ " does not match received checksum: "
						+ (curByte & 0xFF));
				for (int k = 0; k < byteList.size(); ++k) {
					System.out.print((byteList.get(k) & 0xFF) + ", ");
				}
				System.out.println();
				
			} else {
				// Checksum does match, message out
				SerialMessage message = new SerialMessage();
				message.setMessageType(MessageType.DATA);
				message.setData(data);
				
				publisher.publishEvent(new SerialEvent(message));
			}
			
			trimQueue(iter);
			return true;
		} catch (NoSuchElementException e) {
			// Data frame incomplete, try again next time
			return false;
		}
	}
	
	private void trimQueue(ListIterator<Byte> iter) {
		// Next index returns size if it's already at the end
		// Take care of that
		int next = iter.hasNext() ? iter.nextIndex() - 1 : iter.nextIndex();
		queue.subList(0, next).clear();
	}
}
