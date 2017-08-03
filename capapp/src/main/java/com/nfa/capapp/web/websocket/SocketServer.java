package com.nfa.capapp.web.websocket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.nfa.capapp.service.messaging.serial.GestureMessage;
import com.nfa.capapp.service.messaging.serial.SerialEvent;
import com.nfa.capapp.service.messaging.serial.SerialMessage.MessageType;
import com.nfa.capapp.service.messaging.serial.SerialMessage;

@Component
public class SocketServer extends WebSocketServer implements ApplicationListener<SerialEvent> {
    private static final Logger log = LoggerFactory.getLogger(SocketServer.class);
    private int i = 0;

    public SocketServer() throws UnknownHostException {
        super( new InetSocketAddress(8765));
        this.start();
    }

    public SocketServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public SocketServer( InetSocketAddress address ) {
        super( address );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {

    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
    }

    @Override
    public void onApplicationEvent(SerialEvent event) {
        if (event.getSource().getClass() == SerialMessage.class) {
        	SerialMessage message = (SerialMessage) event.getSource();

            log.info("Socket server received: " + message.toString());

            switch (message.getMessageType()) {
            case ECHO:
            	// Do this less badly
                sendToAll("{ \"type\": \"echo\" }");
                break;
            case TRAINING_COMPLETE:
                sendToAll("{ \"type\": \"trainingcomplete\" }");
                break;
			case BASELINE:
				break;
			case DATA:
                if (i==100) {
                    i = 0;
                    log.info("===============================100 data===============================");
                } else 
                    log.info("Count gesture =" + i++);
				break;
			case STOP:
				break;
			default:
				break;
            }
        } else if (event.getSource().getClass() == GestureMessage.class) {
        	GestureMessage message = (GestureMessage) event.getSource();
        	sendToAll("{ \"type\": \"prediction\", \"data\": " + message.getGestureNumber() + " }");
        }
    	
    	
    }

    public void sendToAll( String text ) {
        Collection<WebSocket> con = connections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send( text );
            }
        }
    }
}
