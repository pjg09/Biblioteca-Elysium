# Arquitectura de Aplicaciones - TOGAF
**Curso:** Arquitectura de Aplicaciones  
**Profesor:** MSc. César Augusto López Gallego — cesar.lopezg@upb.edu.co  
**Universidad:** Universidad Pontificia Bolivariana — Medellín, Bucaramanga, Montería y Palmira

---

## Slide 1 — Portada
Curso de Arquitectura de Aplicaciones, UPB. Acreditación Institucional Multicampus Alta Calidad (Res. MEN No. 17228 del 24 de octubre de 2018 • 8 años).

---

## Slide 2 — Cambio

**Estructura del cambio:** uso de medios tecnológicos y desarrollo de la capacidad de respuesta para adaptarse al cambio.

**Cultura del cambio:** cambio en la sociedad y las personas. La ola rompe con las características de la Ola anterior: Familia nuclear, jornadas de trabajo, ubicación de trabajo, acceso a información.

**Aceleración del cambio:** lo hace complejo para que las organizaciones y las personas lo asimilen.

> ***Arquitectura Empresarial: Facilita la adaptación a los cambios***

*[Imagen decorativa: personas ensamblando piezas de puzzle/rompecabezas, simbolizando trabajo colaborativo ante el cambio]*

---

## Slide 3 — La Arquitectura Empresarial puede ser de utilidad cuando: (Parte 1)

Diagrama con 5 óvalos verdes:

1. **Diferentes áreas de la compañía dan diferentes respuestas a las mismas interrogantes.**
2. **Cumplir con los lineamientos y disposiciones de los entes reguladores demanda un gran esfuerzo e inversiones de infraestructura.**
3. **No existe agilidad para cumplir con las estrategias del negocio. Cada iniciativa que se requiere implementar es "como iniciar desde cero".**
4. **Las áreas de Tecnología se convierten en cuellos de botella.**
5. **Existen diferentes procesos de negocio, cada uno con diferentes sistemas y realizando la misma actividad a lo largo de la empresa.**

---

## Slide 4 — La Arquitectura Empresarial puede ser de utilidad cuando: (Parte 2)

Diagrama con 4 óvalos verdes adicionales:

1. **La información para tomar decisiones clave de negocio no está disponible.**
2. **Una buena parte del esfuerzo del trabajo de las personas se concentra en tomar información de algunos sistemas, procesarla, modificarla y luego ingresarla en otros sistemas.**
3. **La Alta Gerencia sufre un desgaste analizando aspectos relacionados con las Tecnologías de Información.**
4. **La Gerencia no sabe como obtener valor de las tecnologías de información.**

*Fuente: Arquitectura empresarial. Dux Diligens. Documento electrónico. Consultado 2018*

---

## Slide 5 — Arquitectura Empresarial

> "Una arquitectura empresarial (EA) es un modelo conceptual que define la estructura y el funcionamiento de las organizaciones. La intención de la arquitectura empresarial es determinar cómo una organización puede lograr de manera efectiva sus objetivos actuales y futuros. La arquitectura empresarial involucra la práctica de analizar, planificar, diseñar y eventualmente implementar el análisis en una empresa."  
> — *Techtarget, 2020*

**Diagrama de puzzle (6 piezas interconectadas):**
- Portafolio
- Estrategia
- Procesos
- Aplicaciones
- Aplicaciones de software
- Infraestructura Tecnológica

*Las piezas representan que todos estos componentes deben encajar de forma coherente en la Arquitectura Empresarial.*

---

## Slide 6 — Artefactos Arquitectura Empresarial

Cinco categorías de artefactos (presentadas como cajas rectangulares):

| Artefacto | Componentes |
|-----------|-------------|
| **Arquitectura del Negocio** | Metas, Estrategias, Roles, Ubicaciones, Drivers, Objetivos |
| **Arquitectura de Procesos** | Funciones, Actividades, Flujos, Ciclos, Procedimientos |
| **Arquitectura de Sistemas** | Aplicaciones, componentes de software, interfaces, proyectos |
| **Arquitectura de la Información** | Entidades, Relaciones, Atributos, Definiciones, Valores de Referencia |
| **Arquitectura de Tecnología** | Redes, Hardware, Software, Protocolos, Contenedores |

*Fuente: DMBOK. Dama*

---

## Slide 7 — Frameworks para diseñar Arquitectura Empresarial

Cuatro imágenes de frameworks (sin texto descriptivo, solo logos/diagramas):
1. **Zachman** — matriz tabular con filas y columnas (The Zachman Framework for Enterprise Architecture)
2. **TOGAF** — diagrama circular con fases (ADM)
3. **DoDAF** — tabla con vistas: Strategic Views, Technical Standards Views, Operational Views, System Views, Acquisition Views
4. **EABok** — pirámide triangular con capas (Core Architecture Data Model, Operational View, All View)

---

## Slide 8 — Frameworks Arquitectura Empresarial (Comparativa)

| Framework | Zachman | TOGAF | DoDAF | EABok | MINTIC |
|-----------|---------|-------|-------|-------|--------|
| **Propietario** | Zachman Institute | Open Group | Departamento de defensa de USA | MITRE's Center for Innovative Computing and Informatics | Ministerio de las TIC Colombia |
| **1era Versión** | 1987 | 1995 (llamada TAFIM) | 1996 (llamada C4ISR) | 2004 | 2018 |
| **Versión Actual** | 3.0 de 2011 | 10th | 2.02 de 2010 | Oct 2020, terminó operaciones | Versión Actual 2 de 2019 |

**Otros frameworks:** MoDAF, Obashi, SAP, Oracle

---

## Slide 9 — Framework TOGAF

*Slide de sección. Logo de "The Open Group" (verde y azul oscuro). Título: Framework TOGAF.*

---

## Slide 10 — TOGAF – Contextualización de la Arquitectura Empresarial

Dos círculos, uno azul y uno verde:

**Contexto 1 (círculo azul):**  
Descripción formal de un sistema o un plan detallado del sistema en un nivel de componentes para guiar su implementación.

**Contexto 2 (círculo verde):**  
Estructura de sus componentes, sus interacciones, las nociones y principios, así como las guías para la evolución en el tiempo.

---

## Slide 11 — Dominios TOGAF

**Diagrama en forma de casa/edificio con capas:**

**Techo (azul oscuro):**
> VISIÓN DE LA ARQUITECTURA (Alineamiento Estratégico)

**Cuatro columnas principales (cuerpo del edificio):**

| Arquitectura de Información (Datos) | Arquitectura de Negocio (Procesos) | Arquitectura Aplicaciones | Arquitectura Tecnológica |
|---|---|---|---|
| Describe la estructura de los datos físicos y lógicos de la organización y sus modelos de gestión. | Define la estrategia de negocio, la estructura organizacional y los procesos clave de la organización. | Provee la definición funcional para cada uno de los sistemas de información requeridos, las interacciones entre estos sistemas y sus relaciones con los procesos de negocio CORE de la organización. | Describe la estructura de hardware, software y comunicaciones requerida para dar soporte a la implantación de los sistemas de información. |

**Base (tres barras horizontales):**
- OPORTUNIDADES Y SOLUCIONES
- PORTAFOLIO DE PROYECTOS
- GOBIERNO DE IMPLEMENTACIÓN

*Fuente: Adaptación Colombia Digital*

---

## Slide 12 — TOGAF ADM — Architecture Development Method

**Descripción:**
- Es un proceso iterativo para desarrollar la arquitectura empresarial.
- Integrador de todos los elementos de TOGAF.
- Direcciona necesidades del negocio y TI para proveer:
  - Conjunto de vistas de la arquitectura
  - Conjunto de entregables
  - Metodología para manejar los requerimientos
  - Herramientas para desarrollar la arquitectura

**Diagrama circular del ADM (rueda con círculo central y 8 fases alrededor):**

- **Centro:** Requirements Management
- **Arriba:** Preliminary
- **A (arriba-derecha):** Architecture Vision
- **B (derecha):** Business Architecture
- **C (derecha-abajo):** Information Systems Architectures
- **D (abajo-derecha):** Technology Architecture
- **E (abajo):** Opportunities and Solutions
- **F (abajo-izquierda):** Migration Planning
- **G (izquierda):** Implementation Governance
- **H (izquierda-arriba):** Architecture Change Management

---

## Slide 13 — Arquitectura de Aplicaciones en TOGAF ©

Mismo diagrama circular del ADM con la fase **C (Information Systems Architectures)** resaltada en amarillo/dorado, indicando que es el foco del curso.

Título: **Arquitectura de Aplicaciones en TOGAF ©**

---

## Slide 14 — Arquitectura de Aplicaciones Basada en TOGAF - Objetivos

**Objetivos de la fase C:**

1. Desarrollar la arquitectura de Aplicaciones que habilita la Arquitectura del negocio y la visión de la arquitectura.
2. Atender las solicitudes para el trabajo de arquitectura y las preocupaciones de los interesados.
3. Identificar los componentes candidatos Aplicaciones para el roadmap de la arquitectura en función de las brechas entre las arquitecturas de Aplicaciones (As Is & To Be).

*[Imagen decorativa: casco de construcción amarillo con planos/blueprints]*

---

## Slide 15 — Arquitectura de Aplicaciones Basada en TOGAF - Entradas

Tres categorías de entradas (representadas como documentos/carpetas con doblez en esquina):

**1. Documentos Generales:**
- Framework arquitectura
- Especificación de la Arquitectura
- Visión de la Arquitectura
- Roadmap de la Arquitectura

**2. Borradores de definición de Arquitectura (línea base As Is – To Be):**
- Arquitectura de Negocio
- Arquitectura de Aplicaciones
- Arquitectura de Tecnología

**3. Especificación de Requerimientos:**
- Resultados de análisis de brecha
- Requerimientos técnicos relevantes
- Requerimientos de interoperabilidad
- Necesidades de cambio desde las áreas
- Restricciones tecnológicas a ser diseñadas
- Actualización de requerimientos de negocio
- Actualización de Aplicaciones

---

## Slide 16 — Arquitectura de Aplicaciones Basada en TOGAF - Actividades

**Flujo de actividades (diagrama de proceso con 6 pasos en forma de serpentina):**

```
[Obtener la línea base de la Arquitectura de Aplicaciones]
        ↓
[Desarrollar la Arquitectura de Aplicaciones objetivo]
        ↓
[Realizar Análisis de Brechas]
        ↓
[Definir los componentes candidatos para el roadmap]
        ↓
[Llevar a cabo la revisión formal con un interesado]
        ↓
[Construir o actualizar el documento de la arquitectura de Aplicaciones]
```

*(Los primeros 3 pasos son azul/verde, los últimos 3 son verde oscuro)*

---

## Slide 17 — Otra forma de ver lo anterior

**Diagrama Archimate de migración de arquitectura (notación Archimate):**

Elementos del diagrama:
- **Baseline architecture** (1) → rectángulo con líneas (arquitectura actual)
- **Gap baseline-target** (3) → elemento de brecha con símbolo de restricción
- **Plateau** (área rosa que contiene):
  - New hardware configuration (4)
  - Integrated back-office suite (9)
  - Hardware update (5)
  - Software modification (6)
  - Legacy outphasing (7)
  - Back-office system integration project (área/work package)
- **Deliverable** → etiqueta del entregable
- **Target architecture** (2) → rectángulo destino
- **Work package** → etiqueta inferior

> **Nota importante (estrella verde):** Esta notación pertenece a **Archimate**. Mayor nivel de abstracción.

---

## Slide 18 — Arquitectura de Aplicaciones Basada en TOGAF - Salidas

**5 entregables (representados como notas/post-its de colores):**

| Color | Salida |
|-------|--------|
| Azul | Cambios que se necesiten en la Especificación de la Arquitectura |
| Salmón | Componentes de la Arquitectura desarrollados para el Roadmap |
| Verde | Artefactos Actualizados |
| Gris | Nuevas políticas y definiciones para las Aplicaciones |
| Amarillo | Documento con la definición de la Arquitectura de Aplicaciones Actualizado |

---

## Slide 19 — Artefactos

Son metadatos de Información valiosos para entender todo lo relacionado con las aplicaciones de la organización.

Deben almacenarse y administrarse en un repositorio de artefactos de arquitectura empresarial. (DAMA)

**Tres tipos de artefactos (con iconos):**
- 🔧 **Diagramas** (ícono de compás de arquitecto)
- 📊 **Matrices** (ícono de tabla/cuadrícula)
- 🗄️ **Catálogos** (ícono de base de datos/cilindro)

---

## Slide 20 — Artefactos: Diagramas (Slide de sección)

*Slide de transición. Ícono verde de compás. Título: "Artefactos: Diagramas"*

---

## Slide 21 — Archimate

**Logo:** Hexágono azul con estrella blanca (logo oficial de Archimate)

**Descripción:**
- Lenguaje de modelado descriptivo que permite a los arquitectos empresariales describir, analizar y visualizar relaciones entre dominios de una arquitectura utilizando representaciones visuales fáciles de entender.
- Ayuda a representar las partes de la empresa y su operación e interacción.
- Fue creado y es mantenido por **OpenGroup.org**

*[Imagen decorativa: arquitecto animado con planos]*

---

## Slide 22 — TOGAF ADM - Archimate

**Diagrama de correspondencia entre fases ADM y capas Archimate:**

**Lado izquierdo — Rueda ADM (con agrupaciones):**
- **Strategy & Motivation:** Preliminary, A. Architecture Vision
- **Core:** B. Business Architecture, C. Information Systems Architecture, D. Technology Architecture
- **Implementation & Migration:** E. Opportunities and Solutions, F. Migration Planning, G. Implementation Governance, H. Architecture Change Management
- **Centro:** Requirements Management

**Lado derecho — Core Layers de Archimate:**
- **Business Layer** (corresponde a fase B)
- **Application Layer** (corresponde a fase C)
- **Technology Layer** (corresponde a fase D)

*(Las fases B, C, D están resaltadas con borde rosado punteado como "Core Layers")*

---

## Slide 23 — Elementos para representar en Archimate

**Diagrama de flor con 6 pétalos alrededor de un centro:**

- **Centro (amarillo):** Arquitectura Empresarial
- **Pétalo 1 (verde claro):** Estrategia
- **Pétalo 2 (verde):** Procesos
- **Pétalo 3 (verde):** Estructuras organizacionales
- **Pétalo 4 (azul claro):** Flujos de información
- **Pétalo 5 (azul claro):** Sistemas de TI
- **Pétalo 6 (azul grisáceo):** Infraestructuras técnicas y físicas

---

## Slide 24 — Estructura Archimate para implementar TOGAF completamente

**Tabla de capas y aspectos (matriz):**

| Capa | Passive structure | Behavior | Active structure | Motivation |
|------|-----------------|----------|-----------------|------------|
| **Strategy** | ✓ | ✓ | ✓ | ✓ |
| **Business** | ✓ | ✓ | ✓ | ✓ |
| **Application** | ✓ | ✓ | ✓ | ✓ |
| **Technology** | ✓ | ✓ | ✓ | ✓ |
| **Physical** | ✓ | ✓ | ✓ | — |
| **Implementation & Migration** | ✓ | ✓ | ✓ | — |

*(Eje horizontal = Aspects, Eje vertical = Layers)*

**Notas laterales:**
- Las capas superiores usan servicios proveídos por las capas inferiores.
- Se denominan **capas de core** a: Capa de Negocio, Capa de Aplicaciones, Capa de Tecnología.

**Cuatro tipos de estructuras:**

- **Las estructuras activas** — sujetos que muestran un comportamiento real (¿quién?). Se representan mediante cuadros con esquinas cuadradas y un icono en la esquina superior derecha.
- **Estructura de comportamiento** — representan los comportamientos de las estructuras activas (¿cómo?). Se representan utilizando cuadros con esquinas redondeadas y un icono en la esquina superior derecha.
- **Las estructuras pasivas** — son los objetos en los que se realiza el comportamiento (¿qué?). Frecuentemente, objetos de Aplicaciones e información u objetos físicos.
- **Los conceptos motivacionales** — se utilizan para modelar las motivaciones, o razones, que subyacen en el diseño o cambio de alguna arquitectura empresarial.

---

## Slide 25 — Notación y Elementos Arquitectónicos Básicos (Slide de sección)

*Slide de transición. Logo Archimate (hexágono azul). Título: "Notación y Elementos Arquitectónicos Básicos"*

---

## Slide 26 — Elementos Motivación

**Tabla de elementos con iconos Archimate:**

| Elemento | Icono | Descripción |
|----------|-------|-------------|
| **Stakeholder** | Rectángulo con símbolo de persona y doble línea derecha | Individuo, equipo u organización que representa sus intereses en el resultado de la arquitectura. |
| **Driver** | Rectángulo con símbolo de engranaje | Condición externa o interna que motiva a una organización a definir sus objetivos e implementar los cambios necesarios para lograrlos. |
| **Assessment** | Rectángulo con símbolo de lupa | El resultado de un análisis del estado de cosas de la empresa con respecto a algún conductor. |
| **Goal** | Rectángulo con símbolo de diana/objetivo | Declaración de alto nivel de intención, dirección o estado final deseado para una organización y sus partes interesadas. |
| **Outcome** | Rectángulo con símbolo de bandera | Resultado final que se ha o se espera ser logrado. |
| **Principle** | Rectángulo con símbolo de exclamación | Declaración cualitativa de intenciones que debe cumplir la arquitectura. |
| **Requirement** | Rectángulo con esquina doblada | Declaración de necesidad que debe cumplir la arquitectura. |
| **Constraint** | Rectángulo con esquina doblada y línea | Factor que impide u obstruye la realización de las metas. |
| **Meaning** | Nube | El conocimiento o experiencia presente en, o la interpretación dada a, un elemento central en un contexto particular. |
| **Value** | Óvalo | Utilidad o la importancia de un elemento central o un resultado. |

---

## Slide 27 — Relaciones Archimate — Dependency Relationships

**Tabla de relaciones de dependencia:**

| Name | Representation | Descripción |
|------|---------------|-------------|
| **Serving** | Línea sólida con flecha → | Modela que un elemento proporciona su funcionalidad a otro elemento. (Uso de servicios por procesos, funciones o interacciones. Acceso a interfaces por roles, componentes o colaboraciones) |
| **Access** | Línea punteada con flecha → o ← | Representa un elemento de comportamiento accediendo a un elemento pasivo (objeto de negocio o datos). |
| **Influence** | Línea punteada con flecha → (dentro de óvalo) | Modela que un elemento afecta la implementación o el logro de algún elemento de motivación. (Actúan sobre elementos de motivación) |

> **Relación Dinámica:** describe dependencias temporales entre elementos dentro de la arquitectura.

---

## Slide 28 — Relaciones Archimate — Structural Relationships

Modelan la construcción o composición estática de conceptos del mismo o diferente tipo.

**Tabla de Structural Relationships:**

| Name | Representation | Descripción |
|------|---------------|-------------|
| **Composition** | Línea con rombo relleno ◆ | El elemento al final con el rombo relleno es el padre. El hijo NO puede existir independientemente del padre. (Consiste en) |
| **Aggregation** | Línea con rombo sin relleno ◇ | El elemento al final con el rombo sin relleno es el padre. El hijo SÍ puede existir independientemente del padre. (Agrupa) |
| **Assignment** | Línea con punto lleno •→ | El lado con el punto expresa la atribución de responsabilidad, realización de conducta, o ejecución sobre el elemento en el lado con la punta de flecha. |
| **Realization** | Línea punteada con punta abierta →▷ | El elemento sin punta de flecha es el que crea/logra/sustenta/opera una entidad más abstracta conectada con punta de flecha. Conecta una entidad lógica con otra entidad más concreta que la realiza. |

---

## Slide 29 — Relaciones Archimate — Dynamic & Other Relationships

**Dynamic Relationships:**

| Name | Representation | Descripción |
|------|---------------|-------------|
| **Triggering** | Línea sólida con flecha → | Describe relación causal entre dos elementos. (Entre procesos, funciones, interacciones y eventos) |
| **Flow** | Línea punteada con flecha --→ | Representa algo que fluye o se transfiere entre dos elementos. (Información entre procesos, funciones, interacciones y eventos) |

**Other Relationships:**

| Name | Representation | Descripción |
|------|---------------|-------------|
| **Specialization** | Línea sólida con punta abierta →▷ | Indica que un elemento es una clase particular de otro elemento. |
| **Association** | Línea sólida sin flecha — | Representa una relación no especificada o que no se puede representar con otros elementos. |
| **Junction** | Círculo relleno ● (And) / Círculo vacío ○ (Or) | And Junction / Or Junction |

---

## Slide 30 — Relaciones Archimate — Taxonomía Completa

**Árbol jerárquico de todos los conceptos:**

```
Concept
├── Relationship
│   ├── Structural relationship
│   │   ├── Realization
│   │   ├── Assignment
│   │   ├── Aggregation
│   │   └── Composition
│   ├── Dependency relationship
│   │   ├── Influence
│   │   ├── Access
│   │   └── Serving
│   ├── Dynamic relationship
│   │   ├── Triggering
│   │   └── Flow
│   └── Other relationship
│       ├── Specialization
│       └── Association
├── Element
└── Relationship Connector
    ├── (And) Junction
    └── Or Junction
```

---

## Slide 31 — Ejemplo Archimate — Diagrama de Motivación

**Diagrama de motivación con elementos Archimate (notación Assessment/Driver/Goal/Outcome):**

```
[Lack of Insight in Portfolio] (Assessment) ──────── [Employee Costs too High] (Assessment)
         │                                                        │
         │                                              [Reduce Workload Employees] (Goal) ◎
         │                                                        ◇
         │                                         ┌─────────────────────────────┐
         │                                [Reduce Interaction with Customer] ◎  [Reduce Manual Work] ◎
         │                                         ◇
         │                          ┌──────────────────────────┐
         │                [Facilitate Self-Service] ◎    [Make Customer Interaction More Effective] ◎
         │                          △ (Realization punteada)
         ▼
[Improve Portfolio Management] ◎
         △ (Realization punteada desde abajo)
┌──────────────────────────────────────────┐
[Assign Personal Assistant]  [Provide Online Portfolio Service]  [Provide Online Information Service]
```

*Fuente: https://archimate.visual-paradigm.com/*

---

## Slide 32 — Elementos Negocio (Vista general con iconos)

**Capa de Negocio — tres categorías:**

**Representa E. Activa (circulos rojos):**
- `Actor` — rectángulo con ícono de persona
- `Role` — rectángulo con doble línea horizontal superior
- `Collaboration` — rectángulo con doble círculo

**Representa E. Comportamiento (óvalos verdes):**
- `Process` — rectángulo redondeado con flecha →
- `Function` — rectángulo redondeado con circunflejo ^
- `Interaction` — rectángulo redondeado con doble D
- `Event` — pentágono con vértice izquierdo
- `Service` — óvalo simple

**Representa E. Pasiva (óvalos azules):**
- Interfaz (círculo pequeño)
- `Object` — rectángulo simple
- `Product` — rectángulo con pestaña superior izquierda
- `Contract` — rectángulo con línea
- `Representation` — rectángulo con línea inferior

---

## Slide 33 — Elementos Negocio (Definiciones)

| Elemento | Icono | Definición |
|----------|-------|-----------|
| **Actor** | Rectángulo con persona | Entidad que es capaz de realizar un comportamiento. |
| **Role** | Rectángulo con doble línea | Papel que desempeña un actor. |
| **Collaboration** | Rectángulo con doble círculo | Conjunto de dos o más elementos de la estructura activa interna de la empresa que trabajan juntos para realizar un comportamiento colectivo. |
| **Process** | Rectángulo redondeado con flecha | Secuencia de comportamientos para lograr un resultado específico como productos o servicios. |
| **Function** | Rectángulo redondeado con ^  | Comportamiento de un conjunto elegido de criterios estrechamente alineados con una organización, pero no necesariamente gobernados por ésta. |
| **Interaction** | Rectángulo redondeado con doble D | Unidad de comportamiento colectivo realizada por la colaboración de dos o más roles. |
| **Event** | Pentágono | Denota un cambio en el estado de la organización. Puede originarse y resolverse dentro o fuera de la organización. |
| **Service** | Óvalo | Un comportamiento expuesto definido explícitamente. |
| Interface | Círculo pequeño | Un punto de acceso en el que un servicio se pone a disposición del entorno. |
| **Object** | Rectángulo | Concepto utilizado dentro de un dominio particular. |
| **Product** | Rectángulo con pestaña | Colección coherente de servicios y/o elementos de estructura pasiva, acompañada de un contrato/conjunto de acuerdos, que se ofrece a clientes internos o externos. |
| **Contract** | Rectángulo con línea | (Acuerdo formal) |
| **Representation** | Rectángulo con base | Forma perceptible de la información transportada por un objeto. |

---

## Slide 34 — Ejemplos de Relaciones Estructurales en Capa de Negocio

**Composición:**
```
Financial Processing ◆── Accounting
                    ◆── Payment  
                    ◆── Billing
```
*(Representación alternativa: Financial Processing como contenedor con Accounting, Payment, Billing dentro)*

**Agregación:**
```
Customer File ◇── Insurance Policy
             ◇── Insurance Claim
```
*(Representación alternativa: Customer File como contenedor)*

**Asignación:**
```
Payment Interface ──•→ Payment Service
```

**Realización:**
```
Transaction Processing ···▷ Billing Service ──→ Billing Data
                            ···▷
                       Paper Invoice
```
*(Es realizado por)*

---

## Slide 35 — Elementos Aplicaciones, Tecnología

**Capa de Aplicaciones (izquierda, bordes rojos=activos, verdes=comportamiento, azul=pasivo):**

| Categoría | Elementos |
|-----------|-----------|
| E. Activa (rojo) | Collaboration, Component |
| E. Comportamiento (verde) | Service, Function, Interaction, Process, Event |
| E. Pasiva (azul) | Data |
| Interface | Círculo pequeño |

**Capa de Tecnología (derecha):**

| Categoría | Elementos |
|-----------|-----------|
| E. Activa | Node, Device, Software |
| E. Comportamiento | Function, Service, Collaboration, Interaction, Event, Process |
| E. Pasiva | Artifact, Path (←--→), Network (↔) |

---

## Slide 36 — Elementos Aplicaciones (Definiciones)

| Elemento | Icono | Definición |
|----------|-------|-----------|
| **Collaboration** | Rectángulo con doble círculo | Dos o más componentes de aplicaciones que trabajan juntos para realizar un comportamiento de aplicación colectivo. |
| **Component** | Rectángulo con símbolo de componente (doble cuadrado) | Funcionalidad encapsulada de la aplicación alineada con la estructura de implementación, es modular y reemplazable. Encapsula su comportamiento y datos, expone servicios y los pone a disposición a través de interfaces. |
| **Service** | Óvalo | Un comportamiento de aplicación expuesto definido explícitamente. |
| **Function** | Rectángulo redondeado con ^ | Comportamiento automatizado que puede realizar un componente de la aplicación. |
| **Interaction** | Rectángulo redondeado con doble D | Unidad de comportamiento colectivo de la aplicación realizada por (una colaboración de) dos o más componentes de la aplicación. |
| Interface | Círculo pequeño | Punto de acceso donde los servicios de la aplicación están disponibles. |
| **Process** | Rectángulo redondeado con → | Secuencia de comportamientos de aplicación que logra un resultado específico. |
| **Event** | Pentágono | Elemento de comportamiento de la aplicación que denota un cambio de estado. |
| **Data** | Rectángulo | Datos estructurados (y no estructurados) para su tratamiento automatizado. |
| **Application interface** | Rectángulo azul con círculo | Una interfaz de aplicación representa un punto de acceso donde los servicios de aplicación se ponen a disposición de un usuario, otro componente de la aplicación o un nodo. |

---

## Slide 37 — Ejemplos Archimate — Capa de Aplicaciones

**Ejemplo 1 (The Open Group — arriba izquierda): Composición y Asignación**
```
Travel Website (Component)
    │ ↑ (Assignment)
    │
Web Services Interface (Application Interface) ──○
    ◆ (Composition)
Online Travel Insurance Sales (Component) ◇── Quotation (Data)
                                          ◇── Purchase (Data)
```

**Ejemplo 2 (The Open Group — arriba derecha): Servicios y Realización**
```
Request for a Quotation (Event) → Obtain Travel Insurance (Process)
                                    △
                        ┌─────────────────────┐
                  Get Quotation (Service)   Purchase Quoted Insurance (Service)
                        △                         △
              ┌─────────────────┐      ┌────────────────────┐
        Transfer Quotation    Finalize Purchase
        (Function)            (Function)
                └── Purchase Travel Insurance (Collaboration) ──┘
```

**Ejemplo 3 (abajo): Especialización**
```
Online Insurance Quotation ←── Auto Insurance Quotation (Specialization)
                           ←── Travel Insurance Quotation (Specialization)

Online Insurance Quotation ◆── Quoted Price
                           ◆── Terms and Conditions
                           ◆── Certificate of Authenticity
                           ◆── Purchased Itinerary
```

*Fuente: https://pubs.opengroup.org/architecture/archimate31-doc/chap09.html*

---

## Slide 38 — Ejemplo Archimate — Diagrama Multi-capa (Mastering ArchiMate)

**Diagrama completo cruzando capas de Motivación, Negocio, Aplicación y Tecnología:**

```
Business (Stakeholder) ─────────────────── IT Run (Stakeholder)
        │                                           │
[Our IT Cost is 25% above industry average]  [Major New Competitor]
[10% YoY Customer Churn Growth]              (Assessments)
(Assessments)
        └──────────► Increasing World-wide Competition (Driver) ◄──────┘

24/7 Operation (Goal) ◎ ─────────────── Low IT Run Cost (Goal) ◎
        │                                           │
        ++ (influence positive)               -- (influence negative)
        │
Fully Automated Business Process A (Requirement) ◄── (conflicts con Low IT Run Cost)
        △ (Realization)
        │
        A (Business Process) ──────────────────────────────────
        △                                                       │
        │                                                       │
A System (Application Component) ──● A Service (Application Service)
        △ (Realization)                    △ (Realization)
        │                                  │
High Available IT Infrastructure    An Infrastructure Service
(Requirement: ++) ◄───────────────── (Technology Service)
                                           │
                                    Standardized OS (Requirement: ++)
```

*Fuente: Mastering ArchiMate — Gerben Wierda, 2017*

---

## Slide 39 — Elementos Tecnología (Definiciones)

| Elemento | Icono | Definición |
|----------|-------|-----------|
| **Node** | Cubo 3D con cuadrado | Recurso computacional o físico que aloja, manipula o interactúa con otros recursos computacionales o físicos. |
| **Device** | Cubo 3D con símbolo monitor | Dispositivo de TI físico sobre el cual el software del sistema y los artefactos pueden almacenarse o implementarse para su ejecución. |
| **Software** | Cubo 3D con símbolo circular | Software que proporciona o contribuye a un entorno para almacenar, ejecutar y usar software o datos implementados en él. |
| Interface | Círculo pequeño | Punto de acceso donde se puede acceder a los servicios tecnológicos ofrecidos por un nodo. |
| **Function** | Rectángulo redondeado con ^ | Colección de comportamiento tecnológico que puede realizar un nodo. |
| **Service** | Óvalo | Comportamiento de tecnología expuesto explícitamente definido. |
| **Collaboration** | Rectángulo con doble círculo | Conjunto de dos o más nodos que trabajan juntos para realizar un comportamiento tecnológico colectivo. |
| **Interaction** | Rectángulo redondeado con doble D | Unidad de comportamiento tecnológico colectivo realizado por (una colaboración de) dos o más nodos. |
| **Event** | Pentágono | Elemento de comportamiento tecnológico que denota un cambio de estado. |
| **Process** | Rectángulo redondeado con → | Secuencia de comportamientos tecnológicos que logra un resultado específico. |
| **Artifact** | Rectángulo con pestaña de documento | Pieza de datos que se utiliza o se produce en un proceso de desarrollo de software, o por la implementación y operación de un sistema. |
| Path | ←---→ | Ruta de comunicación. Un enlace entre dos o más nodos, a través del cual estos pueden intercambiar datos. |
| Network | ↔ | Red. Un conjunto de estructuras que conecta sistemas informáticos u otros dispositivos electrónicos para la transmisión, enrutamiento y recepción de datos o comunicaciones basadas en datos, como voz y video. |

---

## Slide 40 — Ejemplo Archimate — Capa de Tecnología

**Diagrama de relaciones entre elementos tecnológicos:**

```
(Technology Service) ←──────── (Technology Interface) ──○
        △                               │ (Composition ◆)
        │                               ▼
(Technology Process)              (Node) ◄──── (Technology Collaboration)
        △                         △  ▷  ▷
        └─────────────────────────┘
                                  △          △
                           (Device)    (System Software)
                              △               △
                       (Facility)         (Equipment)
```

*Fuente: Mastering ArchiMate — Gerben Wierda, 2017*

---

## Slide 41 — Ejemplo Archimate — Diagrama Multi-capa App + Tecnología

**Capas: Aplicación (amarillo) + Tecnología (azul/verde)**

```
[App A] Application Component ──→ [App A] Application Function ──→ [App A] Some data (Data Object)
        △ (Realization)                   △ (Realization)                  △
        │ (punteada)                      │ (punteada)                      │
        ↓                         [x86dev001] Oracle db001                  │
[x86dev001/db001]                    (Technology Service)                   │
Oracle Database (Artifact)                  △ (Realization)                 │
[x86dev001/db001]                           │                               │
App A PL*SQL code (Artifact)         [x86dev001] RHEL (Node)                │
                                      │                    │                │
                               [x86dev001]           [x86dev001]            │
                               Oracle RDBMS            RHEL                 │
                               (System Software)   (System Software)        │
                                      △                    │                │
                               [x86dev001]                 ←────────────────┘
                               x86 server (Device)
```

*Fuente: Mastering ArchiMate — Gerben Wierda, 2017*

---

## Slide 42 — Ejemplo Archimate — Diagrama Completo Multi-capa (Insurance)

**Diagrama completo de sistema de seguros cruzando Business + Application + Technology:**

**Capa de Negocio (amarillo):**
```
Insurant (Role) ←──── Customer (Actor)          ArchiSurance (Actor)
     △                                                  │
Claims Registration (Service)                      Insurer (Role)
Claims Acceptance (Service)                             │
Claims Payment (Service)                                │
     △ (realizados por)                                 │
Process Claims (Process) ─────────────────────────────→│
  ├── Register (Function)                               │
  ├── Accept (Function)     ←── Customer Information (Object)
  ├── Adjudicate (Function)
  └── Pay (Function)
```

**Capa de Aplicación (azul):**
```
Customer Data Management (App Service) ←── CRM System (Component)
Payment Processing (App Service) ←── Financial Application (Component)
CRM System ←── Customer Data (Data) ←── Customer Database Tables (Artifact)
Financial Application ←── Application Hosting (App Service) ←── Financial Application Web Archive (Artifact)
```

**Capa de Tecnología (verde):**
```
Database Management System (System Software) ←── Blade System (Device) ──→ Application Server (System Software)
Database Management (Technology Service) ←── Database Access Java Archive (Artifact)
Application Hosting ←── Application Server
```

*Fuente: https://archimate.visual-paradigm.com/*

*Leyendas del diagrama:*
- **Active Structure** = Actor, Role, Component (rectángulos con icono)
- **Service** = Óvalos
- **Process (Behavior)** = Rectángulos redondeados con flecha
- **Application Component** = Rectángulo con ícono de componente (doble cuadrado)
- **Passive Structure** = Rectángulos sin icono (datos, objetos)

---

## Slide 43 — Ejemplo Archimate — Diagrama CI/CD Pipeline Completo

**Diagrama completo de pipeline de entrega de software (Business + Application + Technology)**

**Capa de Negocio — "Deliver Feature" (proceso principal):**
```
Develop Feature → Test (Local Commit) → Inspect/Manually Test → Commit → 
Test (Commit Stage) --passed--> Deploy Artifact → Deploy App to Staging → 
Test (Acceptance) --passed--> Deploy App to Production
```

**Actores:** LDI Member (izquierda y derecha), Software Developer (izquierda y derecha), Customer

**Software Development Service** (servicio expuesto al Customer)

**Capa de Aplicación:**
| Etapa | Herramientas |
|-------|-------------|
| Develop Feature | Mybatis Migrations, IntelliJ IDEA/NetBeans IDE, GlassFish App Server, Maven (Automated Builds) |
| Inspect/Manually Test | MyApp (Local Test), LdiApp Test Database |
| Commit | MyApp Source Code, MyApp Unit Tests, MyApp Acceptance Tests, MyApp Build Scripts |
| Deploy Artifact (Jenkins CI) | Maven (Automated Builds), SONAR (Code Quality), Artifactory (Artifact Repository) |
| Deploy App to Staging | GlassFish Admin Console, Selenium (Auto Acceptance), MyApp (Staging) |
| Deploy App to Production | GlassFish Admin Console, Mybatis Migrations, MyApp |

**Capa de Tecnología:**
| Servidor | Componentes |
|----------|------------|
| Developer PC | Mybatis Migrations ○, IntelliJ IDEA/NetBeans ○, Maven ○, GlassFish ○, MySQL ○ |
| Code Server | Subversion ○ |
| Build Server | Maven ○, GlassFish ○ |
| Staging Server | GlassFish ○, MySQL ○, Maven ○, Selenium ○ |
| Production Server | GlassFish ○, MySQL ○ |

*(○ = System Software en Archimate)*

---

## Slide 44 — Referencias

- https://pubs.opengroup.org/architecture/archimate31-doc/chap09.html
- https://www.dragon1.com/images/archimate-landscape-overview.png
- https://agileea.com/services/remote-services/modelling-as-a-service/archimate-model-types/
- Mastering ArchiMate — Gerben Wierda, 2017

---

## RESUMEN EJECUTIVO

### Temas principales cubiertos:

1. **Arquitectura Empresarial (EA)** — qué es, cuándo es útil, sus dominios y artefactos.
2. **Frameworks** — Zachman, TOGAF, DoDAF, EABok, MINTIC (comparativa histórica y características).
3. **TOGAF ADM** — 9 fases del ciclo iterativo (Preliminary, A-H + Requirements Management).
4. **Fase C — Information Systems Architecture** — objetivos, entradas, actividades, salidas.
5. **Archimate** — lenguaje de modelado oficial de TOGAF:
   - Estructura de capas (Strategy, Business, Application, Technology, Physical, Implementation)
   - Aspectos (Active Structure, Behavior, Passive Structure, Motivation)
   - **Elementos de Motivación:** Stakeholder, Driver, Assessment, Goal, Outcome, Principle, Requirement, Constraint, Meaning, Value
   - **Elementos de Negocio:** Actor, Role, Collaboration, Process, Function, Interaction, Event, Service, Object, Product, Contract, Representation
   - **Elementos de Aplicación:** Collaboration, Component, Service, Function, Interaction, Interface, Process, Event, Data, Application Interface
   - **Elementos de Tecnología:** Node, Device, Software, Interface, Function, Service, Collaboration, Interaction, Event, Process, Artifact, Path, Network
   - **Relaciones:** Structural (Composition, Aggregation, Assignment, Realization), Dependency (Serving, Access, Influence), Dynamic (Triggering, Flow), Other (Specialization, Association, Junction)
6. **Ejemplos prácticos** de diagramas Archimate en contextos reales (seguros, CI/CD, sistemas multi-capa).
