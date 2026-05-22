package com.biblioteca.usuarios.infraestructura.persistencia;

import com.biblioteca.usuarios.dominio.agregados.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "tipo_usuario", nullable = false)
    private String tipoUsuario;

    @Column(name = "estado_usuario", nullable = false)
    private String estadoUsuario;

    @Column(name = "limite_maximo_prestamos", nullable = false)
    private int limiteMaximoPrestamos;

    @Column(name = "motivo_bloqueo")
    private String motivoBloqueo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public UsuarioEntity() {}

    // -------------------------------------------------------------------------
    // Fábrica estática desde dominio
    // -------------------------------------------------------------------------

    public static UsuarioEntity fromDomain(Usuario u) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.id = u.getId();
        entity.nombre = u.getNombre();
        entity.email = u.getEmail();
        entity.tipoUsuario = u.getTipoUsuario();
        entity.estadoUsuario = u.getEstadoUsuario();
        entity.limiteMaximoPrestamos = u.getLimiteMaximoPrestamos();
        entity.fechaCreacion = LocalDateTime.now();
        return entity;
    }

    // -------------------------------------------------------------------------
    // Conversión a dominio
    // -------------------------------------------------------------------------

    public Usuario toDomain() {
        return new Usuario(id, nombre, email, tipoUsuario, estadoUsuario, limiteMaximoPrestamos);
    }

    // -------------------------------------------------------------------------
    // Getters y setters
    // -------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public int getLimiteMaximoPrestamos() {
        return limiteMaximoPrestamos;
    }

    public void setLimiteMaximoPrestamos(int limiteMaximoPrestamos) {
        this.limiteMaximoPrestamos = limiteMaximoPrestamos;
    }

    public String getMotivoBloqueo() {
        return motivoBloqueo;
    }

    public void setMotivoBloqueo(String motivoBloqueo) {
        this.motivoBloqueo = motivoBloqueo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
