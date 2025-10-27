package com.apexsphere.flexibility_bridge_service.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory; 
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// NEW IMPORT
import com.fasterxml.jackson.databind.ObjectMapper; 

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
    private String responseOutboundRoutingKey; // Value: flexibility-hub.response

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

    // 1D. Hub Response Queue (Outbound destination queue for this service's producer)
    @Bean(name = "hubResponseQueue") 
    public Queue hubResponseQueue() {
        // Queue name matches the routing key 'flexibility-hub.response'
        return new Queue(responseOutboundRoutingKey, true); 
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
    
    // 2C. Binds the Connector Request Queue to the OUTBOUND routing key 
    @Bean
    public Binding connectorRequestBinding(@Qualifier("connectorRequestQueue") Queue queue, TopicExchange exchange) {
        // The message producer uses 'requestOutboundRoutingKey' (connector.request)
        return BindingBuilder.bind(queue).to(exchange).with(requestOutboundRoutingKey);
    }

    // 2D. Binds the Hub Response Queue to the OUTBOUND routing key 
    @Bean
    public Binding hubResponseBinding(@Qualifier("hubResponseQueue") Queue queue, TopicExchange exchange) {
        // The response producer uses 'responseOutboundRoutingKey' (flexibility-hub.response)
        return BindingBuilder.bind(queue).to(exchange).with(responseOutboundRoutingKey);
    }


    // --- 3. Utilities / Converters ---
    
    // 3A-NEW. Shared ObjectMapper for JSON conversion and manual parsing (used in ResponseConsumer)
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // 3B. JSON Converter (Default for Requests and Responses)
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        // Injecting the shared ObjectMapper ensures consistent serialization/deserialization logic.
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // 3C. Default Rabbit Template (Uses JSON converter)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter); // Uses JSON converter
        return template;
    }

    // 3D. Default Listener Container Factory (Used by consumers for JSON)
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // The converter is provided directly, eliminating the need for @Qualifier here.
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
    
    // --- 4. Queue and Binding Initialization (Guaranteed Declaration) ---

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    
    // This part is for declaration, keeping it simple as an example
    @Bean
    public Object declareQueuesAndBindings(AmqpAdmin amqpAdmin,
                                         @Qualifier("requestInboundQueue") Queue requestQueue,
                                         @Qualifier("responseInboundQueue") Queue responseQueue,
                                         @Qualifier("hubResponseQueue") Queue hubResponseQueue,
                                         TopicExchange exchange) {
        
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(requestQueue);
        amqpAdmin.declareQueue(responseQueue);
        amqpAdmin.declareQueue(hubResponseQueue);
        
        amqpAdmin.declareBinding(requestInboundBinding(requestQueue, exchange));
        amqpAdmin.declareBinding(responseInboundBinding(responseQueue, exchange));
        amqpAdmin.declareBinding(hubResponseBinding(hubResponseQueue, exchange));
        
        return null; 
    }
}
