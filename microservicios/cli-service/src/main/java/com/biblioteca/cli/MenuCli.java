package com.biblioteca.cli;

import com.biblioteca.cli.cliente.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class MenuCli {

    private final MaterialesClient materiales;
    private final UsuariosClient usuarios;
    private final CirculacionClient circulacion;
    private final MultasClient multas;
    private final ReservasClient reservas;
    private final CobrosClient cobros;

    private final Scanner sc = new Scanner(System.in);

    public MenuCli(MaterialesClient materiales, UsuariosClient usuarios,
                   CirculacionClient circulacion, MultasClient multas,
                   ReservasClient reservas, CobrosClient cobros) {
        this.materiales = materiales;
        this.usuarios = usuarios;
        this.circulacion = circulacion;
        this.multas = multas;
        this.reservas = reservas;
        this.cobros = cobros;
    }

    public void iniciar() {
        boolean activo = true;
        while (activo) {
            sep();
            System.out.println("        SISTEMA DE BIBLIOTECA — MICROSERVICIOS");
            sep();
            System.out.println("1. Gestión de Materiales");
            System.out.println("2. Gestión de Usuarios");
            System.out.println("3. Gestión de Préstamos");
            System.out.println("4. Gestión de Devoluciones");
            System.out.println("5. Gestión de Reservas");
            System.out.println("6. Gestión de Multas");
            System.out.println("0. Salir");
            sep();
            System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> menuMateriales();
                case "2" -> menuUsuarios();
                case "3" -> menuPrestamos();
                case "4" -> menuDevoluciones();
                case "5" -> menuReservas();
                case "6" -> menuMultas();
                case "0" -> activo = false;
                default -> System.out.println("Opción no válida");
            }
        }
        System.out.println("\n¡Hasta pronto!");
    }

    // ─── MATERIALES ───────────────────────────────────────────────────────────

    private void menuMateriales() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE MATERIALES"); sep();
            System.out.println("1. Listar todos");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Ver disponibilidad");
            System.out.println("4. Agregar material");
            System.out.println("5. Actualizar estado");
            System.out.println("0. Volver");
            sep(); System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> listarMateriales();
                case "2" -> buscarMaterial();
                case "3" -> verDisponibilidadMaterial();
                case "4" -> agregarMaterial();
                case "5" -> actualizarEstadoMaterial();
                case "0" -> { return; }
                default -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarMateriales() {
        try {
            List<Map<String, Object>> lista = materiales.listar();
            if (lista == null || lista.isEmpty()) { System.out.println("No hay materiales"); return; }
            System.out.printf("\n%-12s %-30s %-20s %-12s %-12s%n", "ID", "TÍTULO", "AUTOR", "TIPO", "ESTADO");
            System.out.println("-".repeat(90));
            for (Map<String, Object> m : lista) {
                System.out.printf("%-12s %-30s %-20s %-12s %-12s%n",
                        trunc(m.get("id"), 12), trunc(m.get("titulo"), 28),
                        trunc(m.get("autor"), 18), m.get("tipo"), m.get("estado"));
            }
        } catch (Exception e) {
            System.out.println("Error al conectar con materiales-service: " + e.getMessage());
        }
    }

    private void buscarMaterial() {
        System.out.print("ID del material: ");
        String id = leer();
        try {
            Map<String, Object> m = materiales.obtenerPorId(id);
            if (m == null) { System.out.println("Material no encontrado"); return; }
            printMaterial(m);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void verDisponibilidadMaterial() {
        System.out.print("ID del material: ");
        String id = leer();
        try {
            Map<String, Object> d = materiales.consultarDisponibilidad(id);
            if (d == null) { System.out.println("Material no encontrado"); return; }
            System.out.println("Disponible: " + d.get("disponible"));
            System.out.println("Estado: " + d.get("estado"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void agregarMaterial() {
        System.out.println("Tipos: LIBRO_NORMAL, BESTSELLER, REFERENCIA, DVD, REVISTA, EBOOK");
        System.out.print("Tipo: "); String tipo = leer().toUpperCase();
        System.out.print("ID: "); String id = leer();
        System.out.print("Título: "); String titulo = leer();
        System.out.print("Autor: "); String autor = leer();
        System.out.print("Precio: "); double precio = parseDouble(leer());
        try {
            Map<String, Object> req = new java.util.HashMap<>();
            req.put("id", id); req.put("titulo", titulo);
            req.put("autor", autor); req.put("tipo", tipo); req.put("precio", precio);
            Map<String, Object> r = materiales.crear(req);
            System.out.println("Material creado: " + r.get("id"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void actualizarEstadoMaterial() {
        System.out.print("ID del material: "); String id = leer();
        System.out.println("Estados: DISPONIBLE, PRESTADO, RESERVADO, EN_REPARACION, PERDIDO");
        System.out.print("Nuevo estado: "); String estado = leer().toUpperCase();
        try {
            materiales.actualizarEstado(id, estado);
            System.out.println("Estado actualizado");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ─── USUARIOS ─────────────────────────────────────────────────────────────

    private void menuUsuarios() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE USUARIOS"); sep();
            System.out.println("1. Listar todos");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Ver estado de usuario");
            System.out.println("4. Registrar usuario");
            System.out.println("0. Volver");
            sep(); System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> listarUsuarios();
                case "2" -> buscarUsuario();
                case "3" -> verEstadoUsuario();
                case "4" -> registrarUsuario();
                case "0" -> { return; }
                default -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarUsuarios() {
        try {
            List<Map<String, Object>> lista = usuarios.listar();
            if (lista == null || lista.isEmpty()) { System.out.println("No hay usuarios"); return; }
            System.out.printf("\n%-12s %-22s %-28s %-15s %-12s%n", "ID", "NOMBRE", "EMAIL", "TIPO", "ESTADO");
            System.out.println("-".repeat(92));
            for (Map<String, Object> u : lista) {
                System.out.printf("%-12s %-22s %-28s %-15s %-12s%n",
                        trunc(u.get("id"), 12), trunc(u.get("nombre"), 20),
                        trunc(u.get("email"), 26), u.get("tipoUsuario"), u.get("estadoUsuario"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarUsuario() {
        System.out.print("ID del usuario: ");
        try {
            Map<String, Object> u = usuarios.obtenerPorId(leer());
            if (u == null) { System.out.println("Usuario no encontrado"); return; }
            printUsuario(u);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void verEstadoUsuario() {
        System.out.print("ID del usuario: "); String id = leer();
        try {
            Map<String, Object> e = usuarios.consultarEstado(id);
            if (e == null) { System.out.println("Usuario no encontrado"); return; }
            System.out.println("Usuario: " + e.get("nombre"));
            System.out.println("Estado:  " + e.get("estadoUsuario"));
            System.out.println("Activo:  " + e.get("activo"));
            System.out.println("Tipo:    " + e.get("tipoUsuario"));
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void registrarUsuario() {
        System.out.println("Tipos: ESTUDIANTE, PROFESOR, INVESTIGADOR, PUBLICO_GENERAL");
        System.out.print("Tipo: "); String tipo = leer().toUpperCase();
        System.out.print("ID: "); String id = leer();
        System.out.print("Nombre: "); String nombre = leer();
        System.out.print("Email: "); String email = leer();
        System.out.print("Límite máximo préstamos: "); int limite = parseInt(leer());
        try {
            Map<String, Object> req = new java.util.HashMap<>();
            req.put("id", id); req.put("nombre", nombre);
            req.put("email", email); req.put("tipoUsuario", tipo);
            req.put("limiteMaximoPrestamos", limite);
            Map<String, Object> r = usuarios.crear(req);
            System.out.println("Usuario registrado: " + r.get("id"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ─── PRÉSTAMOS ────────────────────────────────────────────────────────────

    private void menuPrestamos() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE PRÉSTAMOS"); sep();
            System.out.println("1. Listar todos");
            System.out.println("2. Listar por usuario");
            System.out.println("3. Buscar por ID");
            System.out.println("4. Registrar préstamo");
            System.out.println("5. Renovar préstamo");
            System.out.println("0. Volver");
            sep(); System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> listarPrestamos(null);
                case "2" -> { System.out.print("ID usuario: "); listarPrestamos(leer()); }
                case "3" -> buscarPrestamo();
                case "4" -> registrarPrestamo();
                case "5" -> renovarPrestamo();
                case "0" -> { return; }
                default -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarPrestamos(String usuarioId) {
        try {
            List<Map<String, Object>> lista = circulacion.listar(usuarioId);
            if (lista == null || lista.isEmpty()) { System.out.println("No hay préstamos"); return; }
            System.out.printf("\n%-15s %-12s %-12s %-12s %-20s%n", "ID", "USUARIO", "MATERIAL", "ESTADO", "FECHA DEV.");
            System.out.println("-".repeat(75));
            for (Map<String, Object> p : lista) {
                System.out.printf("%-15s %-12s %-12s %-12s %-20s%n",
                        trunc(p.get("id"), 14), trunc(p.get("idUsuario"), 10),
                        trunc(p.get("idMaterial"), 10), p.get("estado"),
                        p.get("fechaDevolucionEsperada"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPrestamo() {
        System.out.print("ID préstamo: ");
        try {
            Map<String, Object> p = circulacion.obtenerPorId(leer());
            if (p == null) { System.out.println("Préstamo no encontrado"); return; }
            printPrestamo(p);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarPrestamo() {
        System.out.print("ID usuario: "); String idU = leer();
        System.out.print("ID material: "); String idM = leer();
        System.out.println("Tipo (1=NORMAL, 2=INTERBIBLIOTECARIO): "); String t = leer();
        String tipo = t.equals("2") ? "INTERBIBLIOTECARIO" : "NORMAL";
        try {
            Map<String, Object> r = circulacion.registrar(idU, idM, tipo);
            System.out.println("Resultado: " + r.get("mensaje") + " — éxito: " + r.get("exito"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void renovarPrestamo() {
        System.out.print("ID préstamo: ");
        try {
            Map<String, Object> r = circulacion.renovar(leer());
            System.out.println("Resultado: " + r.get("mensaje") + " — éxito: " + r.get("exito"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ─── DEVOLUCIONES ─────────────────────────────────────────────────────────

    private void menuDevoluciones() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE DEVOLUCIONES"); sep();
            System.out.println("1. Registrar devolución");
            System.out.println("0. Volver");
            sep(); System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> registrarDevolucion();
                case "0" -> { return; }
                default -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void registrarDevolucion() {
        System.out.print("ID préstamo: "); String id = leer();
        System.out.print("¿Material en buen estado? (s/n): ");
        boolean usable = leer().equalsIgnoreCase("s");
        try {
            Map<String, Object> r = circulacion.devolver(id, usable);
            System.out.println("Resultado: " + r.get("mensaje") + " — éxito: " + r.get("exito"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ─── RESERVAS ─────────────────────────────────────────────────────────────

    private void menuReservas() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE RESERVAS"); sep();
            System.out.println("1. Listar todas");
            System.out.println("2. Listar por usuario");
            System.out.println("3. Listar por material");
            System.out.println("4. Crear reserva");
            System.out.println("5. Cancelar reserva");
            System.out.println("0. Volver");
            sep(); System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> listarReservas(null, null);
                case "2" -> { System.out.print("ID usuario: "); listarReservas(leer(), null); }
                case "3" -> { System.out.print("ID material: "); listarReservas(null, leer()); }
                case "4" -> crearReserva();
                case "5" -> cancelarReserva();
                case "0" -> { return; }
                default -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarReservas(String usuarioId, String materialId) {
        try {
            List<Map<String, Object>> lista = reservas.listar(usuarioId, materialId);
            if (lista == null || lista.isEmpty()) { System.out.println("No hay reservas"); return; }
            System.out.printf("\n%-15s %-12s %-12s %-15s%n", "ID", "USUARIO", "MATERIAL", "ESTADO");
            System.out.println("-".repeat(60));
            for (Map<String, Object> r : lista) {
                System.out.printf("%-15s %-12s %-12s %-15s%n",
                        trunc(r.get("id"), 14), trunc(r.get("idUsuario"), 10),
                        trunc(r.get("idMaterial"), 10), r.get("estado"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void crearReserva() {
        System.out.print("ID usuario: "); String idU = leer();
        System.out.print("ID material: "); String idM = leer();
        System.out.println("Tipo (1=NORMAL, 2=INTERBIBLIOTECARIA): "); String t = leer();
        String tipo = t.equals("2") ? "INTERBIBLIOTECARIA" : "NORMAL";
        try {
            Map<String, Object> r = reservas.crear(idU, idM, tipo);
            System.out.println("Reserva creada: " + r.get("id") + " — estado: " + r.get("estado"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cancelarReserva() {
        System.out.print("ID reserva: "); String id = leer();
        try {
            reservas.cancelar(id);
            System.out.println("Reserva cancelada");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ─── MULTAS ───────────────────────────────────────────────────────────────

    private void menuMultas() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE MULTAS"); sep();
            System.out.println("1. Ver todas las multas");
            System.out.println("2. Ver multas por usuario");
            System.out.println("3. Consultar deuda de usuario");
            System.out.println("4. Registrar pago de multa");
            System.out.println("0. Volver");
            sep(); System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> listarTodasMultas();
                case "2" -> listarMultasPorUsuario();
                case "3" -> consultarDeuda();
                case "4" -> pagarMulta();
                case "0" -> { return; }
                default -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarTodasMultas() {
        try {
            List<Map<String, Object>> lista = multas.listarTodas();
            printListaMultas(lista);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarMultasPorUsuario() {
        System.out.print("ID usuario: "); String id = leer();
        try {
            List<Map<String, Object>> lista = multas.listarPorUsuario(id);
            printListaMultas(lista);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printListaMultas(List<Map<String, Object>> lista) {
        if (lista == null || lista.isEmpty()) { System.out.println("No hay multas"); return; }
        System.out.printf("\n%-15s %-12s %-10s %-12s %-30s%n", "ID", "USUARIO", "MONTO", "ESTADO", "MOTIVO");
        System.out.println("-".repeat(82));
        for (Map<String, Object> m : lista) {
            System.out.printf("%-15s %-12s %-10s %-12s %-30s%n",
                    trunc(m.get("id"), 14), trunc(m.get("usuarioId"), 10),
                    m.get("monto"), m.get("estado"), trunc(m.get("motivo"), 28));
        }
    }

    private void consultarDeuda() {
        System.out.print("ID usuario: "); String id = leer();
        try {
            Map<String, Object> d = multas.consultarDeuda(id);
            if (d == null) { System.out.println("Usuario no encontrado"); return; }
            System.out.println("Deuda total: " + d.get("deudaTotal"));
            System.out.println("Multas pendientes: " + d.get("multasPendientes"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void pagarMulta() {
        System.out.print("ID multa: "); String idM = leer();
        System.out.print("ID usuario: "); String idU = leer();
        System.out.print("Monto: "); double monto = parseDouble(leer());
        try {
            Map<String, Object> r = cobros.registrarPago(idM, idU, monto);
            System.out.println("Pago registrado: " + r.get("id"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private void printMaterial(Map<String, Object> m) {
        System.out.println("\n--- MATERIAL ---");
        System.out.println("ID:     " + m.get("id"));
        System.out.println("Título: " + m.get("titulo"));
        System.out.println("Autor:  " + m.get("autor"));
        System.out.println("Tipo:   " + m.get("tipo"));
        System.out.println("Estado: " + m.get("estado"));
        System.out.println("Precio: " + m.get("precio"));
    }

    private void printUsuario(Map<String, Object> u) {
        System.out.println("\n--- USUARIO ---");
        System.out.println("ID:     " + u.get("id"));
        System.out.println("Nombre: " + u.get("nombre"));
        System.out.println("Email:  " + u.get("email"));
        System.out.println("Tipo:   " + u.get("tipoUsuario"));
        System.out.println("Estado: " + u.get("estadoUsuario"));
        System.out.println("Límite: " + u.get("limiteMaximoPrestamos"));
    }

    private void printPrestamo(Map<String, Object> p) {
        System.out.println("\n--- PRÉSTAMO ---");
        System.out.println("ID:            " + p.get("id"));
        System.out.println("Usuario:       " + p.get("idUsuario"));
        System.out.println("Material:      " + p.get("idMaterial"));
        System.out.println("Estado:        " + p.get("estado"));
        System.out.println("Fecha préstamo:" + p.get("fechaPrestamo"));
        System.out.println("Devol. esperada:" + p.get("fechaDevolucionEsperada"));
    }

    private void sep() { System.out.println("=".repeat(55)); }

    private void pausa() {
        System.out.print("\nEnter para continuar...");
        sc.nextLine();
    }

    private String leer() { return sc.nextLine().trim(); }

    private String trunc(Object val, int max) {
        if (val == null) return "";
        String s = val.toString();
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 5; }
    }
}
