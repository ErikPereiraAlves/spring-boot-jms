package com.erikalves.jmsapp;

import com.erikalves.jmsapp.jms.Producer;
import com.erikalves.jmsapp.models.Product;
import com.erikalves.jmsapp.utils.DateUtils;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@EnableJms
public class Application {



    @Bean
    public JmsListenerContainerFactory<?> jmsAppFactory(ConnectionFactory connectionFactory,
                                                        DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        return factory;
    }


    public static void main(String[] args) {

        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        //fire a test JMS
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

        Product product =  new Product("1", "Mentos", "Mentos powermings", "Mentos powermings", "google.com/mentos", 1.5, DateUtils.getCurrentTimestamp(), DateUtils.getCurrentTimestamp() );

        Producer producer = new Producer();
        String json = new Gson().toJson(product);

        producer.send(jmsTemplate,json);
}

}