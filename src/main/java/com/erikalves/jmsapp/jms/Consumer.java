package com.erikalves.jmsapp.jms;

import com.erikalves.jmsapp.models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
/*
This receiver is known as a message driven POJO
 */
public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    /*
    The JmsListener annotation defines the name of the Destination that this method should listen to
    and
    the reference to the JmsListenerContainerFactory to use to create the underlying message listener container.
     */

    @JmsListener(destination = "PRODUCT-JMS-QUEUE", containerFactory = "jmsAppFactory")
    public void receiveMessage(String message) {
        LOGGER.debug("********************************* Received/Consumed {}  *****************************************",message);
    }


}
