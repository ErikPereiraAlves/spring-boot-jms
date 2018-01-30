package com.erikalves.jmsapp.ecommerce;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

/**
 * The Order orders products from the Inventory by sending a message via
 * the InventoryQueue. It then syncronously receives the reponse message
 * and reports if the order was successful or not.
 */
public class Order implements Runnable {
    private String url;
    private String user;
    private String password;

    public Order(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void run() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        try {
            Connection connection = connectionFactory.createConnection();

            // The Order's session is non-trasacted.
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination inventoryQueue = session.createQueue("InventoryQueue");
            TemporaryQueue orderConfirmQueue = session.createTemporaryQueue();

            MessageProducer producer = session.createProducer(inventoryQueue); //producer - send request to inventory
            MessageConsumer replyConsumer = session.createConsumer(orderConfirmQueue); //consumer - get response back from inventory

            connection.start();


            MapMessage message = session.createMapMessage();
            String product = "iPhone8";
            message.setString("Item", product);
            message.setString("Product", product);
            int quantity = 1;
            message.setInt("Quantity", quantity);
            message.setJMSReplyTo(orderConfirmQueue);
            producer.send(message);
            System.out.println("STEP1 - Order sent to inventory system " + quantity + " product " + product);

            MapMessage reply = (MapMessage) replyConsumer.receive();
            if (reply.getBoolean("PaymentAccepted")) {
                System.out.println("====================  Order: Order Filled as inventory got the payment and itwas accepted ===================");
            } else {
                System.out.println("====================== Order: Order Not Filled, payment was not accepted according of inventory system. ================");
            }


            // Send a non-MapMessage to signal the end
            producer.send(session.createMessage());

            replyConsumer.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

        System.out.println("Good bye Order thread.");
    }

}