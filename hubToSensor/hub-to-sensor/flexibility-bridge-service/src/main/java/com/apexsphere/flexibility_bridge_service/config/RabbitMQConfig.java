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
import org.springframework.amqp.support.converter.MarshallingMessageConverter; 
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller; 

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

    // 3A. JSON Converter (Default for Requests)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 3B. Default Rabbit Template (Uses JSON converter)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter()); // Uses JSON converter
        return template;
    }

    // 3C. Default Listener Container Factory (Used by RequestConsumerFromHub for JSON)
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("jsonMessageConverter") MessageConverter jsonMessageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }


    // --- 4. XML Configuration for Responses (Used by ResponseConsumer) ---

    // 4A. JAXB Marshaller/Unmarshaller for FlexibilityResponse model
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // Set the package where the @XmlRootElement class (FlexibilityResponse) is located
        marshaller.setPackagesToScan("com.apexsphere.flexibility_bridge_service.model"); 
        return marshaller;
    }

    // 4B. XML Message Converter
    @Bean
    public MessageConverter xmlMessageConverter(@Qualifier("marshaller") Jaxb2Marshaller marshaller) {
        // MarshallingMessageConverter uses the JAXB marshaller to convert XML content
        return new MarshallingMessageConverter(marshaller, marshaller);
    }

    // 4C. XML Rabbit Template (Used by ResponseProducerToHub)
    @Bean(name = "xmlRabbitTemplate")
    public RabbitTemplate xmlRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        // Explicitly call the message converter with its dependency chain (marshaller)
        template.setMessageConverter(xmlMessageConverter(marshaller())); 
        return template;
    }

    // 4D. XML Listener Container Factory (Used specifically by the ResponseConsumer)
    @Bean(name = "xmlListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory xmlListenerContainerFactory(
            ConnectionFactory connectionFactory, 
            @Qualifier("xmlMessageConverter") MessageConverter xmlMessageConverter) {
        
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(xmlMessageConverter);
        return factory;
    }
    
    // --- 5. Queue and Binding Initialization (Guaranteed Declaration) ---

    // Creates the Admin for manual declaration
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    
    /**
     * Explicitly declares the Hub Response queue and binding to ensure they are set up 
     * immediately on application startup, overriding potential timing issues with auto-declaration.
     */
    @Bean
    public Object initializeHubResponseQueueAndBinding(AmqpAdmin amqpAdmin,
                                                     @Qualifier("hubResponseQueue") Queue hubResponseQueue) {
        // Ensure the exchange is also declared
        TopicExchange topicExchange = exchange();
        amqpAdmin.declareExchange(topicExchange);

        // Declare the queue
        amqpAdmin.declareQueue(hubResponseQueue);
        
        // Manually declare the binding using the explicit parameters to guarantee the key is used.
        Binding binding = BindingBuilder.bind(hubResponseQueue).to(topicExchange).with(responseOutboundRoutingKey);
        amqpAdmin.declareBinding(binding);
        
        return null; 
    }
}
