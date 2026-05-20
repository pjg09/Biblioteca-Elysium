# Elysium — Sistema de Gestión Bibliotecaria

Proyecto universitario de **Arquitectura de Software** que implementa los principios SOLID y patrones de diseño en dos módulos complementarios: un monolito Java con consola interactiva y una arquitectura de microservicios basada en DDD con Docker.

---

## Tabla de contenidos

- [Descripción](#descripción)
- [Módulos del monorepo](#módulos-del-monorepo)
- [Requisitos](#requisitos)
- [Inicio rápido](#inicio-rápido)
  - [Módulo 1 — Monolito (biblioteca-backend)](#módulo-1--monolito-biblioteca-backend)
  - [Módulo 2 — Microservicios](#módulo-2--microservicios)
- [Arquitectura](#arquitectura)
  - [Monolito](#monolito)
  - [Microservicios](#microservicios)
- [Patrones de diseño](#patrones-de-diseño)
- [Principios SOLID](#principios-solid)
- [Testing y cobertura](#testing-y-cobertura)
  - [Monolito](#monolito-1)
  - [Microservicios](#microservicios-1)
- [Estructura de directorios](#estructura-de-directorios)
- [Variables de entorno](#variables-de-entorno)

---

## Descripción

**Elysium** modela las operaciones de una biblioteca académica: préstamos, devoluciones, reservas, multas y notificaciones. El proyecto está organizado como monorepo con dos fases de desarrollo:

| Fase | Módulo | Descripción |
|------|--------|-------------|
| 1–2  | `biblioteca-backend/` | Monolito Java puro, menú de consola, patrones de diseño SOLID |
| 3    | `microservicios/`     | Arquitectura DDD con Spring Boot, Docker Compose, RabbitMQ y Eureka |

---

## Módulos del monorepo

```
.
├── biblioteca-backend/     # Monolito Java (Fase 1-2) — referencia de patrones SOLID
├── microservicios/         # Microservicios DDD (Fase 3) — módulo activo
└── docs/                   # Documentación de arquitectura, diagramas y planes
```

---

## Requisitos

### Monolito

| Herramienta | Versión mínima |
|-------------|----------------|
| Java        | 17             |
| Maven       | 3.6            |

### Microservicios

| Herramienta  | Versión mínima |
|--------------|----------------|
| Java         | 17             |
| Maven        | 3.6            |
| Docker       | 24             |
| Docker Compose | v2 (plugin) |

> **Linux:** el usuario debe pertenecer al grupo `docker` para no necesitar `sudo`.
> ```bash
> sudo usermod -aG docker $USER && newgrp docker
> ```

---

## Inicio rápido

### Módulo 1 — Monolito (`biblioteca-backend`)

```bash
cd biblioteca-backend/

# Compilar y ejecutar (lanza el menú interactivo de consola)
mvn clean compile exec:java

# Solo compilar
mvn clean compile

# Empaquetar como JAR
mvn clean package
```

### Módulo 2 — Microservicios

```bash
cd microservicios/

# Arranque completo: compila JARs → construye imágenes → levanta stack → lanza CLI
./start.sh

# Apagar el stack
docker compose down
```

El script `start.sh` realiza los siguientes pasos automáticamente:

1. `mvn clean package -DskipTests` — compila todos los módulos Maven
2. `docker compose --profile cli build` — construye las imágenes Docker
3. `docker compose up -d` — levanta el stack en background
4. Espera a que `circulacion-service` responda en `/actuator/health`
5. Lanza el CLI interactivo (`cli-service`)

#### Reconstruir solo el CLI

```bash
docker compose --profile cli build cli-service
docker compose --profile cli run --rm cli-service
```

#### Solucionar problemas de red

```bash
sudo systemctl restart docker                          # contenedores bloqueados por sudo
docker container prune -f && docker network prune -f   # redes/contenedores zombies
```

---

## Arquitectura

### Monolito

El monolito sigue una arquitectura en capas donde cada capa depende únicamente de interfaces:

```
MenuConsola
    └── [BibliotecaFacade | ConsultaFacade | AdministracionFacade]
            └── Services (PrestamoService, DevolucionService, ...)
                    └── Domain + Repositories (IRepositorio<T>)
```

**Capas:**

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Presentación | `consola/` | Menú de consola interactivo |
| Fachada | `servicios/interfaces/` | Punto de entrada unificado por dominio |
| Servicios | `servicios/implementaciones/` | Lógica de negocio de responsabilidad única |
| Dominio | `dominio/` | Entidades, agregados, eventos, value objects |
| Repositorios | `repositorios/` | Abstracción de persistencia (en memoria) |

`Main.java` es la raíz de composición: conecta todas las dependencias mediante inyección por constructor y carga los datos mock.

### Microservicios

Arquitectura de microservicios con DDD, comunicación asíncrona vía RabbitMQ y descubrimiento de servicios con Eureka.

```
CLI ──► circulacion-service ──► materiales-service
                │               usuarios-service
                │               multas-service
                │               reservas-service
                │
                └──(RabbitMQ)──► notificaciones-service
                                 reportes-service
                                 cobros-service
```

**Puertos de los servicios:**

| Servicio                    | Puerto |
|-----------------------------|--------|
| `circulacion-service`       | 8081   |
| `materiales-service`        | 8082   |
| `usuarios-service`          | 8083   |
| `multas-service`            | 8084   |
| `prestamos-externos-service`| 8085   |
| `notificaciones-service`    | 8086   |
| `reportes-service`          | 8087   |
| `reservas-service`          | 8088   |
| `cobros-service`            | 8089   |
| `cli-service`               | 8090   |
| Eureka Server               | 8761   |
| RabbitMQ UI                 | 15672  |

**Mensajería — RabbitMQ:**

- Exchange: `biblioteca.events` (tipo `topic`)
- Routing keys: `prestamo.registrado`, `material.devuelto`, `multa.generada`, `multa.pagada`, `reserva.notificada`, entre otros

**IDs generados por los servicios:**

| Entidad   | Formato      | Servicio origen  |
|-----------|--------------|------------------|
| Préstamo  | `PRE-XXXXXX` | circulacion      |
| Reserva   | `RES-XXXXXX` | reservas         |
| Multa     | `MUL-XXXXXX` | multas           |
| Material  | `MAT-XXXXXX` | CLI              |
| Usuario   | `USR-XXXXXX` | CLI              |

---

## Patrones de diseño

| Patrón | Implementación |
|--------|----------------|
| **Facade** | `BibliotecaFacade`, `ConsultaFacade`, `AdministracionFacade` |
| **Strategy** | `ICalculadorMulta` (cálculo de multas por tipo de usuario); `IReglaValidacion` (reglas de validación de préstamos) |
| **State** | `IEstadoPrestamo` → `PrestamoActivoState`, `PrestamoCompletadoState` |
| **Builder** | `MaterialBuilder`, `UsuarioBuilder`, `PrestamoBuilder` |
| **Abstract Factory** | `IRepositorioFactory` / `RepositorioEnMemoriaFactory` |
| **Repository** | `IRepositorio<T>` — interfaz CRUD genérica |
| **Value Object** | `Resultado<T>`, `Evaluacion`, `ContextoMulta` |

---

## Principios SOLID

| Principio | Dónde se aplica |
|-----------|-----------------|
| **S** — Single Responsibility | Cada servicio tiene una sola razón de cambio (`GestorMultasService` solo gestiona multas, `PrestamoService` solo gestiona préstamos) |
| **O** — Open/Closed | Nuevos tipos de multa → nueva impl de `ICalculadorMulta`; nuevas reglas → nueva impl de `IReglaValidacion`. Sin modificar código existente |
| **L** — Liskov Substitution | Las jerarquías abstractas `Material`, `Usuario` y `Prestamo` son sustituibles en todos los contextos donde se usan sus bases |
| **I** — Interface Segregation | `IPrestamoService`, `IDevolucionService` e `IReservaService` son interfaces separadas; ningún cliente depende de métodos que no usa |
| **D** — Dependency Inversion | Los servicios dependen de `IRepositorio<T>`; las fachadas dependen de interfaces de servicio. `Main.java` posee los bindings concretos |

---

## Testing y cobertura

Los tests cubren el **núcleo del dominio**: agregados, estados y estrategias — las capas donde viven los patrones SOLID. La cobertura baja en instrucciones refleja que la capa de infraestructura (controladores, repositorios, configuración Spring) no tiene tests unitarios, lo cual es intencional en esta fase.

### Monolito

```bash
cd biblioteca-backend/

# Ejecutar tests
mvn clean test

# Ejecutar una clase de test específica
mvn clean test -Dtest=PrestamoTest

# Generar reporte de cobertura (se abre en target/site/jacoco/index.html)
mvn clean test   # JaCoCo se ejecuta automáticamente en la fase test
```

**Resultados — `mvn clean test`:**

| Suite | Tests | Passed | Failed | Skipped |
|-------|------:|-------:|-------:|--------:|
| `PrestamoTest` | 9 | 9 | 0 | 0 |
| `ReservaTest` | 10 | 10 | 0 | 0 |
| `MultaTest` | 7 | 7 | 0 | 0 |
| `CalculadorMultaPorRetrasoTest` | 6 | 6 | 0 | 0 |
| **Total** | **32** | **32** | **0** | **0** |

**Cobertura JaCoCo — capa de dominio:**

| Métrica | Cobertura | Cubiertos | Total |
|---------|----------:|----------:|------:|
| Instrucciones | 7.9 % | 1 132 | 14 338 |
| Ramas | 3.3 % | 38 | 1 135 |
| Métodos | 7.6 % | 62 | 819 |
| Clases con al menos 1 test | — | 29 | 105 |

> El reporte HTML detallado se genera en `biblioteca-backend/target/site/jacoco/index.html`.

### Microservicios

Los tests de dominio se ejecutan sin necesitar el contexto Spring (tests unitarios puros). Solo los módulos con tests de dominio se incluyen en la ejecución.

```bash
cd microservicios/

# Ejecutar solo los módulos con tests
mvn test -pl circulacion-service,multas-service,reservas-service

# Reporte de cobertura por servicio (tras ejecutar los tests)
# circulacion-service/target/site/jacoco/index.html
# multas-service/target/site/jacoco/index.html
# reservas-service/target/site/jacoco/index.html
```

**Resultados por servicio:**

| Servicio | Suite de tests | Tests | Passed | Failed |
|----------|---------------|------:|-------:|-------:|
| `circulacion-service` | `PrestamoStateTest` | 7 | 7 | 0 |
| `circulacion-service` | `ValidacionReglasTest` | 11 | 11 | 0 |
| `multas-service` | `MultaStateTest` | 6 | 6 | 0 |
| `multas-service` | `CalculadorMultaStrategyTest` | 15 | 15 | 0 |
| `reservas-service` | `ReservaStateTest` | 9 | 9 | 0 |
| **Total** | | **48** | **48** | **0** |

**Cobertura JaCoCo por servicio:**

| Servicio | Instrucciones | Ramas | Métodos | Clases |
|----------|-------------:|------:|--------:|-------:|
| `circulacion-service` | 14.7 % | 21.6 % | 23.0 % | 27 |
| `multas-service` | 10.9 % | 10.7 % | 19.7 % | 22 |
| `reservas-service` | 7.7 % | 2.6 % | 16.6 % | 18 |

> Los reportes HTML se generan en `<servicio>/target/site/jacoco/index.html` tras ejecutar `mvn test`.

---

## Estructura de directorios

```
.
├── biblioteca-backend/
│   ├── src/
│   │   ├── main/java/com/biblioteca/
│   │   │   ├── Main.java                        # Raíz de composición (DI + mock data)
│   │   │   ├── consola/MenuConsola.java          # Menú interactivo
│   │   │   ├── dominio/
│   │   │   │   ├── entidades/                   # Agregados: Prestamo, Reserva, Multa, Usuario, Material
│   │   │   │   ├── estados/                     # State pattern: IEstadoPrestamo
│   │   │   │   ├── eventos/                     # Eventos de dominio (13 clases)
│   │   │   │   ├── excepciones/                 # Jerarquía checked: BibliotecaException
│   │   │   │   ├── objetosValor/                # Resultado<T>, Evaluacion, ContextoMulta
│   │   │   │   ├── builders/                    # Builder pattern
│   │   │   │   ├── factories/                   # ContextoCreacionPrestamo
│   │   │   │   ├── config/                      # ConfiguracionBiblioteca
│   │   │   │   └── enumeraciones/               # TipoUsuario, TipoMaterial, EstadoReserva...
│   │   │   ├── repositorios/                    # IRepositorio<T> + impl en memoria
│   │   │   └── servicios/
│   │   │       ├── interfaces/                  # IXxxFacade, IXxxService
│   │   │       └── implementaciones/            # PrestamoService, MultasService...
│   │   └── test/java/com/biblioteca/dominio/    # 4 suites, 32 tests
│   └── pom.xml
│
├── microservicios/
│   ├── biblioteca-commons/                      # Librería compartida: excepciones, eventos, value objects
│   ├── eureka-server/                           # Service registry (puerto 8761)
│   ├── circulacion-service/                     # Préstamos, devoluciones, renovaciones (8081)
│   ├── materiales-service/                      # CRUD de materiales (8082)
│   ├── usuarios-service/                        # CRUD de usuarios (8083)
│   ├── multas-service/                          # Generación y gestión de multas (8084)
│   ├── prestamos-externos-service/              # Préstamos interbibliotecarios (8085)
│   ├── notificaciones-service/                  # Notificaciones vía RabbitMQ + MongoDB (8086)
│   ├── reportes-service/                        # Estadísticas y reportes (8087)
│   ├── reservas-service/                        # Reservas en cola (8088)
│   ├── cobros-service/                          # Registro de pagos de multas (8089)
│   ├── cli-service/                             # CLI interactivo (perfil Docker: cli)
│   ├── docker-compose.yml
│   ├── pom.xml                                  # Pom padre multi-módulo
│   └── start.sh                                 # Script de arranque completo
│
└── docs/
    ├── analisis-microservicios.md
    ├── diagramas-arquitectura.md
    ├── migracion-microservicios-plan.md
    └── referencia-rapida-microservicios.md
```

---

## Variables de entorno

Las variables se configuran en `microservicios/docker-compose.yml`. Valores por defecto para desarrollo local:

| Variable | Valor por defecto | Descripción |
|----------|-------------------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Activa `DataInitializer` con datos mock |
| `RABBITMQ_HOST` | `rabbitmq` | Host del broker de mensajes |
| `EUREKA_URL` | `http://eureka-server:8761/eureka/` | URL del service registry |
| `DB_URL` | `jdbc:postgresql://<host>:5432/<db>` | Conexión PostgreSQL (servicios JPA) |
| `MONGODB_URI` | `mongodb://mongodb-notificaciones:27017/notificaciones_db` | Conexión MongoDB (notificaciones) |

> Con `SPRING_PROFILES_ACTIVE=dev`, cada servicio carga sus datos mock al primer arranque verificando `count() > 0` — no duplica si el contenedor se reinicia.

---
