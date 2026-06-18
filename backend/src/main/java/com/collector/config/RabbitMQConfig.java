package com.collector.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Noms des queues
    public static final String QUEUE_VALIDEE = "annonce.validee.queue";
    public static final String QUEUE_REJETEE = "annonce.rejetee.queue";

    // Exchange
    public static final String EXCHANGE = "collector.exchange";

    // Routing keys
    public static final String ROUTING_KEY_VALIDEE = "annonce.validee";
    public static final String ROUTING_KEY_REJETEE = "annonce.rejetee";

    // Déclaration des queues
    @Bean
    public Queue queueValidee() {
        return new Queue(QUEUE_VALIDEE, true);
    }

    @Bean
    public Queue queueRejetee() {
        return new Queue(QUEUE_REJETEE, true);
    }

    // Déclaration de l'exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // Liaison queue validée avec l'exchange
    @Bean
    public Binding bindingValidee(Queue queueValidee, TopicExchange exchange) {
        return BindingBuilder
                .bind(queueValidee)
                .to(exchange)
                .with(ROUTING_KEY_VALIDEE);
    }

    // Liaison queue rejetée avec l'exchange
    @Bean
    public Binding bindingRejetee(Queue queueRejetee, TopicExchange exchange) {
        return BindingBuilder
                .bind(queueRejetee)
                .to(exchange)
                .with(ROUTING_KEY_REJETEE);
    }

    // Convertisseur de messages en JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}