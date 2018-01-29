package com.erikalves.jmsapp.jms;

import com.erikalves.jmsapp.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JmsClientImpl implements JmsClient{

    @Autowired
    WebJmsConsumer jmsConsumer;

    @Autowired
    WebJmsProducer jmsProducer;

    @Override
    public void send(String msg) throws ServiceException {
        jmsProducer.send(msg);
    }

    @Override
    public String receive() {
        return jmsConsumer.receive();
    }

}