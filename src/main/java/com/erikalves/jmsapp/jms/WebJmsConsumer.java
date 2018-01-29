package com.erikalves.jmsapp.jms;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebJmsConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebJmsConsumer.class);

    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${jms.queue.destination}")
    String destinationQueue;

    public String receive(){
        String str = (String)jmsTemplate.receiveAndConvert(destinationQueue);
        LOGGER.debug("=========================================  RECEIVE/CONSUME ======================================= >>>> "+str);
        return str;
    }
}
