package com.biblioteca.notificaciones.infraestructura.mensajeria;

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
    public static final String QUEUE_NAME = "notificaciones.queue";

    @Bean
    public TopicExchange bibliotecaExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue notificacionesQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingPrestamoRegistrado(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("prestamo.registrado");
    }

    @Bean
    public Binding bindingMaterialDevuelto(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("material.devuelto");
    }

    @Bean
    public Binding bindingPrestamoRenovado(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("prestamo.renovado");
    }

    @Bean
    public Binding bindingRenovacionRechazada(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("renovacion.rechazada");
    }

    @Bean
    public Binding bindingMultaGenerada(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("multa.generada");
    }

    @Bean
    public Binding bindingMultaPagada(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("multa.pagada");
    }

    @Bean
    public Binding bindingUsuarioBloqueadoDeuda(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("usuario.bloqueado.deuda");
    }

    @Bean
    public Binding bindingUsuarioDesbloqueoPeticion(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("usuario.desbloqueo.peticion");
    }

    @Bean
    public Binding bindingReservaCreada(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("reserva.creada");
    }

    @Bean
    public Binding bindingReservaNotificada(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("reserva.notificada");
    }

    @Bean
    public Binding bindingReservaExpirada(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("reserva.expirada");
    }

    @Bean
    public Binding bindingReservaCancelada(Queue notificacionesQueue, TopicExchange bibliotecaExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(bibliotecaExchange).with("reserva.cancelada");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
