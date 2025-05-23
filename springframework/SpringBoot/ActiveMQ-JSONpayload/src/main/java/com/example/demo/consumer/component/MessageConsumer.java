package com.example.demo.consumer.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.example.demo.model.SystemMessage;

@Component
public class MessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

/*     @JmsListener(destination = "IEC20_OUT")
    public void messageListener(String message) {
    LOGGER.info("Message received: {}", message);
} */

    @JmsListener(destination = "IEC20_IN_1")
    public void messageListener(SystemMessage systemMessage) {
        LOGGER.info("Message recieved. {}", systemMessage);
    }
}
