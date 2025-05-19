package com.example;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {
    public static void main(String[] args) throws Exception {
        String brokerURL = "tcp://localhost:61616";

        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("test-queue");

        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        for (int i = 1; i <= 5; i++) {
            String msg = "Hello from Producer " + i;
            TextMessage message = session.createTextMessage(msg);
            producer.send(message);
            System.out.println("Sent: " + msg);
        }

while (true) {
                Thread.sleep(1000); // sleep to keep process running to see info in VisualVM
            }
        session.close();
        connection.close();
    }
}

/*

package com.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Producer {

    private static String url = "tcp://FIJYVvrhessw12.eu.bm.net:61616";

    private static String queueName = "IEC20_IN_1";

    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("url = " + url);

        String username = "broker";
        String password = "broker";

       // Create an ActiveMQConnectionFactory and set the broker URL
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

        // Create a connection
        Connection connection = connectionFactory.createConnection(username, password);
        connection.start();

        // Create a session and destination
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);

        // Create a message producer
        MessageProducer producer = session.createProducer(destination);

        // Read the content of your XML file
        Path path = Paths.get("src/test/IEC4HES3.xml");
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path));
        TextMessage message = session.createTextMessage(content);

        // Send the message
        producer.send(message);

        // Clean up and close the connection
        connection.close();
    }
}

*/
