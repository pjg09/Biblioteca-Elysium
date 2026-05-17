# Diagramas de Estados - Patrones Aplicados

## 1. Diagrama de Estados: Prestamo (State Pattern)

```mermaid
stateDiagram-v2
    [*] --> ACTIVO: crear()
    
    ACTIVO --> ACTIVO: renovar(fecha, maxRenovaciones)
    note right of ACTIVO
        • Permite hasta N renovaciones
        • Calcula nueva fecha
        • Valida no exceder límite
    end note
    
    ACTIVO --> COMPLETADO: devolver(fecha)
    note right of COMPLETADO
        Estado terminal
        Material es retornado
        No permite más operaciones
    end note
    
    ACTIVO --> CANCELADO: cancelar()
    note right of CANCELADO
        Estado terminal
        Préstamo cancelado
        No permite más operaciones
    end note
    
    COMPLETADO --> [*]
    CANCELADO --> [*]
    
    COMPLETADO -.-> [*]: terminal
    CANCELADO -.-> [*]: terminal
```

**Transiciones permitidas:**
- `ACTIVO → ACTIVO`: Renovación (máximo N veces)
- `ACTIVO → COMPLETADO`: Devolución
- `ACTIVO → CANCELADO`: Cancelación
- Estados terminales: COMPLETADO, CANCELADO

**Excepciones lanzadas:**
- Renovar cuando se excede límite: `OperacionNoPermitidaEnEstadoException`
- Operar en estado terminal: `OperacionNoPermitidaEnEstadoException`

---

## 2. Diagrama de Estados: Reserva (State Pattern)

```mermaid
stateDiagram-v2
    [*] --> EN_ESPERA: crearReserva()
    
    EN_ESPERA --> NOTIFICADA: notificar()\n[posición == 1]
    note right of EN_ESPERA
        • En la cola esperando
        • Solo notificable si es posición 1
        • Puede cancelarse
    end note
    
    EN_ESPERA --> CANCELADA: cancelar()
    
    NOTIFICADA --> COMPLETADA: completar()\n[usuario recoge]
    NOTIFICADA --> EXPIRADA: expirar()\n[24h sin recoger]
    NOTIFICADA --> CANCELADA: cancelar()\n[usuario cancela]
    note right of NOTIFICADA
        • Material disponible
        • 24 horas para recoger
        • Puede expirar o cancelarse
    end note
    
    COMPLETADA --> [*]
    CANCELADA --> [*]
    EXPIRADA --> [*]
    
    COMPLETADA -.-> [*]: terminal
    CANCELADA -.-> [*]: terminal
    EXPIRADA -.-> [*]: terminal
```

**Transiciones permitidas:**
- `EN_ESPERA → NOTIFICADA`: Material disponible (solo posición 1)
- `EN_ESPERA → CANCELADA`: Usuario cancela
- `NOTIFICADA → COMPLETADA`: Usuario recoge
- `NOTIFICADA → EXPIRADA`: Pasaron 24 horas
- `NOTIFICADA → CANCELADA`: Usuario cancela después de notificado
- Estados terminales: COMPLETADA, CANCELADA, EXPIRADA

**Regla importante:** Solo se puede notificar si `posicionCola == 1`

---

## 3. Diagrama de Estados: Multa (State Pattern)

```mermaid
stateDiagram-v2
    [*] --> GENERADA: calcularMulta()
    
    GENERADA --> PAGADA: pagar(fecha)
    note right of GENERADA
        • Multa recién creada
        • Pendiente de resolución
        • Generada por retraso/pérdida/daño
    end note
    
    GENERADA --> CONDONADA: condonar()
    
    PAGADA --> [*]
    CONDONADA --> [*]
    
    PAGADA -.-> [*]: terminal\nPagada por usuario
    CONDONADA -.-> [*]: terminal\nPerdonada por sistema
```

**Transiciones permitidas:**
- `GENERADA → PAGADA`: Usuario paga
- `GENERADA → CONDONADA`: Sistema condona
- Estados terminales: PAGADA, CONDONADA

**Cálculo dinámico según tipo de usuario:**
- Usa Strategy Pattern (CalculadorMultaContext)
- Selecciona estrategia: ESTUDIANTE, PROFESOR, INVESTIGADOR, PÚBLICO

---

## 4. Matriz de Transiciones Válidas

| Estado Origen | Operación | Estado Destino | Válido | Excepciones |
|---|---|---|---|---|
| **PRESTAMO** |
| ACTIVO | renovar() | ACTIVO | ✓ | Si max renovaciones |
| ACTIVO | devolver() | COMPLETADO | ✓ | N/A |
| ACTIVO | cancelar() | CANCELADO | ✓ | N/A |
| COMPLETADO | renovar() | - | ✗ | OperacionNoPermitida |
| COMPLETADO | devolver() | - | ✗ | OperacionNoPermitida |
| CANCELADO | cualquier() | - | ✗ | OperacionNoPermitida |
| **RESERVA** |
| EN_ESPERA | notificar() | NOTIFICADA | ✓ | Si posición=1 |
| EN_ESPERA | cancelar() | CANCELADA | ✓ | N/A |
| NOTIFICADA | completar() | COMPLETADA | ✓ | N/A |
| NOTIFICADA | expirar() | EXPIRADA | ✓ | N/A |
| NOTIFICADA | cancelar() | CANCELADA | ✓ | N/A |
| COMPLETADA | cualquier() | - | ✗ | OperacionNoPermitida |
| **MULTA** |
| GENERADA | pagar() | PAGADA | ✓ | N/A |
| GENERADA | condonar() | CONDONADA | ✓ | N/A |
| PAGADA | pagar() | - | ✗ | OperacionNoPermitida |
| CONDONADA | condonar() | - | ✗ | OperacionNoPermitida |

---

## 5. Integración de Patrones

### State Pattern + Strategy Pattern (Multa)

```
Multa.pagar(fecha)
    ↓
MultaGeneradaState.pagar(contexto)
    ↓
contexto.estado = MultaPagadaState
    ↓
CirculacionService publica evento "multa.pagada"
    ↓
CalculadorMultaContext selecciona estrategia
    ↓
Estrategia específica (Estudiante/Profesor/Investigador/Público)
    ↓
Calcula multa con tarifas diferenciadas
```

### Facade + Validación (Circulacion)

```
CirculacionFacade.registrarPrestamo()
    ↓
ValidadorReglasService.validar(contexto)
    ├─ ReglaUsuarioActivo
    ├─ ReglaMaterialDisponible
    ├─ ReglaLimitePrestamos
    └─ ReglaUsuarioNoMoroso
    ↓
PrestamoBuilder.construir()
    ↓
Prestamo con estado ACTIVO (State Pattern)
    ↓
PrestamoEntity.save()
```

---

## 6. Beneficios de los Patrones Aplicados

### State Pattern
- ✓ Elimina if/else complejos
- ✓ Cada estado es testeable independientemente
- ✓ Fácil agregar nuevos estados
- ✓ Cumple Open/Closed Principle

### Strategy Pattern
- ✓ Cálculos diferenciados sin switch
- ✓ Fácil agregar nuevos tipos de usuario
- ✓ Bajo acoplamiento
- ✓ Cumple Liskov Substitution Principle

### Facade Pattern
- ✓ Interfaz única para clientes
- ✓ Coordina múltiples servicios
- ✓ Encapsula complejidad
- ✓ Facilita testing

### Builder Pattern
- ✓ Validación centralizada
- ✓ Construcción segura
- ✓ Manejo de errores con Resultado<T>
- ✓ API fluida

### Validación (IReglaValidacion)
- ✓ Reglas compostas
- ✓ Cada regla es independiente
- ✓ Fácil de extender
- ✓ Cumple Single Responsibility
