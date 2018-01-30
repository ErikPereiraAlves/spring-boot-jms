Example project using Java JMS.

1 - Starter example, where this project evolved from.
https://spring.io/guides/gs/messaging-jms/

2- Check activeMQ examples
C:\Users\Erik_Pereira_Alves\Downloads\apache-activemq-5.15.2-bin\apache-activemq-5.15.2\examples\openwire\ecommerce\src

More info:

Using JTA Transactions with JMS
Transacted sessions enable a JMS producer or consumer to group messages into a single, atomic send or receive. However, a transacted session is only used within JMS. Many applications need JMS and JDBC or EJB work to participate in a single transaction. For instance, an application might dequeue a message containing a customer order and use the order information to update some inventory tables in the database. The order processing and the inventory update must be within the same transaction, so a transacted session is insufficient because it only handles JMS. The application must use a JTA javax.transaction.UserTransaction to wrap the JMS and JDBC work in a single transaction.
The JTA UserTransaction API may only be used with nontransacted sessions. A transacted session will not participate in any JTA transaction, and it will ignore any UserTransaction commits or rollbacks.

source: http://www.informit.com/articles/article.aspx?p=26137&seqNum=8
