# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java console application demonstrating SOLID principles and design patterns in the context of a library management system (Sistema de Biblioteca). University assignment — no web framework, in-memory persistence only.

**Requirements:** Java 17+, Maven 3.6+

## Common Commands

All commands must be run from the `biblioteca-backend/` directory.

```bash
# Build
mvn clean compile

# Build and run (launches interactive console menu)
mvn clean compile exec:java

# Package as JAR
mvn clean package
```

```bash
# Run all tests
mvn clean test

# Run a single test class
mvn clean test -Dtest=PrestamoTest
```

## Architecture

### Layer Communication
```
MenuConsola → [BibliotecaFacade | ConsultaFacade | AdministracionFacade] → Services → Domain + Repositories
```

Each layer depends only on interfaces. `Main.java` is the composition root — it wires all dependencies via constructor injection and loads mock data.

### Layer Breakdown

**Presentation** (`consola/MenuConsola.java`)
Interactive console menu. Depends only on the three facade interfaces.

**Facade** (`servicios/interfaces/IXxxFacade.java`, implemented in `servicios/implementaciones/`)
- `BibliotecaFacade` — loans, returns, renewals, reservations
- `ConsultaFacade` — queries and reports
- `AdministracionFacade` — user and material CRUD

**Services** (`servicios/implementaciones/`)
Single-responsibility services: `PrestamoService`, `DevolucionService`, `ReservaService`, `RenovacionService`, `GestorMultasService`, `ValidadorReglasService`, `DisponibilidadStandardService`, `GestorBloqueoService`, etc.

**Domain** (`dominio/`)
- `entidades/` — abstract bases (`Usuario`, `Material`, `Prestamo`, `Multa`) with concrete subtypes (e.g., `Estudiante`, `Libro`, `PrestamoNormal`)
- `objetosValor/` — `Resultado<T>` (universal result wrapper), `Evaluacion`, `ContextoMulta`, `ContextoValidacion`
- `estados/` — State pattern for loan lifecycle (`IEstadoPrestamo`)
- `builders/` — Builder pattern for entities
- `enumeraciones/` — All enums (`TipoUsuario`, `TipoMaterial`, `EstadoMaterial`, `EstadoReserva`, etc.)
- `eventos/` — Domain events (`IDomainEvent` + 13 event classes emitted by aggregates)

**Repositories** (`repositorios/`)
Generic `IRepositorio<T>` interface with in-memory implementations. `IRepositorioFactory` / `RepositorioEnMemoriaFactory` used for creation.

### Design Patterns in Use

| Pattern | Where |
|---------|-------|
| Facade | `BibliotecaFacade`, `ConsultaFacade`, `AdministracionFacade` |
| Strategy | `ICalculadorMulta` implementations; `IReglaValidacion` rules composed in `ValidadorReglasService` |
| State | `IEstadoPrestamo` → `PrestamoActivoState`, `PrestamoCompletadoState` |
| Builder | `MaterialBuilder`, `UsuarioBuilder`, `PrestamoBuilder` |
| Abstract Factory | `IRepositorioFactory` / `RepositorioEnMemoriaFactory` |
| Repository | `IRepositorio<T>` — generic CRUD interface |
| Value Object | `Resultado`, `Evaluacion`, `ContextoMulta` |

### SOLID Principle Mapping

- **S** — Each service has one responsibility (e.g., `GestorMultasService` only manages fines)
- **O** — New fine types → add `ICalculadorMulta` impl; new validation rules → add `IReglaValidacion` impl
- **L** — Abstract entity hierarchies (`Material`, `Usuario`, `Prestamo`) are substitutable
- **I** — `IPrestamoService`, `IDevolucionService`, `IReservaService` are separate interfaces, not one monolith
- **D** — Services depend on `IRepositorio<T>`, facades depend on service interfaces; `Main.java` owns concrete bindings

## Key Files

- `biblioteca-backend/src/main/java/com/biblioteca/Main.java` — entry point, DI wiring, mock data loading
- `biblioteca-backend/pom.xml` — build config, main class set to `com.biblioteca.Main`
- `diagrama_nuevo_patrones.puml` — diagrama UML actualizado con todos los patrones y cambios de Fase 2
- `docs/migracion-microservicios-plan.md` — plan completo de migración DDD con estado actual
- `docs/diagramas-arquitectura.md` — diagramas Mermaid de la arquitectura de microservicios

## Gotchas

- `OperacionNoPermitidaException(String operacion, String motivo)` requiere **dos argumentos** — nunca uno solo.
- `BibliotecaException` extiende `Exception` (checked, no RuntimeException). Los métodos de agregado que la lanzan deben declarar `throws OperacionNoPermitidaException`. Los tests también.
- `@EnableEurekaClient` no existe en Spring Cloud 3.x — usar solo `@SpringBootApplication` (auto-configurado).
- Dominio en microservicios: si el constructor del agregado es privado, añadir `static reconstruir(...)` para que `toDomain()` en la entidad JPA pueda usarlo.
- El directorio en disco es `objetosValor` (V mayúscula) pero el package Java es `com.biblioteca.dominio.objetosvalor` (todo minúscula). Usar minúscula en imports.

## Modelo de dominio enriquecido

Los agregados protegen sus invariantes. No usar setters de estado — usar los métodos de comportamiento:
- `Prestamo.renovar(nuevaFecha, maxRenovaciones)` / `Prestamo.devolver(fechaReal, evaluacion)`
- `Reserva.registrar(posicion)` / `cancelar()` / `notificarDisponibilidad(fecha)` / `expirar()`
- `Multa.pagar(fechaPago)` / `condonar()`
- Todos tienen `pullEvents()` — llamar tras persistir para extraer `List<IDomainEvent>`.

`ContextoMulta` lleva `diasRetraso`, `tipoUsuario` y `valorMaterial` pre-resueltos. Los calculadores **no inyectan repositorios**.

`EstadoReserva` enum (`EN_ESPERA`, `NOTIFICADA`, `COMPLETADA`, `CANCELADA`) coexiste con `EstadoTransaccion` en `Reserva`.

Eventos de dominio en `dominio/eventos/` (13 clases + `IDomainEvent`).

Tests en `biblioteca-backend/src/test/java/com/biblioteca/dominio/` (4 clases, 32 tests).

## Microservicios (Fase 3)

Directorio: `microservicios/`. Construcción desde la raíz del módulo:

```bash
# Construir todos los módulos (desde microservicios/)
mvn clean install -DskipTests

# Levantar infraestructura + servicios
docker-compose up --build
```

RabbitMQ exchange: `biblioteca.events` (tipo topic). Routing keys: `prestamo.registrado`, `material.devuelto`, `multa.generada`, `multa.pagada`, `reserva.notificada`, entre otros.

| Servicio | Puerto |
|---|---|
| `circulacion-service` | 8081 |
| `materiales-service` | 8082 |
| `usuarios-service` | 8083 |
| `multas-service` | 8084 |
| `prestamos-externos-service` | 8085 |
| `notificaciones-service` | 8086 |
| `reportes-service` | 8087 |
| `reservas-service` | 8088 |
| `cobros-service` | 8089 |
| Eureka Server | 8761 |
| RabbitMQ UI | 15672 |
