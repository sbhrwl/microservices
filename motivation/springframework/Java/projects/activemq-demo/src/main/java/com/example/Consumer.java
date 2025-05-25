package com.example;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer {
    public static void main(String[] args) throws Exception {
        String brokerURL = "tcp://localhost:61616";

        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("test-queue");

        MessageConsumer consumer = session.createConsumer(destination);

        for (int i = 0; i < 5; i++) {
            Message message = consumer.receive(2000);
            if (message instanceof TextMessage) {
                System.out.println("Received: " + ((TextMessage) message).getText());
            }
        }

        session.close();
        connection.close();
    }
}
