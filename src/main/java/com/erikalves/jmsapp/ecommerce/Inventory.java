package com.erikalves.jmsapp.ecommerce;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * The Inventory synchronously, and in a single transaction, receives the
 * order from InventoryQueue and sends messages to the Payment via
 * PaymentQueue
 * * The response is received asynchronously; when the response comes
 * back, the order confirmation message is sent back to the Order class via InventoryQueue.
 */
public class Inventory implements Runnable, MessageListener {
    private String url;
    private String user;
    private String password;
    private Session asyncSession;
    private Object paymentLock = new Object();
    private boolean paid = false;

    public Inventory(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void run() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Session session = null;
        Destination inventoryQueue;
        Destination paymentQueue;
        TemporaryQueue inventoryConfirmQueue;
        MessageConsumer inventoryConsumer = null;
        MessageProducer paymentProducer = null;


        try {
            Connection connection = connectionFactory.createConnection();  //transacted and synchronous connection between Order and Inventory
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            inventoryQueue = session.createQueue("InventoryQueue");  //consumer queue
            paymentQueue = session.createQueue("PaymentQueue");  // producer queue
            // 1- receive message from Order
            inventoryConsumer = session.createConsumer(inventoryQueue); //consume message from Order
            //2 - create a request message to Payment
            paymentProducer = session.createProducer(paymentQueue);  //produce message to payment
            //start inventory-order sync connection
            connection.start();


            //from here on it's assync.
            Connection asyncconnection = connectionFactory.createConnection(); // transacted and assynchronous connection between Inventory and Payment
            asyncSession = asyncconnection.createSession(true, Session.SESSION_TRANSACTED);
            // 3- receive message from Payment
            inventoryConfirmQueue = asyncSession.createTemporaryQueue();
            MessageConsumer confirmConsumer = asyncSession.createConsumer(inventoryConfirmQueue);  // consumer message from Payment - assync - listening
            confirmConsumer.setMessageListener(this);
            //start inventory-payment assync connnection
            asyncconnection.start();


            while (true) {
                Payment payment = null;
                try {
                    Message inMessage = inventoryConsumer.receive();
                    MapMessage message;
                    if (inMessage instanceof MapMessage) {
                        System.out.println("[Inventory] Started Consuming messaging from Order and Producing another towards Payment.");
                        message = (MapMessage) inMessage;

                    } else {
                        // end of stream
                        System.out.println("[Inventory] end of stream - Messaging between Inventory and Payment is concluded. It is also time to reply back to Order.");
                        Message outMessage = session.createMessage();
                        outMessage.setJMSReplyTo(inventoryConfirmQueue);
                        paymentProducer.send(outMessage);
                        session.commit();
                        break;
                    }


                    System.out.println("[Inventory] Creating payment object and producing a MapMessage to Payment class.");
                    payment = new Payment(message);

                    MapMessage paymentMessage = session.createMapMessage();
                    paymentMessage.setJMSReplyTo(inventoryConfirmQueue);
                    paymentMessage.setInt("PaymentNumber", payment.getPaymentNumber());

                    int quantity = message.getInt("Quantity");
                    String product = message.getString("Product");
                    System.out.println("[Inventory] time to request the following product quantity payments.  Quantity = " + quantity + "  Product = " + product);

                    paymentMessage.setInt("Quantity", quantity);
                    paymentMessage.setString("Product", product);
                    paymentProducer.send(paymentMessage);

                    session.commit();
                    System.out.println("[Inventory] Messaging (producer) Committed Payment Class for Transacting");

                } catch (JMSException e) {
                    System.out.println("[Inventory] JMSException Occurred: " + e.getMessage());
                    e.printStackTrace();
                    session.rollback();
                    System.out.println("[Inventory] Rolled Back Payment Transaction.");
                }
            }

            synchronized (paymentLock) {
                while (paid == false) {
                    try {
                        paymentLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            connection.close();
            asyncconnection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("[Inventory]Good bye Inventory thread.");
    }

    public void onMessage(Message message) {

        try {
            System.out.println("[Inventory] onMessage listener. JMS Reply to: " + message.getJMSDestination().toString());
        } catch (JMSException e) {
            e.printStackTrace();
        }


        if (!(message instanceof MapMessage)) {

            synchronized (paymentLock) {
                paid = true;
                paymentLock.notifyAll();
            }
            try {
                asyncSession.commit();
                System.out.println("[Inventory] This is NOT a MapMessage. Time to return from onMessage listener method");
                return;

            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[Inventory] This is a MapMessage to be consumed. Time to produce MapMessage to Payment.");
        }


        int paymentNumber = -1;
        try {
            MapMessage receivedPaymentMessage = (MapMessage) message;

            paymentNumber = receivedPaymentMessage.getInt("PaymentNumber");
            Payment payment = Payment.getPayment(paymentNumber);
            payment.processPaymentMessage(receivedPaymentMessage);
            asyncSession.commit();

            if (!"Pending".equals(payment.getStatus())) {

                MessageProducer replyProducer = asyncSession.createProducer(payment.getPaymentRequestMessage().getJMSReplyTo());
                MapMessage replyMessage = asyncSession.createMapMessage();
                if ("Fulfilled".equals(payment.getStatus())) {
                    replyMessage.setBoolean("PaymentAccepted", true);
                    System.out.println("[Inventory]sent " + payment.quantity + " products(s)" + payment.product + " to payment system.");
                } else {
                    replyMessage.setBoolean("PaymentAccepted", false);
                    System.out.println("[Inventory] unable to send " + payment.quantity + " product(s)" + payment.product + " to payment system.");
                }
                replyProducer.send(replyMessage);
                asyncSession.commit();
                System.out.println("[Inventory] committed payment transaction");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static class Payment {
        private static Map<Integer, Payment> pendingOrders = new HashMap<>();
        private static int nextPaymentNumber = 1;

        private int paymentNumber;
        private int quantity;
        private String product;
        private MapMessage paidCompletedMessage = null;
        private MapMessage paymentRequestMessage;
        private String status;

        public Payment(MapMessage message) {
            this.paymentNumber = nextPaymentNumber++;
            this.paymentRequestMessage = message;
            try {
                this.quantity = message.getInt("Quantity");
                this.product = message.getString("Product");
            } catch (JMSException e) {
                e.printStackTrace();
                this.quantity = 0;
            }
            status = "Pending";
            pendingOrders.put(paymentNumber, this);
        }

        public Object getStatus() {
            return status;
        }

        public int getPaymentNumber() {
            return paymentNumber;
        }

        public static int getOutstandingOrders() {
            return pendingOrders.size();
        }

        public static Payment getPayment(int number) {
            return pendingOrders.get(number);
        }

        public MapMessage getPaymentRequestMessage() {
            return paymentRequestMessage;
        }

        public void processPaymentMessage(MapMessage message) {

            paidCompletedMessage = message;


            if (null != paidCompletedMessage) {
                // Received payment message
                try {
                    if (quantity > paidCompletedMessage.getInt("Quantity")) {
                        status = "Cancelled";

                    } else {
                        status = "Fulfilled";
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                    status = "Cancelled";
                }
            }
        }
    }

}