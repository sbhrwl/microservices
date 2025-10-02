package com.apexsphere.flexibility_bridge_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory; // NEW IMPORT
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.MarshallingMessageConverter; // NEW IMPORT
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller; // NEW IMPORT

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

// --- 3. Utilities / Converters ---

// 3A. JSON Converter (Default for Requests)
@Bean
public MessageConverter jsonMessageConverter() {
return new Jackson2JsonMessageConverter();
}

@Bean
public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
RabbitTemplate template = new RabbitTemplate(connectionFactory);
template.setMessageConverter(jsonMessageConverter()); // Uses JSON converter
return template;
}

// 3D. Default Listener Container Factory (Used by RequestConsumerFromHub for JSON)
// This ensures that the RequestConsumer, which does not specify a containerFactory,
// correctly uses the JSON message converter.
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
// CORRECTED to use the confirmed model package: com.apexsphere.flexibility_bridge_service.model
marshaller.setPackagesToScan("com.apexsphere.flexibility_bridge_service.model"); 
return marshaller;
}

// 4B. XML Message Converter
@Bean
public MessageConverter xmlMessageConverter(@Qualifier("marshaller") Jaxb2Marshaller marshaller) {
// MarshallingMessageConverter uses the JAXB marshaller to convert XML content
return new MarshallingMessageConverter(marshaller, marshaller);
}

// 4C. XML Listener Container Factory (Used specifically by the ResponseConsumer)
@Bean(name = "xmlListenerContainerFactory")
public SimpleRabbitListenerContainerFactory xmlListenerContainerFactory(
ConnectionFactory connectionFactory, 
@Qualifier("xmlMessageConverter") MessageConverter xmlMessageConverter) {

SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
factory.setConnectionFactory(connectionFactory);
factory.setMessageConverter(xmlMessageConverter);
return factory;
}
    
    // 4D. XML Rabbit Template (Used specifically by the ResponseProducerToHub to publish XML responses)
    @Bean(name = "xmlRabbitTemplate")
    public RabbitTemplate xmlRabbitTemplate(ConnectionFactory connectionFactory, 
                                            @Qualifier("xmlMessageConverter") MessageConverter xmlMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(xmlMessageConverter); // Uses XML converter
        return template;
    }
}
