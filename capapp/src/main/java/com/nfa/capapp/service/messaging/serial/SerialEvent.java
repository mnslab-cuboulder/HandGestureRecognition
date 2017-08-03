package com.nfa.capapp.service.messaging.serial;

import org.springframework.context.ApplicationEvent;

public class SerialEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    public SerialEvent(Object message) {
        super(message);
    }
}
