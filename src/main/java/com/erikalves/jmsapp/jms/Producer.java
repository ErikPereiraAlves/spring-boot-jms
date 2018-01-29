package com.erikalves.jmsapp.jms;


import com.erikalves.jmsapp.controllers.WebController;
import com.erikalves.jmsapp.models.Product;
import com.erikalves.jmsapp.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);


    public void send(JmsTemplate jmsTemplate,String message){

        LOGGER.debug("******************************  Sending/PRODUCING a message containing a new message:  {} ",message);

        jmsTemplate.convertAndSend("PRODUCT-JMS-QUEUE", message);
    }
}
