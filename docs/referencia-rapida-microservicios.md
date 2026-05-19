# Referencia Rápida - Microservicios Biblioteca

## 🚀 Puertos y URLs Base

| Servicio | Puerto | URL Base |
|----------|--------|----------|
| **Usuarios** | 8083 | http://localhost:8083/usuarios |
| **Materiales** | 8082 | http://localhost:8082/materiales |
| **Circulación** | 8081 | http://localhost:8081/prestamos |
| **Multas** | 8084 | http://localhost:8084/multas |
| **Cobros** | 8089 | http://localhost:8089/cobros |
| **Reservas** | 8088 | http://localhost:8088/reservas |
| **Notificaciones** | 8086 | http://localhost:8086/notificaciones |
| **Préstamos Externos** | 8085 | http://localhost:8085/solicitudes-externas |
| **Reportes** | 8087 | http://localhost:8087/reportes |
| **Eureka** | 8761 | http://localhost:8761 |
| **RabbitMQ UI** | 15672 | http://localhost:15672 |

---

## 📋 Estructura de Capas (Cada Servicio)

```
com.biblioteca.{servicio}/
├── {Servicio}Application.java          ← Spring Boot entry
├── aplicacion/
│   ├── {Servicio}Service.java          ← Orquestación/Lógica de aplicación
│   └── dto/                            ← Request/Response DTOs
├── dominio/
│   ├── {Entidad}.java                  ← Agregado root (POJO)
│   └── [Servicios de dominio]
└── infraestructura/
    ├── api/                            ← Controllers REST
    ├── persistencia/                   ← JPA Entities + Repositories
    ├── mensajeria/                     ← RabbitMQ handlers/publishers
    └── clientes/                       ← OpenFeign REST clients
```

---

## 🎯 Servicios Core (Criticidad)

### 🔴 **CRÍTICO** - CirculacionService
- **Rol**: Orquestador central de préstamos
- **Llamadas sincrónicas**: 4-5 REST calls en cascada
- **Risk**: Si Usuarios/Materiales fallan → todo falla
- **Debe mejorarse con**: Circuit Breaker + Saga pattern

### 🟡 **IMPORTANTE**
- **UsuariosService**: Consultado sincronamente (estado, límites)
- **MaterialesService**: Consultado sincronamente (disponibilidad)
- **MultasService**: Consultado sincronamente (deuda pendiente)

### 🟢 **SOPORTE**
- **NotificacionesService**: Escucha eventos (no bloquea)
- **ReportesService**: Escucha eventos (no bloquea)
- **CobroService**: Registra pagos + publica eventos
- **ReservaService**: Cola de reservas + eventos
- **PrestamosExternosService**: CRUD interbibliotecario

---

## 🔄 Flujo Principal: Registrar Préstamo

```
CirculacionController.POST /prestamos
    ↓
CirculacionService.registrarPrestamo()
    ├─ 1. UsuariosClient.consultarEstado(userId) [SYNC]
    ├─ 2. MaterialesClient.consultarDisponibilidad(materialId) [SYNC]
    ├─ 3. UsuariosClient.consultarLimite(userId) [SYNC]
    ├─ 4. MultasClient.consultarDeudaPendiente(userId) [SYNC]
    ├─ 5. (Si interbibliotecario) PrestamosExternosClient.crearSolicitud() [SYNC]
    ├─ 6. PrestamoRepository.save() [DB]
    └─ 7. EventoPublisher.publish(PrestamoRegistrado) [RABBITMQ - ASYNC]
         ├─ → NotificacionesService (escucha)
         └─ → ReportesService (escucha)
```

**⚠️ Punto débil**: Pasos 1-5 son síncronos y secuenciales

---

## 📬 Eventos de Dominio (RabbitMQ)

**Exchange**: `biblioteca.events` (type: topic)

### Eventos Publicados

| Evento | Publicador | Routing Key | Listeners |
|--------|-----------|------------|-----------|
| `PrestamoRegistrado` | CirculacionService | `prestamo.registrado` | Notificaciones, Reportes |
| `MaterialDevuelto` | CirculacionService | `material.devuelto` | Notificaciones, Reportes |
| `MultaPagada` | CobroService | `multa.pagada` | Notificaciones, Reportes |
| `ReservaCreada` | ReservaService | `reserva.creada` | Notificaciones, Reportes |
| `ReservaNotificada` | ReservaService | `reserva.notificada` | Notificaciones, Reportes |
| `ReservaCancelada` | ReservaService | `reserva.cancelada` | Notificaciones, Reportes |
| `ReservaExpirada` | ReservaService | `reserva.expirada` | Notificaciones, Reportes |

---

## 🔐 DTOs Clave

### Usuarios
```
CrearUsuarioRequest { id, nombre, email, tipoUsuario }
EstadoUsuarioDTO { id, nombre, activo, estadoUsuario, tipoUsuario }
LimitePrestamoDTO { idUsuario, limite, tipoUsuario }
```

### Materiales
```
CrearMaterialRequest { id, titulo, autor, tipo, precio }
DisponibilidadDTO { id, titulo, disponible, estado }
```

### Circulación
```
RegistrarPrestamoRequest { idUsuario, idMaterial, tipoPrestamo, sede }
ResultadoOperacion { exito, mensaje, data }
```

### Multas
```
DeudaPendienteDTO { idUsuario, totalDeuda, cantidadMultas }
```

### Cobros
```
RegistrarPagoRequest { multaId, usuarioId, monto }
```

### Reservas
```
CrearReservaRequest { idUsuario, idMaterial, sede }
```

---

## 💾 Entidades JPA por Servicio

| Servicio | Entidades |
|----------|-----------|
| **Usuarios** | `UsuarioEntity` |
| **Materiales** | `MaterialEntity` |
| **Circulación** | `PrestamoEntity` |
| **Multas** | `MultaEntity` |
| **Cobros** | `RegistroPagoEntity`, `DeudaPendienteEntity` |
| **Reservas** | `ReservaEntity` |
| **Notificaciones** | `Notificacion` |
| **Préstamos Externos** | `SolicitudExternaEntity` |
| **Reportes** | `RegistroEstadistica` |

---

## 🎛️ Patrones Implementados

### ✅ Bien Implementados
- **Repository pattern** (Spring Data JPA) - Todo servicio
- **Factory methods** - Prestamo, SolicitudExterna, RegistroPago
- **Event handler pattern** - Notificaciones, Reportes
- **Event publisher pattern** - Circulación, Cobros, Reservas
- **DTO pattern** - Todos
- **Strategy pattern** - MultaService (CalculadorMultaService)

### 🔴 NO Implementados
- **Facade pattern** - ❌ (usar CirculacionService como referencia)
- **State pattern** - ❌ (usar IEstadoPrestamo del backend como referencia)
- **Builder pattern** - ❌ (DTOs complejos crearían con builder)
- **Circuit Breaker** - ❌ (crítico para REST clients)
- **Saga pattern** - ❌ (necesario para transacciones distribuidas)

---

## 🔧 Configuración de RabbitMQ

Cada servicio con messaging define:

```java
// RabbitMQConfig.java
public static final String EXCHANGE_NAME = "biblioteca.events";
public static final String ROUTING_KEY_PRESTAMO_REGISTRADO = "prestamo.registrado";
public static final String ROUTING_KEY_MATERIAL_DEVUELTO = "material.devuelto";
// etc...
```

**Queues**:
- `notificaciones.queue` → consume EventosBibliotecaHandler
- `reportes.queue` → consume EventosEstadisticaHandler

---

## 🚨 Problemas Conocidos

| ID | Problema | Servicio | Severidad |
|----|----------|----------|-----------|
| P1 | Sincronía cascada en registrarPrestamo | Circulación | 🔴 CRÍTICO |
| P2 | Sin Circuit Breaker en REST clients | Circulación | 🔴 CRÍTICO |
| P3 | Sin Facade pattern | Todos | 🟡 MODERADO |
| P4 | State pattern incompleto | Reservas | 🟡 MODERADO |
| P5 | Dominio débil (RegistroPago, Notificacion) | Cobros, Notif | 🟡 MODERADO |
| P6 | Duplicación de endpoints consultados | Usuarios, Materiales | 🟡 MODERADO |
| P7 | Sin Saga pattern para dist. transactions | Circulación | 🟡 MODERADO |
| P8 | Sin garantía de delivery en RabbitMQ | Todos | 🟡 MODERADO |
| P9 | Config RabbitMQ dispersa | Todos | 🟢 MENOR |
| P10 | Falta Builder pattern en DTOs | Todos | 🟢 MENOR |

---

## 📊 Tabla Comparativa: Fortaleza de Agregados

| Agregado | Complejidad | Factory | Reconstruir | Comportamiento | Invariantes |
|----------|-----------|---------|-----------|----------------|------------|
| `Prestamo` | ⭐⭐⭐⭐ | ✅ | ✅ | Excelente | Protegidos |
| `Reserva` | ⭐⭐⭐ | ✅ | 🟡 | Bueno | Parciales |
| `Multa` | ⭐⭐⭐ | ✅ | ❌ | Bueno | Protegidos |
| `Usuario` | ⭐⭐ | ❌ | ❌ | Simple | Débiles |
| `Material` | ⭐⭐ | ❌ | ❌ | Simple | Débiles |
| `SolicitudExterna` | ⭐⭐ | ✅ | ❌ | Básico | Débiles |
| `RegistroPago` | ⭐ | ✅ | ❌ | Anémico | Débiles |
| `Notificacion` | ⭐ | ❌ | ❌ | Anémico | Débiles |

---

## 🎯 Quick Wins (Implementación Rápida)

### 1. Agregar Circuit Breaker (30 min)
```
En CirculacionService, wrappear clientes con @CircuitBreaker
Usar Spring Cloud Resilience4j
```

### 2. Crear Interfaces Facade (1 hora)
```
IUsuarioFacade { consultarEstado(), consultarLimite() }
IMaterialFacade { consultarDisponibilidad() }
IMultaFacade { consultarDeudaPendiente() }
```

### 3. Centralizar Configuración RabbitMQ (30 min)
```
Mover constantes a commons/
Crear shared RabbitMQConfig
```

### 4. Mejorar Dominio en RegistroPago (1 hora)
```
Agregar métodos: verificarConsistencia(), aplicarRecargo()
Implementar factory + reconstruir()
```

---

## 🔄 Checklist Mejora

- [ ] Agregar Circuit Breaker a CirculacionService
- [ ] Implementar Facade en cada servicio
- [ ] Centralizar configuración RabbitMQ
- [ ] Agregar timeouts explícitos a REST clients
- [ ] Mejorar dominio débil (RegistroPago, Notificacion)
- [ ] Implementar Saga pattern
- [ ] Agregar logging distribuido (ELK?)
- [ ] Agregar health checks (actuator)
- [ ] Documenta los eventos que cada servicio escucha
- [ ] Agregar retries con exponential backoff

