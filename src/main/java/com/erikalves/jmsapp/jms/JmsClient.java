package com.erikalves.jmsapp.jms;

import com.erikalves.jmsapp.exceptions.ServiceException;

public interface JmsClient {

    public void send(String msg) throws ServiceException;
    public String receive() throws ServiceException;


}
