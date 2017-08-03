package com.nfa.capapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nfa.capapp.service.messaging.commands.CommandOutLoggingConsumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by cory on 3/6/16.
 */
public class CommandOutLoggingConsumerTest {

    private CommandOutLoggingConsumer consumer;

    @Before
    public void setup() {
        consumer = new CommandOutLoggingConsumer();
    }

    @Test
    public void testGetCommandListListEmptyZeroIndex() throws JsonProcessingException {
        consumer.clearList();
        Assert.assertEquals(consumer.getCommandList(0), "[]");
    }

    @Test
    public void testGetCommandListEmptyIndexDoesNotExist() throws JsonProcessingException {
        consumer.clearList();
        Assert.assertEquals(consumer.getCommandList(5), "[]");
    }

    @Test
    public void testGetCommandListSlice() throws JsonProcessingException {
        consumer.add("0");
        consumer.add("1");
        consumer.add("2");
        consumer.add("3");
        consumer.add("4");
        consumer.add("5");
        Assert.assertEquals(consumer.getCommandList(2),"[\"2\",\"3\",\"4\",\"5\"]");
    }
}
