package com.biblioteca.circulacion.infraestructura.persistencia;

import com.biblioteca.circulacion.dominio.Prestamo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "prestamos")
public class PrestamoEntity {

    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario;

    @Column(name = "id_material", nullable = false)
    private String idMaterial;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDateTime fechaPrestamo;

    @Column(name = "fecha_devolucion_esperada", nullable = false)
    private LocalDateTime fechaDevolucionEsperada;

    @Column(name = "fecha_devolucion_real")
    private LocalDateTime fechaDevolucionReal;

    @Column(name = "renovaciones_usadas", nullable = false)
    private int renovacionesUsadas;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "tipo_prestamo", nullable = false, length = 30)
    private String tipoPrestamo;

    @Column(length = 100)
    private String sede;

    @Column(name = "biblioteca_origen", length = 200)
    private String bibliotecaOrigen;

    // --- Static factory methods ---

    public static PrestamoEntity fromDomain(Prestamo p) {
        PrestamoEntity e = new PrestamoEntity();
        e.id = p.getId();
        e.idUsuario = p.getIdUsuario();
        e.idMaterial = p.getIdMaterial();
        e.fechaPrestamo = p.getFechaPrestamo();
        e.fechaDevolucionEsperada = p.getFechaDevolucionEsperada();
        e.fechaDevolucionReal = p.getFechaDevolucionReal();
        e.renovacionesUsadas = p.getRenovacionesUsadas();
        e.estado = p.getEstado();
        e.tipoPrestamo = p.getTipoPrestamo();
        e.sede = p.getSede();
        return e;
    }

    public Prestamo toDomain() {
        return Prestamo.reconstruir(id, idUsuario, idMaterial,
                fechaPrestamo, fechaDevolucionEsperada, fechaDevolucionReal,
                renovacionesUsadas, estado, tipoPrestamo, sede);
    }

    // --- Getters ---

    public String getId() { return id; }
    public String getIdUsuario() { return idUsuario; }
    public String getIdMaterial() { return idMaterial; }
    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public LocalDateTime getFechaDevolucionReal() { return fechaDevolucionReal; }
    public int getRenovacionesUsadas() { return renovacionesUsadas; }
    public String getEstado() { return estado; }
    public String getTipoPrestamo() { return tipoPrestamo; }
    public String getSede() { return sede; }
    public String getBibliotecaOrigen() { return bibliotecaOrigen; }

    // --- Setters ---

    public void setId(String id) { this.id = id; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }
    public void setFechaPrestamo(LocalDateTime fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public void setFechaDevolucionEsperada(LocalDateTime fechaDevolucionEsperada) { this.fechaDevolucionEsperada = fechaDevolucionEsperada; }
    public void setFechaDevolucionReal(LocalDateTime fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }
    public void setRenovacionesUsadas(int renovacionesUsadas) { this.renovacionesUsadas = renovacionesUsadas; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setTipoPrestamo(String tipoPrestamo) { this.tipoPrestamo = tipoPrestamo; }
    public void setSede(String sede) { this.sede = sede; }
    public void setBibliotecaOrigen(String bibliotecaOrigen) { this.bibliotecaOrigen = bibliotecaOrigen; }
}
