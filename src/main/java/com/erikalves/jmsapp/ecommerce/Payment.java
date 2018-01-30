package com.erikalves.jmsapp.ecommerce;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * The Payment synchronously receives the product order from the Inventory and
 * randomly responds with the number ordered.
 */
public class Payment implements Runnable {
	private String url;
	private String user;
	private String password;
	private final String QUEUE;
	
	public Payment(String queue, String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	    this.QUEUE = queue;
	}
	
	public void run() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
		Session session = null;
		Destination paymentQueue;
		try {
			Connection connection = connectionFactory.createConnection();
			session = connection.createSession(true, Session.SESSION_TRANSACTED);  //synchronous
			paymentQueue = session.createQueue(QUEUE);// PaymentQueue
			MessageConsumer consumer = session.createConsumer(paymentQueue); //consuming PaymentQueue messages from Inventory.
			connection.start();
			
			while (true) {
				Message message = consumer.receive();
				MessageProducer producer = session.createProducer(message.getJMSReplyTo());
				MapMessage paymentMessage;
				if (message instanceof MapMessage) {
					System.out.println("[Payment] Got a MapMessage, of type PaymentQueue, from Inventory, to be consumed.");
					paymentMessage = (MapMessage) message;
				} else {
					// End of Stream
					System.out.println("[Payment] End of stream.");
					producer.send(session.createMessage());
					session.commit();
					producer.close();
					break;
				}
				
				int quantity = paymentMessage.getInt("Quantity");
				String product = paymentMessage.getString("Product");
				System.out.println("Payment: Inventory ordered " + quantity + " Product " + product);
				
				MapMessage outMessage = session.createMapMessage();
				outMessage.setInt("PaymentNumber", paymentMessage.getInt("PaymentNumber"));

				outMessage.setString("Product", product);
				outMessage.setInt("Quantity", quantity);
				
				producer.send(outMessage);
				System.out.println(" Payment: Sent. Quantity:" + quantity + "  product: " + product);
				session.commit();
				System.out.println(" Payment: committed transaction");
				producer.close();
			}
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}

		System.out.println("Good bye Payment thread.");
	}
	

}