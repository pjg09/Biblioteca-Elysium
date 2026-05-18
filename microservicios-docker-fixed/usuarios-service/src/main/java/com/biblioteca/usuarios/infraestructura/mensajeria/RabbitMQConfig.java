package com.biblioteca.usuarios.infraestructura.mensajeria;

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
    public static final String QUEUE_NAME = "usuarios.queue";

    public static final String ROUTING_KEY_BLOQUEO_PETICION     = "usuario.bloqueo.peticion";
    public static final String ROUTING_KEY_DESBLOQUEO_PETICION  = "usuario.desbloqueo.peticion";
    public static final String ROUTING_KEY_USUARIO_BLOQUEADO    = "usuario.bloqueado.deuda";

    @Bean
    public TopicExchange bibliotecaExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue usuariosQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding bloqueoPeticionBinding(Queue usuariosQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder
                .bind(usuariosQueue)
                .to(bibliotecaExchange)
                .with(ROUTING_KEY_BLOQUEO_PETICION);
    }

    @Bean
    public Binding desbloqueoBinding(Queue usuariosQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder
                .bind(usuariosQueue)
                .to(bibliotecaExchange)
                .with(ROUTING_KEY_DESBLOQUEO_PETICION);
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
