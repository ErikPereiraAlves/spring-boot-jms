package com.erikalves.jmsapp.ecommerce;

import com.erikalves.jmsapp.jms.Producer;
import com.erikalves.jmsapp.models.Product;
import com.erikalves.jmsapp.utils.JsonUtil;
import com.google.gson.Gson;
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
    private Product product;
    private String productJson;

    public Order(String url, String user, String password,Product product) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.product = product;
        this.productJson = JsonUtil.JsonConvert(product);
    }

    public void run() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        try {
            Connection connection = connectionFactory.createConnection();

            // The Order's session is non-trasacted.
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); //non transacted.
            Destination inventoryQueue = session.createQueue("InventoryQueue");

            //Creating temporary destinations: You can create temporary destinations that last only for the duration of the connection in which they are created.
            TemporaryQueue orderConfirmQueue = session.createTemporaryQueue();

            MessageProducer producer = session.createProducer(inventoryQueue); //producer - send request to inventory
            MessageConsumer replyConsumer = session.createConsumer(orderConfirmQueue); //consumer - get response back from inventory

            connection.start();

            MapMessage message = session.createMapMessage();

            message.setString("Product", productJson);
            int quantity = 1;
            message.setInt("Quantity", quantity);
            message.setJMSReplyTo(orderConfirmQueue);
            producer.send(message);
            System.out.println("[Order] Order sent to inventory system " + quantity + " product's json " + productJson);

            MapMessage reply = (MapMessage) replyConsumer.receive();
            if (reply.getBoolean("PaymentAccepted")) {
                System.out.println("[Order] Order Filled as inventory got the payment and itwas accepted");
            } else {
                System.out.println("[Order]Order Not Filled, payment was not accepted according of inventory system");
            }

            // Send a non-MapMessage to signal the end of monitoring queue. Send it to Inventory. Inventory will send another simple Message to Payment as well.
            System.out.println("[Order] Send a END OF STREAM message to Inventory.");
            producer.send(session.createMessage());

            replyConsumer.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

        System.out.println("[Order] Good bye Order thread.");
    }

}