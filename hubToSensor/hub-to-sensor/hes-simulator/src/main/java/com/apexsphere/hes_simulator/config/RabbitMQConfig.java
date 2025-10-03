package com.apexsphere.hes_simulator.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the RabbitMQ topology (Exchange, Queues, Bindings) for the HES Simulator service.
 * Note: MessageConverter is explicitly excluded as the simulator uses raw XML String payloads.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${messaging.rabbitmq.exchange}")
    private String exchangeName;

    // The routing key used by the Protocol Adapter to send requests (for binding)
    @Value("${messaging.rabbitmq.request-outbound-routing-key}")
    private String requestRoutingKey;

    // The routing key used by the HES Simulator to send responses back (for binding)
    @Value("${messaging.rabbitmq.response-outbound-routing-key}")
    private String responseRoutingKey;

    // The queue the HES Simulator listens to (hes.request)
    @Value("${messaging.rabbitmq.request-inbound-queue}")
    private String requestQueueName;

    // The queue the Protocol Adapter listens to (hes.response)
    @Value("${messaging.rabbitmq.response-inbound-queue}")
    private String responseQueueName;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    /**
     * Defines the queue the HES Simulator service consumes from (hes.request).
     */
    @Bean(name = "requestQueue")
    public Queue requestQueue() {
        return new Queue(requestQueueName, true); // Durable
    }

    /**
     * Defines the queue the Protocol Adapter service consumes from (hes.response).
     */
    @Bean(name = "responseQueue")
    public Queue responseQueue() {
        return new Queue(responseQueueName, true); // Durable
    }

    @Bean
    public Binding requestBinding(@Qualifier("requestQueue") Queue requestQueue, TopicExchange exchange) {
        // Binds the request queue to the exchange using the request routing key
        return BindingBuilder.bind(requestQueue).to(exchange).with(requestRoutingKey);
    }

    @Bean
    public Binding responseBinding(@Qualifier("responseQueue") Queue responseQueue, TopicExchange exchange) {
        // Binds the response queue to the exchange using the response routing key
        return BindingBuilder.bind(responseQueue).to(exchange).with(responseRoutingKey);
    }

    /**
     * Configures the RabbitTemplate. Note: We do NOT set a MessageConverter here,
     * allowing the default SimpleMessageConverter to handle raw String (XML) payloads.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        // We rely on the SimpleMessageConverter for String/XML payloads
        return template;
    }

    // --- Getters for testing/configuration review ---

    public String getRequestQueueName() {
        return requestQueueName;
    }

    public String getResponseQueueName() {
        return responseQueueName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRequestRoutingKey() {
        return requestRoutingKey;
    }

    public String getResponseRoutingKey() {
        return responseRoutingKey;
    }
}
