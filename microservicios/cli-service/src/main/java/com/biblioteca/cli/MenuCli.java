package com.biblioteca.cli;

import com.biblioteca.cli.cliente.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper mapper = new ObjectMapper();

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
            System.out.println("7. Reportes y Estadísticas");
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
                case "7" -> menuReportes();
                case "0" -> activo = false;
                default  -> System.out.println("Opción no válida");
            }
        }
        System.out.println("\n¡Hasta pronto!");
    }

    // ─── MATERIALES ───────────────────────────────────────────────

    private void menuMateriales() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE MATERIALES"); sep();
            System.out.println("1. Listar todos");
            System.out.println("2. Buscar (ID, título, autor o tipo)");
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
                default  -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarMateriales() {
        try {
            List<Map<String, Object>> lista = materiales.listar();
            printTablaMateriales(lista);
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void buscarMaterial() {
        System.out.print("Buscar (ID, título, autor o tipo): ");
        String termino = leer().toLowerCase();
        if (termino.isEmpty()) { System.out.println("Ingresa un término de búsqueda"); return; }
        try {
            List<Map<String, Object>> lista = materiales.listar();
            if (lista == null) { System.out.println("Sin resultados"); return; }
            List<Map<String, Object>> filtrados = lista.stream()
                .filter(m -> coincide(m.get("id"), termino) || coincide(m.get("titulo"), termino)
                          || coincide(m.get("autor"), termino) || coincide(m.get("tipo"), termino))
                .toList();
            if (filtrados.isEmpty()) System.out.println("No se encontraron materiales para: " + termino);
            else if (filtrados.size() == 1) printMaterial(filtrados.get(0));
            else printTablaMateriales(filtrados);
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void verDisponibilidadMaterial() {
        System.out.print("ID del material: ");
        String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        try {
            Map<String, Object> d = materiales.consultarDisponibilidad(id);
            if (d == null) { System.out.println("Material no encontrado"); return; }
            System.out.println("Título: " + d.get("titulo"));
            System.out.println("Estado: " + d.get("estado"));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void agregarMaterial() {
        System.out.println("Tipos: LIBRO_NORMAL, BESTSELLER, REFERENCIA, DVD, REVISTA, EBOOK");
        System.out.print("Tipo: "); String tipo = leer().toUpperCase();
        if (tipo.isEmpty()) { System.out.println("El tipo es obligatorio"); return; }

        System.out.print("ID (Enter para auto-generar): "); String id = leer();
        if (id.isEmpty()) id = siguienteIdMaterial();

        System.out.print("Título: "); String titulo = leer();
        if (titulo.isEmpty()) { System.out.println("El título es obligatorio"); return; }

        System.out.print("Autor:  "); String autor = leer();
        if (autor.isEmpty()) { System.out.println("El autor es obligatorio"); return; }

        System.out.print("Precio: "); String precioStr = leer();
        if (precioStr.isEmpty()) { System.out.println("El precio es obligatorio"); return; }
        double precio;
        try { precio = Double.parseDouble(precioStr); }
        catch (NumberFormatException e) { System.out.println("Precio inválido — ingresa un número"); return; }
        if (precio < 0) { System.out.println("El precio no puede ser negativo"); return; }

        try {
            Map<String, Object> req = new java.util.HashMap<>();
            req.put("id", id); req.put("titulo", titulo);
            req.put("autor", autor); req.put("tipo", tipo); req.put("precio", precio);
            Map<String, Object> r = materiales.crear(req);
            System.out.println("✔ Material creado: " + r.get("id") + " — " + r.get("titulo"));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void actualizarEstadoMaterial() {
        System.out.print("ID del material: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        System.out.println("Estados: DISPONIBLE, PRESTADO, RESERVADO, EN_REPARACION, PERDIDO");
        System.out.print("Nuevo estado: "); String estado = leer().toUpperCase();
        if (estado.isEmpty()) { System.out.println("Estado requerido"); return; }
        try {
            materiales.actualizarEstado(id, estado);
            System.out.println("✔ Estado actualizado a " + estado);
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private String siguienteIdMaterial() {
        try {
            List<Map<String, Object>> lista = materiales.listar();
            if (lista == null || lista.isEmpty()) return "MAT-000001";
            int max = lista.stream().mapToInt(m -> {
                try { return Integer.parseInt(m.get("id").toString().replaceAll("\\D", "")); }
                catch (Exception e) { return 0; }
            }).max().orElse(0);
            return String.format("MAT-%06d", max + 1);
        } catch (Exception e) { return "MAT-000001"; }
    }

    // ─── USUARIOS ─────────────────────────────────────────────────

    private void menuUsuarios() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE USUARIOS"); sep();
            System.out.println("1. Listar todos");
            System.out.println("2. Buscar (ID, nombre, email o tipo)");
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
                default  -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarUsuarios() {
        try {
            List<Map<String, Object>> lista = usuarios.listar();
            printTablaUsuarios(lista);
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void buscarUsuario() {
        System.out.print("Buscar (ID, nombre, email o tipo): ");
        String termino = leer().toLowerCase();
        if (termino.isEmpty()) { System.out.println("Ingresa un término de búsqueda"); return; }
        try {
            List<Map<String, Object>> lista = usuarios.listar();
            if (lista == null) { System.out.println("Sin resultados"); return; }
            List<Map<String, Object>> filtrados = lista.stream()
                .filter(u -> coincide(u.get("id"), termino) || coincide(u.get("nombre"), termino)
                          || coincide(u.get("email"), termino) || coincide(u.get("tipoUsuario"), termino))
                .toList();
            if (filtrados.isEmpty()) System.out.println("No se encontraron usuarios para: " + termino);
            else if (filtrados.size() == 1) printUsuario(filtrados.get(0));
            else printTablaUsuarios(filtrados);
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void verEstadoUsuario() {
        System.out.print("ID del usuario: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        try {
            Map<String, Object> e = usuarios.consultarEstado(id);
            if (e == null) { System.out.println("Usuario no encontrado"); return; }
            System.out.println("Usuario: " + e.get("nombre"));
            System.out.println("Estado:  " + e.get("estadoUsuario"));
            System.out.println("Tipo:    " + e.get("tipoUsuario"));
        } catch (Exception ex) {
            System.out.println("Error: " + extraerMensaje(ex));
        }
    }

    private void registrarUsuario() {
        System.out.println("Tipos: ESTUDIANTE, PROFESOR, INVESTIGADOR, PUBLICO_GENERAL");
        System.out.print("Tipo: "); String tipo = leer().toUpperCase();
        if (tipo.isEmpty()) { System.out.println("El tipo es obligatorio"); return; }

        System.out.print("ID (Enter para auto-generar): "); String id = leer();
        if (id.isEmpty()) id = siguienteIdUsuario();

        System.out.print("Nombre: "); String nombre = leer();
        if (nombre.isEmpty()) { System.out.println("El nombre es obligatorio"); return; }

        System.out.print("Email:  "); String email = leer();
        if (email.isEmpty()) { System.out.println("El email es obligatorio"); return; }
        if (!email.matches("^[A-Za-z0-9+_.-]+@.+\\..+$")) {
            System.out.println("Email inválido — formato requerido: usuario@dominio.ext"); return;
        }

        try {
            Map<String, Object> req = new java.util.HashMap<>();
            req.put("id", id); req.put("nombre", nombre);
            req.put("email", email); req.put("tipoUsuario", tipo);
            req.put("limiteMaximoPrestamos", 1); // el servicio calcula el real según tipo
            Map<String, Object> r = usuarios.crear(req);
            System.out.println("✔ Usuario registrado: " + r.get("id") + " — " + r.get("nombre")
                + " (límite: " + r.get("limiteMaximoPrestamos") + " préstamos)");
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private String siguienteIdUsuario() {
        try {
            List<Map<String, Object>> lista = usuarios.listar();
            if (lista == null || lista.isEmpty()) return "USR-000001";
            int max = lista.stream().mapToInt(u -> {
                try { return Integer.parseInt(u.get("id").toString().replaceAll("\\D", "")); }
                catch (Exception e) { return 0; }
            }).max().orElse(0);
            return String.format("USR-%06d", max + 1);
        } catch (Exception e) { return "USR-000001"; }
    }

    // ─── PRÉSTAMOS ────────────────────────────────────────────────

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
                default  -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarPrestamos(String usuarioId) {
        try {
            List<Map<String, Object>> lista = circulacion.listar(usuarioId);
            if (lista == null || lista.isEmpty()) { System.out.println("No hay préstamos"); return; }
            System.out.printf("\n%-12s %-12s %-12s %-10s %-12s%n", "ID", "USUARIO", "MATERIAL", "ESTADO", "FECHA LÍM.");
            System.out.println("-".repeat(62));
            for (Map<String, Object> p : lista) {
                System.out.printf("%-12s %-12s %-12s %-10s %-12s%n",
                    trunc(p.get("id"), 11), trunc(p.get("idUsuario"), 11),
                    trunc(p.get("idMaterial"), 11), p.get("estado"),
                    formatFecha(p.get("fechaDevolucionEsperada")));
            }
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void buscarPrestamo() {
        System.out.print("ID préstamo: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        try {
            Map<String, Object> p = circulacion.obtenerPorId(id);
            if (p == null) { System.out.println("Préstamo no encontrado"); return; }
            printPrestamo(p);
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void registrarPrestamo() {
        System.out.print("ID usuario:  "); String idU = leer();
        System.out.print("ID material: "); String idM = leer();
        if (idU.isEmpty() || idM.isEmpty()) { System.out.println("ID usuario e ID material son requeridos"); return; }

        System.out.println("Tipo: 1=NORMAL  2=INTERBIBLIOTECARIO");
        System.out.print("Seleccione: "); String t = leer();
        String tipo = t.equals("2") ? "INTERBIBLIOTECARIO" : "NORMAL";

        String sede;
        String bibliotecaOrigen = null;
        if (tipo.equals("INTERBIBLIOTECARIO")) {
            System.out.print("Nombre de la biblioteca solicitante: "); bibliotecaOrigen = leer();
            if (bibliotecaOrigen.isEmpty()) bibliotecaOrigen = "Biblioteca Externa";
            System.out.print("Sede de recogida (Enter = 'Sede Central'): "); sede = leer();
            if (sede.isEmpty()) sede = "Sede Central";
        } else {
            System.out.print("Sede (Enter = 'Sede Central'): "); sede = leer();
            if (sede.isEmpty()) sede = "Sede Central";
        }

        try {
            Map<String, Object> r = circulacion.registrar(idU, idM, tipo, sede);
            boolean exito = Boolean.TRUE.equals(r.get("exito"));
            System.out.println((exito ? "✔ " : "✘ ") + r.get("mensaje"));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void renovarPrestamo() {
        System.out.print("ID préstamo: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        try {
            Map<String, Object> r = circulacion.renovar(id);
            boolean exito = Boolean.TRUE.equals(r.get("exito"));
            System.out.println((exito ? "✔ " : "✘ ") + r.get("mensaje"));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    // ─── DEVOLUCIONES ─────────────────────────────────────────────

    private void menuDevoluciones() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE DEVOLUCIONES"); sep();
            System.out.println("1. Registrar devolución");
            System.out.println("0. Volver");
            sep(); System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> registrarDevolucion();
                case "0" -> { return; }
                default  -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void registrarDevolucion() {
        System.out.print("ID préstamo: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        System.out.print("¿Material en buen estado? (s/n): ");
        boolean usable = leer().equalsIgnoreCase("s");

        if (!usable) {
            System.out.println("\nINSPECCIÓN DE DAÑOS:");
            System.out.println("  Gravedad:  1=LEVE  2=MODERADO  3=GRAVE  4=IRREPARABLE");
            System.out.print("  Gravedad: "); String g = leer();
            System.out.println("  Tipo:  1=PAGINAS_RASGADAS  2=MANCHAS  3=CUBIERTA_DANADA  4=RAYONES  5=NO_FUNCIONAL");
            System.out.print("  Tipo de daño: "); String t = leer();
            System.out.print("  Descripción del daño: "); String desc = leer();
            boolean irreparable = g.equals("4") || t.equals("5");
            if (irreparable) {
                System.out.println("  ⚠ Daño IRREPARABLE — se generará multa por pérdida/daño grave");
                usable = false;
            }
        }

        try {
            Map<String, Object> r = circulacion.devolver(id, usable);
            boolean exito = Boolean.TRUE.equals(r.get("exito"));
            System.out.println((exito ? "✔ " : "✘ ") + r.get("mensaje"));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    // ─── RESERVAS ─────────────────────────────────────────────────

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
                case "2" -> { System.out.print("ID usuario: ");   listarReservas(leer(), null); }
                case "3" -> { System.out.print("ID material: ");  listarReservas(null, leer()); }
                case "4" -> crearReserva();
                case "5" -> cancelarReserva();
                case "0" -> { return; }
                default  -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarReservas(String usuarioId, String materialId) {
        try {
            List<Map<String, Object>> lista = reservas.listar(usuarioId, materialId);
            if (lista == null || lista.isEmpty()) { System.out.println("No hay reservas"); return; }
            System.out.printf("\n%-12s %-12s %-12s %-15s %-4s%n", "ID", "USUARIO", "MATERIAL", "ESTADO", "POS");
            System.out.println("-".repeat(58));
            for (Map<String, Object> r : lista) {
                System.out.printf("%-12s %-12s %-12s %-15s %-4s%n",
                    trunc(r.get("id"), 11), trunc(r.get("idUsuario"), 11),
                    trunc(r.get("idMaterial"), 11), r.get("estadoReserva"), r.get("posicionCola"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void crearReserva() {
        System.out.print("ID usuario:  "); String idU = leer();
        System.out.print("ID material: "); String idM = leer();
        if (idU.isEmpty() || idM.isEmpty()) { System.out.println("ID usuario e ID material son requeridos"); return; }
        System.out.println("Tipo: 1=NORMAL  2=INTERBIBLIOTECARIA");
        System.out.print("Seleccione: "); String t = leer();
        String tipo = t.equals("2") ? "INTERBIBLIOTECARIA" : "NORMAL";
        System.out.print("Sede (Enter = 'Sede Central'): "); String sede = leer();
        if (sede.isEmpty()) sede = "Sede Central";
        try {
            Map<String, Object> r = reservas.crear(idU, idM, tipo, sede);
            System.out.println("✔ Reserva creada: " + r.get("id")
                + " — posición " + r.get("posicionCola") + " en cola");
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void cancelarReserva() {
        System.out.print("ID reserva: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        try {
            reservas.cancelar(id);
            System.out.println("✔ Reserva cancelada");
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    // ─── MULTAS ───────────────────────────────────────────────────

    private void menuMultas() {
        while (true) {
            sep(); System.out.println("        GESTIÓN DE MULTAS"); sep();
            System.out.println("1. Ver todas las multas");
            System.out.println("2. Ver multas por usuario");
            System.out.println("3. Ver detalle de multa");
            System.out.println("4. Consultar deuda de usuario");
            System.out.println("5. Registrar pago de multa");
            System.out.println("6. Ver historial de pagos");
            System.out.println("0. Volver");
            sep(); System.out.print("Seleccione: ");
            switch (leer()) {
                case "1" -> listarTodasMultas();
                case "2" -> listarMultasPorUsuario();
                case "3" -> verDetalleMulta();
                case "4" -> consultarDeuda();
                case "5" -> pagarMulta();
                case "6" -> verHistorialPagos();
                case "0" -> { return; }
                default  -> System.out.println("Opción no válida");
            }
            pausa();
        }
    }

    private void listarTodasMultas() {
        try {
            printListaMultas(multas.listarTodas());
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void listarMultasPorUsuario() {
        System.out.print("ID usuario: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        try {
            printListaMultas(multas.listarPorUsuario(id));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void verDetalleMulta() {
        System.out.print("ID multa: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        try {
            Map<String, Object> m = multas.obtenerPorId(id);
            if (m == null) { System.out.println("Multa no encontrada"); return; }
            System.out.println("\n--- DETALLE MULTA ---");
            System.out.println("ID:           " + m.get("id"));
            System.out.println("Usuario:      " + m.get("idUsuario"));
            System.out.println("Préstamo:     " + m.get("idPrestamo"));
            System.out.println("Tipo:         " + m.get("tipoMulta"));
            System.out.printf( "Monto total:  $%.2f%n", parseDouble(m.get("montoTotal")));
            System.out.println("Estado:       " + m.get("estado"));
            System.out.println("Generada:     " + formatFecha(m.get("fechaGeneracion")));
            if (m.get("fechaPago") != null)
                System.out.println("Pagada:       " + formatFecha(m.get("fechaPago")));
            System.out.println("Motivo:");
            System.out.println("  " + m.get("motivo"));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void consultarDeuda() {
        System.out.print("ID usuario: "); String id = leer();
        if (id.isEmpty()) { System.out.println("ID requerido"); return; }
        try {
            Map<String, Object> d = multas.consultarDeuda(id);
            if (d == null) { System.out.println("Usuario no encontrado"); return; }
            Object deuda = d.get("deudaTotal");
            System.out.printf("Deuda total:      $%.2f%n", deuda != null ? parseDouble(deuda) : 0.0);
            System.out.println("Multas pendientes: " + d.get("multasPendientes"));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void pagarMulta() {
        System.out.print("ID multa: "); String idM = leer();
        if (idM.isEmpty()) { System.out.println("ID multa requerido"); return; }

        // Obtener multa para mostrar info y extraer usuarioId automáticamente
        Map<String, Object> multa = null;
        try {
            multa = multas.obtenerPorId(idM);
        } catch (Exception e) { /* continuar sin datos de la multa */ }

        String idU = "";
        if (multa != null) {
            idU = String.valueOf(multa.get("idUsuario"));
            System.out.println("Usuario:      " + idU);
            System.out.printf( "Monto total:  $%.2f%n", parseDouble(multa.get("montoTotal")));
            System.out.println("Estado:       " + multa.get("estado"));
        } else {
            System.out.print("ID usuario (no se encontró la multa): "); idU = leer();
            if (idU.isEmpty()) { System.out.println("ID usuario requerido"); return; }
        }

        System.out.print("Monto a pagar: $");
        String montoStr = leer();
        if (montoStr.isEmpty()) { System.out.println("Monto requerido"); return; }
        double monto;
        try { monto = Double.parseDouble(montoStr); }
        catch (NumberFormatException e) { System.out.println("Monto inválido"); return; }
        if (monto <= 0) { System.out.println("El monto debe ser mayor a cero"); return; }

        if (multa != null) {
            double total = parseDouble(multa.get("montoTotal"));
            if (monto < total) {
                System.out.printf("⚠ Pago parcial: quedarán $%.2f pendientes — la multa seguirá activa%n", total - monto);
            }
        }

        try {
            Map<String, Object> r = cobros.registrarPago(idM, idU, monto);
            System.out.println("✔ Pago registrado ID: " + r.get("id"));
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    private void verHistorialPagos() {
        System.out.print("ID usuario (Enter para ver todos): "); String idU = leer();
        try {
            List<Map<String, Object>> lista = idU.isEmpty()
                ? cobros.listarTodos()
                : cobros.listarPorUsuario(idU);
            if (lista == null || lista.isEmpty()) { System.out.println("No hay pagos registrados"); return; }
            System.out.printf("\n%-12s %-12s %-12s %-10s %-12s%n", "ID PAGO", "MULTA", "USUARIO", "MONTO", "FECHA");
            System.out.println("-".repeat(62));
            for (Map<String, Object> p : lista) {
                System.out.printf("%-12s %-12s %-12s %-10s %-12s%n",
                    trunc(p.get("id"), 11), trunc(p.get("multaId"), 11),
                    trunc(p.get("usuarioId"), 11), "$" + p.get("monto"),
                    formatFecha(p.get("fechaPago")));
            }
        } catch (Exception e) {
            System.out.println("Error: " + extraerMensaje(e));
        }
    }

    // ─── REPORTES ─────────────────────────────────────────────────

    private void menuReportes() {
        sep(); System.out.println("        REPORTES Y ESTADÍSTICAS"); sep();
        try {
            List<Map<String, Object>> prests = circulacion.listar(null);
            List<Map<String, Object>> mats   = materiales.listar();
            List<Map<String, Object>> usrs   = usuarios.listar();
            List<Map<String, Object>> multz  = multas.listarTodas();
            List<Map<String, Object>> resv   = reservas.listar(null, null);

            long prestActivos   = count(prests, "estado", "ACTIVO");
            long matDisponibles = count(mats, "estado", "DISPONIBLE");
            long usrActivos     = count(usrs, "estadoUsuario", "ACTIVO");
            long usrBloqueados  = count(usrs, "estadoUsuario", "BLOQUEADO");
            long multasPend     = count(multz, "estado", "GENERADA");
            double deudaTotal   = multz == null ? 0 : multz.stream()
                .filter(m -> "GENERADA".equals(m.get("estado")))
                .mapToDouble(m -> parseDouble(m.get("montoTotal"))).sum();

            System.out.printf("%-30s %s%n", "Materiales totales:",     mats == null ? 0 : mats.size());
            System.out.printf("%-30s %s%n", "Materiales disponibles:", matDisponibles);
            System.out.printf("%-30s %s%n", "Usuarios activos:",       usrActivos);
            System.out.printf("%-30s %s%n", "Usuarios bloqueados:",    usrBloqueados);
            System.out.printf("%-30s %s%n", "Préstamos activos:",      prestActivos);
            System.out.printf("%-30s %s%n", "Reservas activas:",       resv == null ? 0 : resv.size());
            System.out.printf("%-30s %s%n", "Multas pendientes:",      multasPend);
            System.out.printf("%-30s $%.2f%n", "Deuda total pendiente:", deudaTotal);
        } catch (Exception e) {
            System.out.println("Error al obtener estadísticas: " + extraerMensaje(e));
        }
        pausa();
    }

    private long count(List<Map<String, Object>> lista, String campo, String valor) {
        if (lista == null) return 0;
        return lista.stream().filter(m -> valor.equals(m.get(campo))).count();
    }

    // ─── HELPERS ──────────────────────────────────────────────────

    private void printTablaMateriales(List<Map<String, Object>> lista) {
        if (lista == null || lista.isEmpty()) { System.out.println("No hay materiales"); return; }
        System.out.printf("\n%-12s %-28s %-18s %-14s %-10s%n", "ID", "TÍTULO", "AUTOR", "TIPO", "ESTADO");
        System.out.println("-".repeat(86));
        for (Map<String, Object> m : lista) {
            System.out.printf("%-12s %-28s %-18s %-14s %-10s%n",
                trunc(m.get("id"), 12), trunc(m.get("titulo"), 26),
                trunc(m.get("autor"), 16), m.get("tipo"), m.get("estado"));
        }
    }

    private void printTablaUsuarios(List<Map<String, Object>> lista) {
        if (lista == null || lista.isEmpty()) { System.out.println("No hay usuarios"); return; }
        System.out.printf("\n%-12s %-20s %-26s %-16s %-10s%n", "ID", "NOMBRE", "EMAIL", "TIPO", "ESTADO");
        System.out.println("-".repeat(88));
        for (Map<String, Object> u : lista) {
            System.out.printf("%-12s %-20s %-26s %-16s %-10s%n",
                trunc(u.get("id"), 12), trunc(u.get("nombre"), 18),
                trunc(u.get("email"), 24), u.get("tipoUsuario"), u.get("estadoUsuario"));
        }
    }

    private void printListaMultas(List<Map<String, Object>> lista) {
        if (lista == null || lista.isEmpty()) { System.out.println("No hay multas"); return; }
        System.out.printf("\n%-12s %-12s %-10s %-10s %-26s%n", "ID", "USUARIO", "MONTO", "ESTADO", "MOTIVO");
        System.out.println("-".repeat(74));
        for (Map<String, Object> m : lista) {
            System.out.printf("%-12s %-12s $%-9s %-10s %-26s%n",
                trunc(m.get("id"), 11), trunc(m.get("idUsuario"), 11),
                m.get("montoTotal"), m.get("estado"), trunc(m.get("motivo"), 24));
        }
    }

    private void printMaterial(Map<String, Object> m) {
        System.out.println("\n--- MATERIAL ---");
        System.out.println("ID:     " + m.get("id"));
        System.out.println("Título: " + m.get("titulo"));
        System.out.println("Autor:  " + m.get("autor"));
        System.out.println("Tipo:   " + m.get("tipo"));
        System.out.println("Estado: " + m.get("estado"));
        System.out.printf( "Precio: $%.2f%n", parseDouble(m.get("precio")));
    }

    private void printUsuario(Map<String, Object> u) {
        System.out.println("\n--- USUARIO ---");
        System.out.println("ID:     " + u.get("id"));
        System.out.println("Nombre: " + u.get("nombre"));
        System.out.println("Email:  " + u.get("email"));
        System.out.println("Tipo:   " + u.get("tipoUsuario"));
        System.out.println("Estado: " + u.get("estadoUsuario"));
        System.out.println("Límite: " + u.get("limiteMaximoPrestamos") + " préstamos");
    }

    private void printPrestamo(Map<String, Object> p) {
        System.out.println("\n--- PRÉSTAMO ---");
        System.out.println("ID:             " + p.get("id"));
        System.out.println("Usuario:        " + p.get("idUsuario"));
        System.out.println("Material:       " + p.get("idMaterial"));
        System.out.println("Tipo:           " + p.get("tipoPrestamo"));
        System.out.println("Estado:         " + p.get("estado"));
        System.out.println("Fecha préstamo: " + formatFecha(p.get("fechaPrestamo")));
        System.out.println("Fecha límite:   " + formatFecha(p.get("fechaDevolucionEsperada")));
        System.out.println("Sede:           " + p.get("sede"));
    }

    /** Extrae solo el texto del mensaje de error de una excepción HTTP. */
    private String extraerMensaje(Exception e) {
        String raw = e.getMessage();
        if (raw == null) return "Error desconocido";
        try {
            int idx = raw.indexOf('{');
            if (idx >= 0) {
                String json = raw.substring(idx).replaceAll("^\"|\"$", "").replace("\\\"", "\"");
                @SuppressWarnings("unchecked")
                Map<String, Object> parsed = mapper.readValue(json, Map.class);
                if (parsed.containsKey("error"))   return parsed.get("error").toString();
                if (parsed.containsKey("mensaje"))  return parsed.get("mensaje").toString();
                if (parsed.containsKey("message"))  return parsed.get("message").toString();
            }
        } catch (Exception ignored) {}
        String[] partes = raw.split(":");
        return partes[partes.length - 1].trim().replaceAll("[\"{}]", "");
    }

    private String formatFecha(Object val) {
        if (val == null) return "—";
        String s = val.toString();
        if (s.contains("T")) s = s.substring(0, s.indexOf('T'));
        try {
            String[] p = s.split("-");
            if (p.length == 3) return p[2] + "/" + p[1] + "/" + p[0];
        } catch (Exception ignored) {}
        return s;
    }

    private boolean coincide(Object val, String termino) {
        return val != null && val.toString().toLowerCase().contains(termino);
    }

    private void sep() { System.out.println("=".repeat(55)); }

    private void pausa() { System.out.print("\nEnter para continuar..."); sc.nextLine(); }

    private String leer() { return sc.nextLine().trim(); }

    private String trunc(Object val, int max) {
        if (val == null) return "";
        String s = val.toString();
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }

    private double parseDouble(Object val) {
        try { return Double.parseDouble(String.valueOf(val)); }
        catch (Exception e) { return 0.0; }
    }
}
