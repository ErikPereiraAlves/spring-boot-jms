http://localhost:8161/admin/topics.jsp


Example project using Java JMS.

1 - Starter example, where this project evolved from.
https://spring.io/guides/gs/messaging-jms/

----------------------------------------------------------------------------------------

2- Check activeMQ examples
C:\Users\Erik_Pereira_Alves\Downloads\apache-activemq-5.15.2-bin\apache-activemq-5.15.2\examples\openwire\ecommerce\src

----------------------------------------------------------------------------------------
JTA TRANSACTION USING JMS:

Using JTA Transactions with JMS
Transacted sessions enable a JMS producer or consumer to group messages into a single, atomic send or receive. However, a transacted session is only used within JMS. Many applications need JMS and JDBC or EJB work to participate in a single transaction. For instance, an application might dequeue a message containing a customer order and use the order information to update some inventory tables in the database. The order processing and the inventory update must be within the same transaction, so a transacted session is insufficient because it only handles JMS. The application must use a JTA javax.transaction.UserTransaction to wrap the JMS and JDBC work in a single transaction.
The JTA UserTransaction API may only be used with nontransacted sessions. A transacted session will not participate in any JTA transaction, and it will ignore any UserTransaction commits or rollbacks.

source: http://www.informit.com/articles/article.aspx?p=26137&seqNum=8


----------------------------------------------------------------------------------------
Setting up ActiveMQ as a Windows Service:

    Run the batch file $activemq\bin\win64\InstallService.bat. This will install the ActiveMQ service. By default, the service is not started.
    Open Services (Start -> Run -> services.msc).
    Open the properties of the ActiveMQ service.
    Verify that "Startup type" is set to Automatic.
    Start the Service.
    Verify that ActiveMQ is running by visiting http://localhost:8161/admin/ and logging in using account  'admin' and password 'admin'.


----------------------------------------------------------------------------------------
TOPIC VS. QUEUE

    Queue is JMS managed object used for holding messages waiting for subscribers to consume. When all subscribers consumed the message , message will be removed from queue.
    Topic is that all subscribers to a topic receive the same message when the message is published.

    A queue means a message goes to one and only one possible subscriber. A topic goes to each and every subscriber.

    * Topics are for the publisher-subscriber model, while queues are for point-to-point.

    * A JMS topic is the type of destination in a 1-to-many model of distribution.

    * A JMS queue is a 1-to-1 destination of messages.

    Queue:

     - Point-to-point model.
     - Only one consumer gets the message.
     - Messages have to be delivered in the order sent.
     - A JMS queue only guarantees that each message is processed only once.
     - The Queue knows who the consumer or the JMS client is. The destination is known.
     - The JMS client (the consumer) does not have to be  active or connected to the queue all the time to receive or read the message.
     - Every message successfully processed is acknowledged by the consumer.

    Topic:

     - Publish/subscribe model.
     - Multiple clients subscribe to the message.
     - There is no guarantee messages have to be delivered in the order sent.
     - There is no guarantees that each message is processed only once.As this can be sensed from the model.
     - The Topic, have multiple subscribers and there is a chance that the topic does not know all the subscribers. The destination is unknown.
     - The subscriber / JMS client needs to the active when the messages are produced by the producer, unless the subscription was a durable subscription.
     - No, Every message successfully processed is not acknowledged by the consumer/subscriber.


--------------------------------------------------------------------------------------------------
JMS Session modes:

https://www.javaworld.com/article/2074123/java-web-development/transaction-and-redelivery-in-jms.html


---------------------------------------------------------------------------------------------------
MAP MESSAGE  AND MESSAGE

Message is a light weight message having only header and properties and no payload.
Thus if theIf the receivers are to be notified abt an event, and no data needs to be exchanged then using Message can be very efficient.


A MapMessage carries name-value pair as it's payload. Thus it's payload is similar to the java.util.Properties object of Java. The values can be Java primitives or their wrappers.

---------------------------------------------------------------------------------------------------