package com.biblioteca.cli;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.springframework.stereotype.Service;
import com.biblioteca.cli.client.BibliotecaRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MenuService {

    private final Scanner scanner;
    private boolean ejecutando;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final BibliotecaRestClient client;
    private final ObjectMapper objectMapper;

    public MenuService(BibliotecaRestClient client) {
        this.scanner = new Scanner(System.in);
        this.ejecutando = true;
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }

    public void iniciar() {
        while (ejecutando) {
            mostrarMenuPrincipal();
            String opcion = scanner.nextLine();
            procesarOpcionPrincipal(opcion);
        }

        System.out.println("\n¡Gracias por usar el sistema! Hasta pronto.");
        scanner.close();
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            SISTEMA DE BIBLIOTECA - CLI");
        System.out.println("=".repeat(60));
        System.out.println("1.  Gestión de Materiales");
        System.out.println("2.  Gestión de Usuarios");
        System.out.println("3.  Gestión de Préstamos");
        System.out.println("4.  Gestión de Devoluciones");
        System.out.println("5.  Gestión de Reservas");
        System.out.println("6.  Gestión de Multas");
        System.out.println("7.  Consultas y Reportes");
        System.out.println("0.  Salir");
        System.out.println("=".repeat(60));
        System.out.print("Seleccione una opción: ");
    }

    private void procesarOpcionPrincipal(String opcion) {
        switch (opcion) {
            case "1":
                menuMateriales();
                break;
            case "2":
                menuUsuarios();
                break;
            case "3":
                menuPrestamos();
                break;
            case "4":
                menuDevoluciones();
                break;
            case "5":
                menuReservas();
                break;
            case "6":
                menuMultas();
                break;
            case "7":
                menuConsultas();
                break;
            case "0":
                ejecutando = false;
                break;
            default:
                System.out.println("Opción no válida");
                pausa();
        }
    }

    // ============ MENÚ DE MATERIALES ============

    private void menuMateriales() {
        while (true) {
            try {
                System.out.println("\n            GESTIÓN DE MATERIALES");
                System.out.println("1.  Listar todos los materiales");
                System.out.println("2.  Buscar material por ID");
                System.out.println("3.  Agregar nuevo material");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        listarMateriales();
                        break;
                    case "2":
                        buscarMaterial();
                        break;
                    case "3":
                        agregarMaterial();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                pausa();
            }
        }
    }

    private void listarMateriales() {
        List<?> materiales = client.listarMateriales();
        if (materiales.isEmpty()) {
            System.out.println("\nNo hay materiales registrados");
            return;
        }

        System.out.println("\nLISTADO DE MATERIALES:");
        System.out.println("-".repeat(100));
        System.out.printf("%-10s %-30s %-20s %-12s %-12s %-15s%n",
                "ID", "TÍTULO", "AUTOR", "TIPO", "ESTADO", "PRECIO");
        System.out.println("-".repeat(100));

        for (Object m : materiales) {
            try {
                Map<String, Object> materialMap = objectMapper.convertValue(m, Map.class);
                String id = getString(materialMap, "id", "");
                String titulo = getString(materialMap, "titulo", "");
                String autor = getString(materialMap, "autor", "");
                String tipo = getString(materialMap, "tipo", "");
                String estado = getString(materialMap, "estado", "");
                Object precio = materialMap.get("precio");

                System.out.printf("%-10s %-30s %-20s %-12s %-12s %15s%n",
                        truncate(id, 10),
                        truncate(titulo, 30),
                        truncate(autor, 20),
                        truncate(tipo, 12),
                        truncate(estado, 12),
                        precio != null ? String.format("$%.2f", Double.parseDouble(precio.toString())) : "N/A");
            } catch (Exception e) {
                System.out.println("Error al procesar material: " + e.getMessage());
            }
        }
    }

    private void buscarMaterial() {
        System.out.print("\nIngrese ID del material: ");
        String id = scanner.nextLine();

        Object material = client.obtenerMaterial(id);
        if (material == null) {
            System.out.println("Material no encontrado");
            return;
        }

        System.out.println("\nDETALLE DEL MATERIAL:");
        System.out.println(toJson(material));
    }

    private void agregarMaterial() {
        System.out.println("\nAGREGAR NUEVO MATERIAL (JSON):");
        System.out.println("Ingrese el JSON del material (o 'cancelar' para salir):");
        StringBuilder json = new StringBuilder();
        String linea;

        while (!(linea = scanner.nextLine()).equalsIgnoreCase("cancelar")) {
            json.append(linea);
        }

        if (json.length() == 0)
            return;

        try {
            Object material = objectMapper.readValue(json.toString(), Object.class);
            Object resultado = client.crearMaterial(material);
            System.out.println("Material creado exitosamente");
            System.out.println(toJson(resultado));
        } catch (Exception e) {
            System.out.println("Error al crear material: " + e.getMessage());
        }
    }

    // ============ MENÚ DE USUARIOS ============

    private void menuUsuarios() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE USUARIOS");
                System.out.println("=".repeat(60));
                System.out.println("1.  Listar todos los usuarios");
                System.out.println("2.  Buscar usuario por ID");
                System.out.println("3.  Agregar nuevo usuario");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        listarUsuarios();
                        break;
                    case "2":
                        buscarUsuario();
                        break;
                    case "3":
                        agregarUsuario();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                pausa();
            }
        }
    }

    private void listarUsuarios() {
        List<?> usuarios = client.listarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("\nNo hay usuarios registrados");
            return;
        }

        System.out.println("\nLISTADO DE USUARIOS:");
        System.out.println("-".repeat(110));
        System.out.printf("%-12s %-25s %-30s %-15s %-12s %-15s%n",
                "ID", "NOMBRE", "EMAIL", "TIPO", "ESTADO", "LÍM.PRÉSTAMOS");
        System.out.println("-".repeat(110));

        for (Object u : usuarios) {
            try {
                Map<String, Object> usuarioMap = objectMapper.convertValue(u, Map.class);
                String id = getString(usuarioMap, "id", "");
                String nombre = getString(usuarioMap, "nombre", "");
                String email = getString(usuarioMap, "email", "");
                String tipoUsuario = getString(usuarioMap, "tipoUsuario", "");
                String estadoUsuario = getString(usuarioMap, "estadoUsuario", "");
                Object limiteMaximoPrestamos = usuarioMap.get("limiteMaximoPrestamos");

                System.out.printf("%-12s %-25s %-30s %-15s %-12s %15s%n",
                        truncate(id, 12),
                        truncate(nombre, 25),
                        truncate(email, 30),
                        truncate(tipoUsuario, 15),
                        truncate(estadoUsuario, 12),
                        limiteMaximoPrestamos != null ? limiteMaximoPrestamos.toString() : "N/A");
            } catch (Exception e) {
                System.out.println("Error al procesar usuario: " + e.getMessage());
            }
        }
    }

    private void buscarUsuario() {
        System.out.print("\nIngrese ID del usuario: ");
        String id = scanner.nextLine();

        Object usuario = client.obtenerUsuario(id);
        if (usuario == null) {
            System.out.println("Usuario no encontrado");
            return;
        }

        System.out.println("\nDETALLE DEL USUARIO:");
        System.out.println(toJson(usuario));
    }

    private void agregarUsuario() {
        System.out.println("\nAGREGAR NUEVO USUARIO (JSON):");
        System.out.println("Ingrese el JSON del usuario (o 'cancelar' para salir):");
        StringBuilder json = new StringBuilder();
        String linea;

        while (!(linea = scanner.nextLine()).equalsIgnoreCase("cancelar")) {
            json.append(linea);
        }

        if (json.length() == 0)
            return;

        try {
            Object usuario = objectMapper.readValue(json.toString(), Object.class);
            Object resultado = client.crearUsuario(usuario);
            System.out.println("Usuario creado exitosamente");
            System.out.println(toJson(resultado));
        } catch (Exception e) {
            System.out.println("Error al crear usuario: " + e.getMessage());
        }
    }

    // ============ MENÚ DE PRÉSTAMOS ============

    private void menuPrestamos() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE PRÉSTAMOS");
                System.out.println("=".repeat(60));
                System.out.println("1.  Registrar nuevo préstamo");
                System.out.println("2.  Listar préstamos");
                System.out.println("3.  Buscar préstamo por ID");
                System.out.println("4.  Renovar préstamo");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        registrarPrestamo();
                        break;
                    case "2":
                        listarPrestamos();
                        break;
                    case "3":
                        buscarPrestamo();
                        break;
                    case "4":
                        renovarPrestamo();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                pausa();
            }
        }
    }

    private void registrarPrestamo() {
        System.out.println("\nREGISTRAR NUEVO PRÉSTAMO");
        System.out.print("ID Usuario: ");
        String idUsuario = scanner.nextLine();
        System.out.print("ID Material: ");
        String idMaterial = scanner.nextLine();

        System.out.println("Tipo de préstamo:");
        System.out.println("1. NORMAL");
        System.out.println("2. INTERBIBLIOTECARIO");
        System.out.print("Seleccione: ");
        String tipo = scanner.nextLine();

        String tipoPrestamo = tipo.equals("2") ? "INTERBIBLIOTECARIO" : "NORMAL";

        Object resultado = client.registrarPrestamo(idUsuario, idMaterial, tipoPrestamo);
        if (resultado != null) {
            System.out.println("Préstamo registrado exitosamente");
            System.out.println(toJson(resultado));
        } else {
            System.out.println("Error al registrar préstamo");
        }
    }

    private void listarPrestamos() {
        List<?> prestamos = client.listarPrestamos();
        if (prestamos.isEmpty()) {
            System.out.println("\nNo hay préstamos registrados");
            return;
        }

        System.out.println("\nLISTADO DE PRÉSTAMOS:");
        System.out.println("-".repeat(130));
        System.out.printf("%-36s %-12s %-12s %-15s %-15s %-10s %-10s%n",
                "ID PRÉSTAMO", "USUARIO", "MATERIAL", "ESTADO", "TIPO", "RENOVACIONES", "VENCIMIENTO");
        System.out.println("-".repeat(130));

        for (Object p : prestamos) {
            try {
                Map<String, Object> prestamoMap = objectMapper.convertValue(p, Map.class);
                String id = getString(prestamoMap, "id", "");
                String idUsuario = getString(prestamoMap, "idUsuario", "");
                String idMaterial = getString(prestamoMap, "idMaterial", "");
                String estado = getString(prestamoMap, "estado", "");
                String tipoPrestamo = getString(prestamoMap, "tipoPrestamo", "");
                Object renovacionesUsadas = prestamoMap.get("renovacionesUsadas");
                String fechaDevolucion = getString(prestamoMap, "fechaDevolucionEsperada", "");

                System.out.printf("%-36s %-12s %-12s %-15s %-15s %-10s %-10s%n",
                        truncate(id, 36),
                        truncate(idUsuario, 12),
                        truncate(idMaterial, 12),
                        truncate(estado, 15),
                        truncate(tipoPrestamo, 15),
                        renovacionesUsadas != null ? renovacionesUsadas.toString() : "0",
                        truncate(fechaDevolucion, 10));
            } catch (Exception e) {
                System.out.println("Error al procesar préstamo: " + e.getMessage());
            }
        }
    }

    private void buscarPrestamo() {
        System.out.print("\nIngrese ID del préstamo: ");
        String id = scanner.nextLine();

        Object prestamo = client.obtenerPrestamo(id);
        if (prestamo == null) {
            System.out.println("Préstamo no encontrado");
            return;
        }

        System.out.println("\nDETALLE DEL PRÉSTAMO:");
        System.out.println(toJson(prestamo));
    }

    private void renovarPrestamo() {
        System.out.print("\nIngrese ID del préstamo a renovar: ");
        String idPrestamo = scanner.nextLine();

        Object resultado = client.renovarPrestamo(idPrestamo);
        if (resultado != null) {
            System.out.println("Préstamo renovado exitosamente");
        } else {
            System.out.println("Error al renovar préstamo");
        }
    }

    // ============ MENÚ DE DEVOLUCIONES ============

    private void menuDevoluciones() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE DEVOLUCIONES");
                System.out.println("=".repeat(60));
                System.out.println("1.  Procesar devolución de préstamo");
                System.out.println("2.  Listar préstamos activos de usuario");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        procesarDevolucion();
                        break;
                    case "2":
                        listarPrestamosActivos();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                pausa();
            }
        }
    }

    private void procesarDevolucion() {
        System.out.print("\nIngrese ID del préstamo a devolver: ");
        String idPrestamo = scanner.nextLine();

        Object resultado = client.procesarDevolucion(idPrestamo);
        if (resultado != null) {
            System.out.println("Devolución procesada exitosamente");
            System.out.println(toJson(resultado));
        } else {
            System.out.println("Error al procesar la devolución");
        }
    }

    private void listarPrestamosActivos() {
        System.out.print("\nIngrese ID del usuario: ");
        String idUsuario = scanner.nextLine();
        
        List<?> allPrestamos = client.listarPrestamos();
        List<?> prestamosActivos = allPrestamos.stream()
            .filter(p -> {
                try {
                    Map<String, Object> prestamoMap = objectMapper.convertValue(p, Map.class);
                    String usuario = getString(prestamoMap, "idUsuario", "");
                    String estado = getString(prestamoMap, "estado", "");
                    return usuario.equals(idUsuario) && "ACTIVO".equalsIgnoreCase(estado);
                } catch (Exception e) {
                    return false;
                }
            })
            .collect(java.util.stream.Collectors.toList());

        if (prestamosActivos.isEmpty()) {
            System.out.println("\nEste usuario no tiene préstamos activos");
            return;
        }

        System.out.println("\nPRÉSTAMOS ACTIVOS DEL USUARIO:");
        System.out.println("-".repeat(130));
        System.out.printf("%-36s %-12s %-12s %-15s %-15s %-10s %-10s%n",
                "ID PRÉSTAMO", "USUARIO", "MATERIAL", "ESTADO", "TIPO", "RENOVACIONES", "VENCIMIENTO");
        System.out.println("-".repeat(130));

        for (Object p : prestamosActivos) {
            try {
                Map<String, Object> prestamoMap = objectMapper.convertValue(p, Map.class);
                String id = getString(prestamoMap, "id", "");
                String usuario = getString(prestamoMap, "idUsuario", "");
                String material = getString(prestamoMap, "idMaterial", "");
                String estado = getString(prestamoMap, "estado", "");
                String tipo = getString(prestamoMap, "tipoPrestamo", "");
                Object renovaciones = prestamoMap.get("renovacionesUsadas");
                String fechaDevolucion = getString(prestamoMap, "fechaDevolucionEsperada", "");

                System.out.printf("%-36s %-12s %-12s %-15s %-15s %-10s %-10s%n",
                        truncate(id, 36),
                        truncate(usuario, 12),
                        truncate(material, 12),
                        truncate(estado, 15),
                        truncate(tipo, 15),
                        renovaciones != null ? renovaciones.toString() : "0",
                        truncate(fechaDevolucion, 10));
            } catch (Exception e) {
                System.out.println("Error al procesar préstamo");
            }
        }
    }

    // ============ MENÚ DE RESERVAS ============

    private void menuReservas() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE RESERVAS");
                System.out.println("=".repeat(60));
                System.out.println("1.  Crear reserva");
                System.out.println("2.  Cancelar reserva");
                System.out.println("3.  Listar reservas");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        crearReserva();
                        break;
                    case "2":
                        cancelarReserva();
                        break;
                    case "3":
                        listarReservas();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                pausa();
            }
        }
    }

    private void crearReserva() {
        System.out.println("\nCREAR RESERVA");
        System.out.print("ID Usuario: ");
        String idUsuario = scanner.nextLine();
        System.out.print("ID Material: ");
        String idMaterial = scanner.nextLine();

        Object resultado = client.crearReserva(idUsuario, idMaterial);
        if (resultado != null) {
            System.out.println("Reserva creada exitosamente");
            System.out.println(toJson(resultado));
        } else {
            System.out.println("Error al crear reserva");
        }
    }

    private void cancelarReserva() {
        System.out.print("\nIngrese ID de la reserva a cancelar: ");
        String idReserva = scanner.nextLine();

        Object resultado = client.cancelarReserva(idReserva);
        if (resultado != null) {
            System.out.println("Reserva cancelada exitosamente");
        } else {
            System.out.println("Error al cancelar reserva");
        }
    }

    private void listarReservas() {
        System.out.println("\nFiltrar reservas por:");
        System.out.println("1. Material");
        System.out.println("2. Usuario");
        System.out.print("Seleccione: ");
        String filtro = scanner.nextLine();

        List<?> reservas = Collections.emptyList();
        String tipoFiltro = "";

        if ("1".equals(filtro)) {
            System.out.print("Ingrese ID del material: ");
            String idMaterial = scanner.nextLine();
            reservas = client.listarReservasPorMaterial(idMaterial);
            tipoFiltro = "DEL MATERIAL " + idMaterial;
        } else if ("2".equals(filtro)) {
            System.out.print("Ingrese ID del usuario: ");
            String idUsuario = scanner.nextLine();
            reservas = client.listarReservasPorUsuario(idUsuario);
            tipoFiltro = "DEL USUARIO " + idUsuario;
        } else {
            System.out.println("Opción no válida");
            return;
        }

        if (reservas.isEmpty()) {
            System.out.println("\nNo hay reservas " + tipoFiltro);
            return;
        }

        System.out.println("\nLISTADO DE RESERVAS " + tipoFiltro + ":");
        System.out.println("-".repeat(120));
        System.out.printf("%-36s %-12s %-12s %-15s %-12s %-15s %-12s%n",
                "ID RESERVA", "USUARIO", "MATERIAL", "ESTADO", "POSICIÓN", "FECHA EXPIRA", "SEDE");
        System.out.println("-".repeat(120));

        for (Object r : reservas) {
            try {
                Map<String, Object> reservaMap = objectMapper.convertValue(r, Map.class);
                String id = getString(reservaMap, "id", "");
                String idUsuario = getString(reservaMap, "idUsuario", "");
                String idMaterial = getString(reservaMap, "idMaterial", "");
                String estado = getString(reservaMap, "estado", "");
                Object posicion = reservaMap.get("posicion");
                String fechaExpira = getString(reservaMap, "fechaExpiracion", "");
                String sede = getString(reservaMap, "sede", "");

                System.out.printf("%-36s %-12s %-12s %-15s %-12s %-15s %-12s%n",
                        truncate(id, 36),
                        truncate(idUsuario, 12),
                        truncate(idMaterial, 12),
                        truncate(estado, 15),
                        posicion != null ? posicion.toString() : "N/A",
                        truncate(fechaExpira, 15),
                        truncate(sede, 12));
            } catch (Exception e) {
                System.out.println("Error al procesar reserva: " + e.getMessage());
            }
        }
    }

    // ============ MENÚ DE MULTAS ============

    private void menuMultas() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE MULTAS");
                System.out.println("=".repeat(60));
                System.out.println("1.  Listar multas pendientes");
                System.out.println("2.  Buscar multa por ID");
                System.out.println("3.  Pagar multa");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        listarMultas();
                        break;
                    case "2":
                        buscarMulta();
                        break;
                    case "3":
                        pagarMulta();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                pausa();
            }
        }
    }

    private void listarMultas() {
        System.out.print("\nIngrese ID del usuario: ");
        String idUsuario = scanner.nextLine();
        
        List<?> multas = client.listarMultasPorUsuario(idUsuario);
        if (multas.isEmpty()) {
            System.out.println("\nNo hay multas registradas para este usuario");
            return;
        }

        System.out.println("\nLISTADO DE MULTAS DEL USUARIO:");
        System.out.println("-".repeat(110));
        System.out.printf("%-36s %-12s %-15s %-15s %-12s %-15s%n",
                "ID MULTA", "USUARIO", "MONTO", "MOTIVO", "ESTADO", "FECHA GENERADA");
        System.out.println("-".repeat(110));

        for (Object m : multas) {
            try {
                Map<String, Object> multaMap = objectMapper.convertValue(m, Map.class);
                String id = getString(multaMap, "id", "");
                String usuario = getString(multaMap, "idUsuario", "");
                Object monto = multaMap.get("monto");
                String motivo = getString(multaMap, "motivo", "");
                String estado = getString(multaMap, "estado", "");
                String fecha = getString(multaMap, "fechaGeneracion", "");

                System.out.printf("%-36s %-12s %-15s %-15s %-12s %-15s%n",
                        truncate(id, 36),
                        truncate(usuario, 12),
                        monto != null ? String.format("$%.2f", Double.parseDouble(monto.toString())) : "N/A",
                        truncate(motivo, 15),
                        truncate(estado, 12),
                        truncate(fecha, 15));
            } catch (Exception e) {
                System.out.println("Error al procesar multa: " + e.getMessage());
            }
        }
    }

    private void buscarMulta() {
        System.out.print("\nIngrese ID de la multa: ");
        String id = scanner.nextLine();

        Object multa = client.obtenerMulta(id);
        if (multa == null) {
            System.out.println("Multa no encontrada");
            return;
        }

        System.out.println("\nDETALLE DE LA MULTA:");
        System.out.println(toJson(multa));
    }

    private void pagarMulta() {
        System.out.print("\nIngrese ID de la multa a pagar: ");
        String idMulta = scanner.nextLine();

        Object resultado = client.pagarMulta(idMulta);
        if (resultado != null) {
            System.out.println("Multa pagada exitosamente");
            System.out.println(toJson(resultado));
        } else {
            System.out.println("Error al pagar la multa");
        }
    }

    // ============ MENÚ DE CONSULTAS ============

    private void menuConsultas() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            CONSULTAS Y REPORTES");
                System.out.println("=".repeat(60));
                System.out.println("1.  Préstamos activos por usuario");
                System.out.println("2.  Materiales disponibles");
                System.out.println("3.  Multas pendientes por usuario");
                System.out.println("4.  Estado general del sistema");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        consultarPrestamosActivos();
                        break;
                    case "2":
                        consultarMaterialesDisponibles();
                        break;
                    case "3":
                        consultarMultasPendientes();
                        break;
                    case "4":
                        estadoGeneral();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                pausa();
            }
        }
    }

    private void consultarPrestamosActivos() {
        System.out.print("\nIngrese ID del usuario: ");
        String idUsuario = scanner.nextLine();
        
        List<?> prestamos = client.listarPrestamosActivos(idUsuario);
        System.out.println("\nPRÉSTAMOS ACTIVOS:");
        System.out.printf("Total de préstamos activos: %d%n", prestamos.size());
        
        if (!prestamos.isEmpty()) {
            System.out.println("-".repeat(130));
            System.out.printf("%-36s %-12s %-12s %-15s %-15s %-10s %-10s%n",
                    "ID PRÉSTAMO", "USUARIO", "MATERIAL", "ESTADO", "TIPO", "RENOVACIONES", "VENCIMIENTO");
            System.out.println("-".repeat(130));

            for (Object p : prestamos) {
                try {
                    Map<String, Object> prestamoMap = objectMapper.convertValue(p, Map.class);
                    String id = getString(prestamoMap, "id", "");
                    String usuario = getString(prestamoMap, "idUsuario", "");
                    String material = getString(prestamoMap, "idMaterial", "");
                    String estado = getString(prestamoMap, "estado", "");
                    String tipo = getString(prestamoMap, "tipoPrestamo", "");
                    Object renovaciones = prestamoMap.get("renovacionesUsadas");
                    String fechaDevolucion = getString(prestamoMap, "fechaDevolucionEsperada", "");

                    System.out.printf("%-36s %-12s %-12s %-15s %-15s %-10s %-10s%n",
                            truncate(id, 36),
                            truncate(usuario, 12),
                            truncate(material, 12),
                            truncate(estado, 15),
                            truncate(tipo, 15),
                            renovaciones != null ? renovaciones.toString() : "0",
                            truncate(fechaDevolucion, 10));
                } catch (Exception e) {
                    System.out.println("Error al procesar préstamo");
                }
            }
        }
    }

    private void consultarMaterialesDisponibles() {
        List<?> materiales = client.listarMateriales();
        System.out.println("\nMATERIALES DISPONIBLES:");
        long disponibles = 0;
        
        for (Object m : materiales) {
            try {
                Map<String, Object> materialMap = objectMapper.convertValue(m, Map.class);
                String estado = getString(materialMap, "estado", "");
                if ("DISPONIBLE".equalsIgnoreCase(estado)) {
                    disponibles++;
                }
            } catch (Exception e) {
                // skip
            }
        }
        
        System.out.printf("Total de materiales disponibles: %d de %d%n", disponibles, materiales.size());
    }

    private void consultarMultasPendientes() {
        System.out.print("\nIngrese ID del usuario: ");
        String idUsuario = scanner.nextLine();
        
        List<?> multas = client.listarMultasPorUsuario(idUsuario);
        System.out.println("\nMULTAS DEL USUARIO:");
        
        double totalPendiente = 0;
        for (Object m : multas) {
            try {
                Map<String, Object> multaMap = objectMapper.convertValue(m, Map.class);
                String estado = getString(multaMap, "estado", "");
                if ("PENDIENTE".equalsIgnoreCase(estado)) {
                    Object monto = multaMap.get("monto");
                    if (monto != null) {
                        totalPendiente += Double.parseDouble(monto.toString());
                    }
                }
            } catch (Exception e) {
                // skip
            }
        }
        
        System.out.printf("Total de multas pendientes: %d%n", multas.size());
        System.out.printf("Monto total pendiente: $%.2f%n", totalPendiente);
    }

    private void estadoGeneral() {
        System.out.println("\nESTADO GENERAL DEL SISTEMA:");
        System.out.println("-".repeat(50));
        
        List<?> materiales = client.listarMateriales();
        List<?> usuarios = client.listarUsuarios();
        List<?> prestamos = client.listarPrestamos();
        List<?> multas = client.listarMultas();
        List<?> reservas = client.listarReservas();
        
        System.out.printf("Total de materiales: %d%n", materiales.size());
        System.out.printf("Total de usuarios: %d%n", usuarios.size());
        System.out.printf("Total de préstamos: %d%n", prestamos.size());
        System.out.printf("Total de multas: %d%n", multas.size());
        System.out.printf("Total de reservas: %d%n", reservas.size());
    }

    // ============ UTILIDADES ============

    private void pausa() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return obj != null ? obj.toString() : "null";
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null)
            return "";
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
