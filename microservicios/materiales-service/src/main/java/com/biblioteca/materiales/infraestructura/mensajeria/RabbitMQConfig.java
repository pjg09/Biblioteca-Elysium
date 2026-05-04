package com.biblioteca.materiales.infraestructura.mensajeria;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "biblioteca.events";
    public static final String QUEUE_NAME = "materiales.queue";
    public static final String ROUTING_KEY_MATERIAL_DEVUELTO = "material.devuelto";

    @Bean
    public TopicExchange bibliotecaExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue materialesQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding materialDevueltoBinding(Queue materialesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder
                .bind(materialesQueue)
                .to(bibliotecaExchange)
                .with(ROUTING_KEY_MATERIAL_DEVUELTO);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter);
        return template;
    }
}
