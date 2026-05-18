package com.biblioteca.reportes.infraestructura.mensajeria;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "biblioteca.events";
    public static final String QUEUE_NAME = "reportes.queue";

    @Bean
    public TopicExchange bibliotecaExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue reportesQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingPrestamoRegistrado(Queue reportesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(reportesQueue).to(bibliotecaExchange).with("prestamo.registrado");
    }

    @Bean
    public Binding bindingMaterialDevuelto(Queue reportesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(reportesQueue).to(bibliotecaExchange).with("material.devuelto");
    }

    @Bean
    public Binding bindingMultaGenerada(Queue reportesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(reportesQueue).to(bibliotecaExchange).with("multa.generada");
    }

    @Bean
    public Binding bindingMultaPagada(Queue reportesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(reportesQueue).to(bibliotecaExchange).with("multa.pagada");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
