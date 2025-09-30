package com.apexsphere.flexibility_bridge_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange; // Keep TopicExchange; fine for exact keys too
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

    // ✅ Use distinct routing keys
    @Value("${messaging.rabbitmq.request-routing-key}")
    private String requestRoutingKey;

    @Value("${messaging.rabbitmq.response-routing-key}")
    private String responseRoutingKey;

    @Value("${messaging.rabbitmq.request-queue}")
    private String requestQueueName;

    @Value("${messaging.rabbitmq.response-queue}")
    private String responseQueueName;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean(name = "requestQueue")
    public Queue requestQueue() {
        // Durable queues are generally preferred
        return new Queue(requestQueueName, true);
    }

    @Bean(name = "responseQueue")
    public Queue responseQueue() {
        return new Queue(responseQueueName, true);
    }

    @Bean
    public Binding requestBinding(@Qualifier("requestQueue") Queue requestQueue, TopicExchange exchange) {
        // ✅ Bind request queue with request routing key
        return BindingBuilder.bind(requestQueue).to(exchange).with(requestRoutingKey);
    }

    @Bean
    public Binding responseBinding(@Qualifier("responseQueue") Queue responseQueue, TopicExchange exchange) {
        // ✅ Bind response queue with response routing key
        return BindingBuilder.bind(responseQueue).to(exchange).with(responseRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        // ✅ Default publisher settings: send to the exchange with the request routing key
        template.setExchange(exchangeName);
        template.setRoutingKey(requestRoutingKey);
        return template;
    }

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
