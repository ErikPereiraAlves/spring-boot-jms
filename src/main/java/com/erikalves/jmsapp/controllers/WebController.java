package com.erikalves.jmsapp.controllers;

import com.erikalves.jmsapp.exceptions.ServiceException;
import com.erikalves.jmsapp.jms.JmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    @Autowired
    JmsClient jsmClient;

    //ex: http://localhost:8080/produce?msg=hello
    @RequestMapping(value="/produce")
    public String produce(@RequestParam("msg")String msg){
        String str = "Done";
        LOGGER.debug("[WebController] ======= send/produce call =========  {} ",msg);
        try {
            jsmClient.send(msg);
        } catch (ServiceException e) {
            LOGGER.debug("Something went wrong with message producing {} ",e);
            str="message not consumed";
        }
        return str;
    }


    //ex: http://localhost:8080/receive
    @RequestMapping(value="/receive")
    public String receive(){
        String str = null;
        try {
            str = jsmClient.receive();
        } catch (ServiceException e) {
            LOGGER.debug("Something went wrong with message receiving/consuming {} ",e);
        }
        LOGGER.debug("[WebController] ======= receive/consume call ========= {}",str);
        return str;
    }
}