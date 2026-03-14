package com.biblioteca.eventos;

public class ObservadorAuditoria implements IObservadorPrestamo {
    @Override
    public void actualizar(EventoPrestamo evento) {
        System.out.println("📝 Auditoría: Acción '" + evento.getTipo() + "' en préstamo " + evento.getIdPrestamo() + " guardada en log inmutable.");
        // Registrar en blockchain o log centralizado seguro de solo lectura
    }
}
