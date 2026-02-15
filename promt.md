Quitar PrestamoDigital - CORRECTO
Razón: Simplifica el modelo sin perder funcionalidad esencial.
2. ✅ Quitar descuento - CORRECTO
Razón: Simplifica, evita complejidad innecesaria para el ejercicio académico.
3.daño incumple resposabilidad simple porque calcula el daño en si mismo, deberia haber una calculadora o algo ✅ Dano incumple SRP - TIENES RAZÓN

```
❌ PROBLEMA ACTUAL:
class Dano {
    -decimal costoReparacion  ← CALCULA su propio costo
    +GetCostoReparacion()
}

✅ SOLUCIÓN:
class Dano {
    -string descripcion
    -NivelGravedad gravedad
    -TipoDano tipo
    // SIN costo, solo describe el daño
}

class CalculadorCostoDanoService {
    -Map<TipoDano, Map<NivelGravedad, decimal>> tarifas
    +CalcularCosto(dano : Dano) : decimal
```

4. 🤔 CalculadorMultaService incumple LSP? - ANALICEMOS
plantuml

```plantuml
interface ICalculadorMultaService {
    +CalcularMultaPorRetraso(...) : Multa
    +CalcularMultaPorDano(...) : Multa
    +CalcularMultaPorPerdida(...) : Multa
}

class CalculadorMultaService implements ICalculadorMultaService {
    +CalcularMultaPorRetraso(...) : Multa
    +CalcularMultaPorDano(...) : Multa
    +CalcularMultaPorPerdida(...) : Multa
}
```

**Pregunta clave:** ¿Viola LSP?

**LSP dice:** "Las clases derivadas deben poder sustituirse por sus clases base sin alterar el correcto funcionamiento del programa"

**Análisis:**
- ❌ **NO viola LSP** porque:
  - Solo hay UNA implementación (`CalculadorMultaService`)
  - Implementa completamente la interfaz
  - Devuelve tipos específicos de Multa (polimorfismo correcto)

**PERO... ¿Podría haber un problema de diseño?**

🤔 **SÍ, hay un problema potencial:**
```
ICalculadorMultaService tiene 3 métodos que crean diferentes tipos de Multa.

Si mañana agregamos:
- MultaPorUsoIndebido
- MultaPorNoDevolucion

Tendríamos que MODIFICAR la interfaz ICalculadorMultaService (viola OCP)
```

OPCIÓN B: Strategy Pattern (cada tipo su calculador) interface ICalculadorMulta { +Calcular(datos) : Multa } class CalculadorMultaPorRetraso implements ICalculadorMulta class CalculadorMultaPorDano implements ICalculadorMulta class CalculadorMultaPorPerdida implements ICalculadorMulta

* Sí tiene un problema leve de ISP (interfaz con muchos métodos)
* Sí tiene un problema leve de OCP (agregar nuevo tipo = modificar interfaz)

debo hacer estas mejoras en mi diagrama, hazlo porfavorr:



tambien la parte de multas es algo especifica, deberia ser mas como sancion donde pueden haber una sancin de tipo multa que se paga u otras como sancion que no permite sacar material por unos dias o asi. 

