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

    @Value("${messaging.rabbitmq.request-inbound-queue}")
    private String requestInboundQueueName;

    @Value("${messaging.rabbitmq.response-inbound-queue}")
    private String responseInboundQueueName;

    @Value("${messaging.rabbitmq.request-outbound-routing-key}")
    private String requestOutboundRoutingKey; // Value: connector.request (used for publishing/binding)

    @Value("${messaging.rabbitmq.response-outbound-routing-key}")
    private String responseOutboundRoutingKey;

    // --- Exchange ---
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    // --- 1. Queue Definitions (Inbound and Outbound destinations) ---
    
    // 1A. Hub Request (Inbound consumer queue for this service)
    @Bean(name = "requestInboundQueue")
    public Queue requestInboundQueue() {
        return new Queue(requestInboundQueueName, true); // flexibility-hub.request
    }

    // 1B. Connector Response (Inbound consumer queue for this service)
    @Bean(name = "responseInboundQueue")
    public Queue responseInboundQueue() {
        return new Queue(responseInboundQueueName, true); // connector.response
    }
    
    // 1C. Connector Request (Outbound destination queue for this service's producer)
    @Bean(name = "connectorRequestQueue") 
    public Queue connectorRequestQueue() {
        // The queue name matches the routing key 'connector.request'
        return new Queue(requestOutboundRoutingKey, true); 
    }

    // --- 2. Binding Definitions ---

    // 2A. Binds Hub Request Queue to its assumed publishing key
    @Bean
    public Binding requestInboundBinding(@Qualifier("requestInboundQueue") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(requestInboundQueueName);
    }

    // 2B. Binds Connector Response Queue to its assumed publishing key
    @Bean
    public Binding responseInboundBinding(@Qualifier("responseInboundQueue") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(responseInboundQueueName);
    }
    
    // 2C. FIX: Binds the Connector Request Queue to the OUTBOUND routing key 
    @Bean
    public Binding connectorRequestBinding(@Qualifier("connectorRequestQueue") Queue queue, TopicExchange exchange) {
        // The message producer uses 'requestOutboundRoutingKey' (connector.request)
        return BindingBuilder.bind(queue).to(exchange).with(requestOutboundRoutingKey);
    }

    // --- 3. Utilities ---

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}