package com.apexsphere.flexibility_bridge_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${messaging.rabbitmq.exchange}")
    private String exchangeName;

    // FIX: Using the inbound queue property for the Hub request consumer
    @Value("${messaging.rabbitmq.request-inbound-queue}")
    private String requestInboundQueueName;

    // FIX: Using the inbound queue property for the Connector response consumer
    @Value("${messaging.rabbitmq.response-inbound-queue}")
    private String responseInboundQueueName;

    // FIX: Using the outbound routing key for publishing to the Connector
    @Value("${messaging.rabbitmq.request-outbound-routing-key}")
    private String requestOutboundRoutingKey;

    // FIX: Using the outbound routing key for publishing to the Hub
    @Value("${messaging.rabbitmq.response-outbound-routing-key}")
    private String responseOutboundRoutingKey;

    @Bean
    public TopicExchange exchange() {
        // Exchange name: flexibility-bridge.exchange
        return new TopicExchange(exchangeName);
    }

    @Bean(name = "requestInboundQueue")
    public Queue requestInboundQueue() {
        // Queue name: flexibility-hub.request
        return new Queue(requestInboundQueueName, true);
    }

    @Bean(name = "responseInboundQueue")
    public Queue responseInboundQueue() {
        // Queue name: connector.response
        return new Queue(responseInboundQueueName, true);
    }

    @Bean
    public Binding requestInboundBinding(@Qualifier("requestInboundQueue") Queue queue, TopicExchange exchange) {
        // We bind the INBOUND queue for consumption to the OUTBOUND routing key 
        // that the HUB service uses to send messages. (This requires coordination 
        // with the Hub's publication key, assuming the Hub publishes using the queue name).
        // Since we don't know the Hub's key, we'll assume the binding key is the queue name for simplicity.
        return BindingBuilder.bind(queue).to(exchange).with(requestInboundQueueName);
    }

    @Bean
    public Binding responseInboundBinding(@Qualifier("responseInboundQueue") Queue queue, TopicExchange exchange) {
        // The Connector publishes to a key that binds to this queue. Assume the binding key is the queue name.
        return BindingBuilder.bind(queue).to(exchange).with(responseInboundQueueName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        // NOTE: Default exchange and routing key settings are optional since 
        // MessageProducerService will use the explicit exchange/routing key for each send.
        return template;
    }
}