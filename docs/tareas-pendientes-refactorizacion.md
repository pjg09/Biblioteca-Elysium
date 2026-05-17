# Tareas Pendientes: Refactorización SOLID y Patrones

**Generado**: 17 de mayo de 2026  
**Prioridad**: Ordenadas por criticidad e impacto

---

## 🔴 CRÍTICO - Debe hacerse antes que cualquier otra cosa

### 1. Actualizar CirculacionService para manejar nuevas excepciones de State Pattern

**Archivo**: `microservicios/circulacion-service/src/main/java/com/biblioteca/circulacion/aplicacion/CirculacionService.java`

**Cambios necesarios**:
- En método `renovarPrestamo()`: manejar `OperacionNoPermitidaEnEstadoException`
- En método `devolverPrestamo()`: manejar `OperacionNoPermitidaEnEstadoException`
- En método que cuenta "prestamos activos": cambiar `"ACTIVO"` por `prestamo.isActivo()` (ya existe el método)

**Ejemplo**:
```java
@Transactional
public ResultadoOperacion renovarPrestamo(String prestamoId, LocalDateTime nuevaFecha, int maxRenovaciones) {
    Optional<PrestamoEntity> optPrestamo = prestamoRepository.findById(prestamoId);
    if (!optPrestamo.isPresent()) {
        return ResultadoOperacion.fallido("Préstamo no encontrado");
    }
    
    PrestamoEntity entity = optPrestamo.get();
    Prestamo prestamo = entity.toDomain(); // Convertir Entity a Domain
    
    try {
        prestamo.renovar(nuevaFecha, maxRenovaciones);
        // Guardar cambios
        entity.estado = prestamo.getEstado(); // Actualizar entity con nuevo estado
        prestamoRepository.save(entity);
        return ResultadoOperacion.exitoso("Préstamo renovado");
    } catch (OperacionNoPermitidaEnEstadoException e) {
        return ResultadoOperacion.fallido(e.getMessage());
    }
}
```

**Riesgo si no se hace**: Compilación fallará al llamar a métodos refactorizados de Prestamo

---

## 🟡 ALTO - Próximas 2-3 horas de trabajo

### 2. Implementar PrestamoBuilder en circulacion-service

**Archivo a crear**: `microservicios/circulacion-service/src/main/java/com/biblioteca/circulacion/dominio/builders/PrestamoBuilder.java`

**Patrón**: Igual que UsuarioBuilder y MaterialBuilder

**Validaciones necesarias**:
- ID no nulo
- ID Usuario no nulo
- ID Material no nulo
- Fecha de devolución no en el pasado
- Tipo de préstamo válido (NORMAL | INTERBIBLIOTECARIO)
- Sede no nula

**Integración**: Usar en `registrarPrestamo()` después de las validaciones de disponibilidad

### 3. Implementar ReservaBuilder en reservas-service

**Archivo a crear**: `microservicios/reservas-service/src/main/java/com/biblioteca/reservas/dominio/builders/ReservaBuilder.java`

**Patrón**: Igual que UsuarioBuilder

**Validaciones necesarias**:
- ID no nulo
- ID Usuario no nulo
- ID Material no nulo
- Posición en cola >= 1
- Estado inicial válido

---

## 🟡 ALTO - Refactorización de ciclos de vida

### 4. Implementar IEstadoReserva y sus estados en reservas-service

**Archivo a crear**: `microservicios/reservas-service/src/main/java/com/biblioteca/reservas/dominio/estados/IEstadoReserva.java`

**Estados necesarios**:
- `ReservaEnEsperaState`: Está en la cola
- `ReservaNotificadaState`: Ha sido notificada de disponibilidad
- `ReservaCompletadaState`: Material recogido
- `ReservaCanceladaState`: Cancelada
- `ReservaExpiradaState`: Expiró

**Métodos principales**:
- `notificar(LocalDateTime fechaNotificacion, ReservaContexto contexto)`
- `cancelar(ReservaContexto contexto)`
- `expirar(ReservaContexto contexto)`
- `recoger(ReservaContexto contexto)`

### 5. Implementar IEstadoMulta y sus estados en multas-service

**Archivo a crear**: `microservicios/multas-service/src/main/java/com/biblioteca/multas/dominio/estados/IEstadoMulta.java`

**Estados necesarios**:
- `MultaGeneradaState`: Multa acaba de crearse
- `MultaPagadaState`: Multa fue pagada
- `MultaCondonadaState`: Multa fue condonada

**Métodos principales**:
- `pagar(LocalDateTime fechaPago, double monto, MultaContexto contexto)`
- `condonar(String motivo, MultaContexto contexto)`

---

## 🟢 MEDIO - Facades para simplificar

### 6. Crear CirculacionFacade

**Archivo a crear**: `microservicios/circulacion-service/src/main/java/com/biblioteca/circulacion/aplicacion/fachadas/ICirculacionFacade.java` (interfaz)

**Responsabilidades**:
- Orquestar PrestamoService, DevolucionService, RenovacionService
- Validar reglas de negocio antes de delegar
- Manejar transacciones distribuidas

**Interfaz básica**:
```java
public interface ICirculacionFacade {
    Resultado<PrestamoDTO> registrarPrestamo(RegistrarPrestamoRequest request);
    Resultado<Void> renovarPrestamo(String prestamoId, RenovarPrestamoRequest request);
    Resultado<Void> devolverPrestamo(String prestamoId, DevolverMaterialRequest request);
}
```

### 7. Crear UsuariosFacade

**Archivo a crear**: `microservicios/usuarios-service/src/main/java/com/biblioteca/usuarios/aplicacion/fachadas/IUsuariosFacade.java`

**Responsabilidades**:
- Orquestar UsuarioService + UsuarioBuilder
- Unificar CRUD con validaciones

### 8. Crear MaterialesFacade

**Archivo a crear**: `microservicios/materiales-service/src/main/java/com/biblioteca/materiales/aplicacion/fachadas/IMaterialesFacade.java`

**Responsabilidades**:
- Orquestar MaterialService + MaterialBuilder
- Unificar CRUD con validaciones

---

## 🟢 MEDIO - Implementar Strategy Pattern

### 9. Implementar ICalculadorMulta en multas-service

**Archivos a crear**:
- `multas-service/src/main/java/com/biblioteca/multas/dominio/calculadores/ICalculadorMulta.java`
- `multas-service/src/main/java/com/biblioteca/multas/dominio/calculadores/CalculadorMultaEstudiante.java`
- `multas-service/src/main/java/com/biblioteca/multas/dominio/calculadores/CalculadorMultaProfesor.java`
- `multas-service/src/main/java/com/biblioteca/multas/dominio/calculadores/CalculadorMultaInvestigador.java`

**Interfaz**:
```java
public interface ICalculadorMulta extends IStrategy<ContextoMulta, Double> {
    double calcular(ContextoMulta contexto);
    
    @Override
    default Double ejecutar(ContextoMulta parametro) {
        return calcular(parametro);
    }
}
```

**Integración**: `MultaService.calcularMulta()` selecciona estrategia según `tipoUsuario`

### 10. Implementar IReglaValidacion en circulacion-service

**Archivos a crear**:
- `circulacion-service/src/main/java/com/biblioteca/circulacion/dominio/reglas/IReglaValidacion.java`
- `circulacion-service/src/main/java/com/biblioteca/circulacion/dominio/reglas/ReglaUsuarioActivo.java`
- `circulacion-service/src/main/java/com/biblioteca/circulacion/dominio/reglas/ReglaMaterialDisponible.java`
- `circulacion-service/src/main/java/com/biblioteca/circulacion/dominio/reglas/ReglaLimitePrestamos.java`
- `circulacion-service/src/main/java/com/biblioteca/circulacion/dominio/reglas/ReglaUsernoMoroso.java`

---

## 🟣 BAJA - Limpieza y Documentación

### 11. Crear tests unitarios para validar State Pattern

**Archivos a crear**:
- `circulacion-service/src/test/java/com/biblioteca/circulacion/dominio/estados/PrestamoEstadosTest.java`
- `reservas-service/src/test/java/com/biblioteca/reservas/dominio/estados/ReservaEstadosTest.java`
- `multas-service/src/test/java/com/biblioteca/multas/dominio/estados/MultaEstadosTest.java`

**Casos de prueba**:
```java
@Test
public void testPrestamoActivoNoPermiteDevolverleDospVeces() {
    // Crear préstamo activo, devolverlo, intentar devolver nuevamente
    // Debe lanzar OperacionNoPermitidaEnEstadoException
}
```

### 12. Documentar flujos de negocio con State Pattern

**Archivo a crear**: `docs/diagramas-estado-dominio.md`

**Contenido**: Diagramas Mermaid mostrando transiciones de estado para:
- Préstamo
- Reserva
- Multa

---

## 📋 Checklist de Implementación

### Día 1
- [ ] Actualizar CirculacionService
- [ ] Implementar PrestamoBuilder
- [ ] Implementar ReservaBuilder

### Día 2
- [ ] Implementar IEstadoReserva + estados
- [ ] Implementar IEstadoMulta + estados
- [ ] Implementar CirculacionFacade

### Día 3
- [ ] Implementar UsuariosFacade
- [ ] Implementar MaterialesFacade
- [ ] Implementar ICalculadorMulta

### Día 4
- [ ] Implementar IReglaValidacion
- [ ] Tests para State Pattern
- [ ] Documentación de flujos

---

## 🔗 Referencias

- [Plan de Refactorización](plan-refactorizacion-microservicios.md)
- [Progreso Actual](progreso-refactorizacion-microservicios.md)
- [Análisis Detallado](ANALISIS_MICROSERVICIOS.md)
