package com.erikalves.jmsapp.jms;

import com.erikalves.jmsapp.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebJmsProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebJmsProducer.class);


    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${jms.queue.destination}")
    String destinationQueue;

    public void send(String msg) throws ServiceException {
        LOGGER.debug("=========================================  PRODUCE/SEND ======================================= >>>> "+msg);

        if(null == msg || msg.length() ==0){
            throw new ServiceException("Empty message to be produced");
        }


        jmsTemplate.convertAndSend(destinationQueue, msg);
    }
}