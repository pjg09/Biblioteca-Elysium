package com.biblioteca.dominio.eventos;

import java.time.Instant;

public interface IDomainEvent {
    String eventId();
    String eventType();
    Instant occurredOn();
    String aggregateId();
    int version();
}
