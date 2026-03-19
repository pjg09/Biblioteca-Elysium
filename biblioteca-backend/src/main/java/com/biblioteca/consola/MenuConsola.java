package com.biblioteca.consola;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

import com.biblioteca.dominio.entidades.DVD;
import com.biblioteca.dominio.entidades.EBook;
import com.biblioteca.dominio.entidades.Estudiante;
import com.biblioteca.dominio.entidades.Investigador;
import com.biblioteca.dominio.entidades.Libro;
import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Profesor;
import com.biblioteca.dominio.entidades.PublicoGeneral;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Revista;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.enumeraciones.NivelGravedad;
import com.biblioteca.dominio.enumeraciones.TipoDano;
import com.biblioteca.dominio.objetosvalor.Dano;
import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.servicios.interfaces.IBibliotecaFacade;
import com.biblioteca.servicios.interfaces.IAdministracionFacade;
import com.biblioteca.servicios.interfaces.IConsultaFacade;

public class MenuConsola {

    private final Scanner scanner;
    private boolean ejecutando;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Solo 3 fachadas — ningún repositorio ni servicio directo
    private final IBibliotecaFacade bibliotecaFacade;
    private final IConsultaFacade consultaFacade;
    private final IAdministracionFacade adminFacade;

    public MenuConsola(
            IBibliotecaFacade bibliotecaFacade,
            IConsultaFacade consultaFacade,
            IAdministracionFacade adminFacade) {

        this.scanner = new Scanner(System.in);
        this.ejecutando = true;

        this.bibliotecaFacade = bibliotecaFacade;
        this.consultaFacade = consultaFacade;
        this.adminFacade = adminFacade;
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
        System.out.println("            SISTEMA DE BIBLIOTECA");
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
    // MENÚ DE MATERIALES

    private void menuMateriales() {
        while (true) {
            try {
                System.out.println("            GESTIÓN DE MATERIALES");
                System.out.println("1.  Listar todos los materiales");
                System.out.println("2.  Buscar material (ID, título, autor)");
                System.out.println("3.  Ver materiales disponibles");
                System.out.println("4.  Ver materiales prestados");
                System.out.println("5.  Agregar nuevo material");
                System.out.println("6.  Actualizar estado");
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
                        verMaterialesDisponibles();
                        break;
                    case "4":
                        verMaterialesPrestados();
                        break;
                    case "5":
                        agregarMaterial();
                        break;
                    case "6":
                        actualizarEstadoMaterial();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
                pausa();
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado. Intente de nuevo.");
                pausa();
            }
        }
    }

    private void listarMateriales() {
        List<Material> materiales = consultaFacade.listarMateriales();
        if (materiales.isEmpty()) {
            System.out.println("\nNo hay materiales registrados");
            return;
        }

        System.out.println("\nLISTADO DE MATERIALES:");
        System.out.println("-".repeat(80));
        System.out.printf("%-10s %-30s %-20s %-15s %-10s%n",
                "ID", "TÍTULO", "AUTOR", "TIPO", "ESTADO");
        System.out.println("-".repeat(80));

        for (Material m : materiales) {
            System.out.printf("%-10s %-30s %-20s %-15s %-10s%n",
                    m.getId(),
                    truncar(m.getTitulo(), 28),
                    truncar(m.getAutor(), 18),
                    m.getTipo(),
                    m.getEstado());
        }
    }

    private void buscarMaterial() {
        System.out.print("\nIngrese ID, título o autor a buscar: ");
        String busqueda = scanner.nextLine();

        List<Material> resultados = consultaFacade.buscarMateriales(busqueda);

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron materiales");
            return;
        }

        if (resultados.size() == 1) {
            mostrarMaterialDetalle(resultados.get(0));
            return;
        }

        System.out.println("\nRESULTADOS:");
        for (Material m : resultados) {
            System.out.printf("%s - %s (%s)%n", m.getId(), m.getTitulo(), m.getAutor());
        }
    }

    private void verMaterialesDisponibles() {
        List<Material> disponibles = consultaFacade.obtenerMaterialesDisponibles();

        if (disponibles.isEmpty()) {
            System.out.println("\nNo hay materiales disponibles");
            return;
        }

        System.out.println("\nMATERIALES DISPONIBLES:");
        for (Material m : disponibles) {
            System.out.printf("  • %s - %s%n", m.getId(), m.getTitulo());
        }
    }

    private void verMaterialesPrestados() {
        List<Material> prestados = consultaFacade.obtenerMaterialesPrestados();

        if (prestados.isEmpty()) {
            System.out.println("\nNo hay materiales prestados");
            return;
        }

        System.out.println("\nMATERIALES PRESTADOS:");
        for (Material m : prestados) {
            System.out.printf("  • %s - %s%n", m.getId(), m.getTitulo());
        }
    }

    private void agregarMaterial() {
        System.out.println("\nAGREGAR NUEVO MATERIAL");
        System.out.println("Tipos disponibles:");
        System.out.println("1. Libro");
        System.out.println("2. DVD");
        System.out.println("3. Revista");
        System.out.println("4. EBook");
        System.out.print("Seleccione tipo: ");

        String tipo = scanner.nextLine();

        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        System.out.print("Precio: ");
        double precio = Double.parseDouble(scanner.nextLine());

        Material material = null;

        switch (tipo) {
            case "1":
                System.out.print("ISBN: ");
                String isbn = scanner.nextLine();
                System.out.print("Número de páginas: ");
                int paginas = Integer.parseInt(scanner.nextLine());
                System.out.print("¿Es best seller? (s/n): ");
                boolean bestSeller = scanner.nextLine().equalsIgnoreCase("s");
                System.out.print("¿Es referencia? (s/n): ");
                boolean referencia = scanner.nextLine().equalsIgnoreCase("s");
                material = new Libro(id, titulo, autor, isbn, paginas, bestSeller, referencia, precio);
                break;
            case "2":
                System.out.print("Código: ");
                String codigo = scanner.nextLine();
                System.out.print("Duración (minutos): ");
                int duracion = Integer.parseInt(scanner.nextLine());
                System.out.print("Director: ");
                String director = scanner.nextLine();
                material = new DVD(id, titulo, autor, codigo, duracion, director, precio);
                break;
            case "3":
                System.out.print("ISSN: ");
                String issn = scanner.nextLine();
                System.out.print("Número de edición: ");
                int edicion = Integer.parseInt(scanner.nextLine());
                System.out.print("¿Es último número? (s/n): ");
                boolean ultimo = scanner.nextLine().equalsIgnoreCase("s");
                material = new Revista(id, titulo, autor, issn, edicion, ultimo, precio);
                break;
            case "4":
                System.out.print("URL de descarga: ");
                String url = scanner.nextLine();
                System.out.print("Licencias disponibles: ");
                int licencias = Integer.parseInt(scanner.nextLine());
                material = new EBook(id, titulo, autor, url, licencias,
                        LocalDateTime.now().plusMonths(6), precio);
                break;
        }

        if (material != null) {
            Resultado resultado = adminFacade.agregarMaterial(material);
            if (resultado.getExito()) {
                System.out.println("Material agregado exitosamente");
            } else {
                System.out.println("Error: " + resultado.getMensaje());
            }
        }
    }

    private void actualizarEstadoMaterial() {
        System.out.print("\nIngrese ID del material: ");
        String id = scanner.nextLine();

        Material m = consultaFacade.obtenerMaterialPorId(id);
        if (m == null) {
            System.out.println("Material no encontrado");
            return;
        }

        System.out.println("Material: " + m.getTitulo());
        System.out.println("Estado actual: " + m.getEstado());
        System.out.println("\nNuevo estado:");
        System.out.println("1. DISPONIBLE");
        System.out.println("2. PRESTADO");
        System.out.println("3. RESERVADO");
        System.out.println("4. EN_REPARACION");
        System.out.println("5. PERDIDO");
        System.out.print("Seleccione: ");

        String opcion = scanner.nextLine();

        switch (opcion) {
            case "1":
                m.marcarComoDisponible();
                break;
            case "2":
                m.marcarComoPrestado();
                break;
            case "3":
                m.marcarComoReservado();
                break;
            case "4":
                m.marcarComoEnReparacion("Mantenimiento a través de consola");
                break;
            case "5":
                m.marcarComoPerdido("Reportado a través de consola");
                break;
            default:
                System.out.println("Opción no válida");
                return;
        }

        adminFacade.actualizarMaterial(m);
        System.out.println("Estado actualizado");
    }
    // MENÚ DE USUARIOS

    private void menuUsuarios() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE USUARIOS");
                System.out.println("=".repeat(60));
                System.out.println("1.  Listar todos los usuarios");
                System.out.println("2.  Buscar usuario (ID, nombre, email)");
                System.out.println("3.  Ver usuarios activos");
                System.out.println("4.  Ver usuarios bloqueados");
                System.out.println("5.  Agregar nuevo usuario");
                System.out.println("6.  Bloquear usuario");
                System.out.println("7.  Desbloquear usuario");
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
                        verUsuariosActivos();
                        break;
                    case "4":
                        verUsuariosBloqueados();
                        break;
                    case "5":
                        agregarUsuario();
                        break;
                    case "6":
                        bloquearUsuario();
                        break;
                    case "7":
                        desbloquearUsuario();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
                pausa();
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado al procesar el usuario.");
                pausa();
            }
        }
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = consultaFacade.listarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("\nNo hay usuarios registrados");
            return;
        }

        System.out.println("\nLISTADO DE USUARIOS:");
        System.out.println("-".repeat(70));
        System.out.printf("%-10s %-20s %-25s %-15s %-10s%n",
                "ID", "NOMBRE", "EMAIL", "TIPO", "ESTADO");
        System.out.println("-".repeat(70));

        for (Usuario u : usuarios) {
            System.out.printf("%-10s %-20s %-25s %-15s %-10s%n",
                    u.getId(),
                    truncar(u.getNombre(), 18),
                    truncar(u.getEmail(), 23),
                    u.getTipo(),
                    u.getEstado());
        }
    }

    private void buscarUsuario() {
        System.out.print("\nIngrese ID, nombre o email a buscar: ");
        String busqueda = scanner.nextLine();

        List<Usuario> resultados = consultaFacade.buscarUsuarios(busqueda);

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron usuarios");
            return;
        }

        if (resultados.size() == 1) {
            mostrarUsuarioDetalle(resultados.get(0));
            return;
        }

        System.out.println("\nRESULTADOS:");
        for (Usuario u : resultados) {
            System.out.printf("%s - %s (%s)%n", u.getId(), u.getNombre(), u.getEmail());
        }
    }

    private void verUsuariosActivos() {
        List<Usuario> activos = consultaFacade.obtenerUsuariosActivos();

        if (activos.isEmpty()) {
            System.out.println("\nNo hay usuarios activos");
            return;
        }

        System.out.println("\nUSUARIOS ACTIVOS:");
        for (Usuario u : activos) {
            System.out.printf("  • %s - %s%n", u.getId(), u.getNombre());
        }
    }

    private void verUsuariosBloqueados() {
        List<Usuario> bloqueados = consultaFacade.obtenerUsuariosBloqueados();

        if (bloqueados.isEmpty()) {
            System.out.println("\nNo hay usuarios bloqueados");
            return;
        }

        System.out.println("\nUSUARIOS BLOQUEADOS:");
        for (Usuario u : bloqueados) {
            System.out.printf("  • %s - %s (%s)%n", u.getId(), u.getNombre(), u.getEstado());
        }
    }

    private void agregarUsuario() {
        System.out.println("\nAGREGAR NUEVO USUARIO");
        System.out.println("Tipos disponibles:");
        System.out.println("1. Estudiante");
        System.out.println("2. Profesor");
        System.out.println("3. Investigador");
        System.out.println("4. Público General");
        System.out.print("Seleccione tipo: ");

        String tipo = scanner.nextLine();

        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Usuario usuario = null;

        switch (tipo) {
            case "1":
                System.out.print("Carrera: ");
                String carrera = scanner.nextLine();
                System.out.print("Semestre: ");
                int semestre = Integer.parseInt(scanner.nextLine());
                System.out.print("Universidad: ");
                String universidad = scanner.nextLine();
                usuario = new Estudiante(id, nombre, email, carrera, semestre, universidad);
                break;
            case "2":
                System.out.print("Departamento: ");
                String departamento = scanner.nextLine();
                System.out.print("Universidad: ");
                String uniProf = scanner.nextLine();
                System.out.print("Especialidad: ");
                String especialidad = scanner.nextLine();
                usuario = new Profesor(id, nombre, email, departamento, uniProf, especialidad);
                break;
            case "3":
                System.out.print("Línea de investigación: ");
                String linea = scanner.nextLine();
                System.out.print("Institución: ");
                String institucion = scanner.nextLine();
                usuario = new Investigador(id, nombre, email, linea, institucion);
                break;
            case "4":
                System.out.print("Dirección: ");
                String direccion = scanner.nextLine();
                System.out.print("Nombre del fiador: ");
                String fiador = scanner.nextLine();
                usuario = new PublicoGeneral(id, nombre, email, direccion, fiador);
                break;
        }

        if (usuario != null) {
            Resultado resultado = adminFacade.agregarUsuario(usuario);
            if (resultado.getExito()) {
                System.out.println("Usuario agregado exitosamente");
            } else {
                System.out.println("Error: " + resultado.getMensaje());
            }
        }
    }

    private void bloquearUsuario() {
        System.out.print("\nIngrese ID del usuario a bloquear: ");
        String id = scanner.nextLine();

        Usuario u = consultaFacade.obtenerUsuarioPorId(id);
        if (u == null) {
            System.out.println("Usuario no encontrado");
            return;
        }

        System.out.println("Usuario: " + u.getNombre());
        System.out.println("Estado actual: " + u.getEstado());
        System.out.print("Motivo del bloqueo: ");
        String motivo = scanner.nextLine();

        Resultado resultado = adminFacade.bloquearUsuario(id, motivo);
        if (resultado.getExito()) {
            System.out.println("Usuario bloqueado");
        } else {
            System.out.println("Error: " + resultado.getMensaje());
        }
    }

    private void desbloquearUsuario() {
        System.out.print("\nIngrese ID del usuario a desbloquear: ");
        String id = scanner.nextLine();

        Resultado resultado = adminFacade.desbloquearUsuario(id);
        if (resultado.getExito()) {
            System.out.println("Usuario desbloqueado");
        } else {
            System.out.println("Error: " + resultado.getMensaje());
        }
    }
    // MENÚ DE PRÉSTAMOS

    private void menuPrestamos() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE PRÉSTAMOS");
                System.out.println("=".repeat(60));
                System.out.println("1.  Registrar nuevo préstamo");
                System.out.println("2.  Buscar préstamos (Todos, Activos, por Usuario o ID)");
                System.out.println("3.  Ver préstamos vencidos");
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
                        buscarPrestamos();
                        break;
                    case "3":
                        verPrestamosVencidos();
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
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
                pausa();
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado al procesar el préstamo.");
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
        System.out.println("1. Normal");
        System.out.println("2. Interbibliotecario");
        System.out.print("Seleccione: ");
        String tipo = scanner.nextLine();

        String tipoPrestamo = tipo.equals("2") ? "INTERBIBLIOTECARIO" : "NORMAL";

        Resultado resultado = bibliotecaFacade.registrarPrestamo(idUsuario, idMaterial, tipoPrestamo);

        if (resultado.getExito()) {
            System.out.println("" + resultado.getMensaje());
        } else {
            System.out.println("Error: " + resultado.getMensaje());
        }
    }

    private void buscarPrestamos() {
        System.out.print("\nIngrese ID del usuario, ID del préstamo, o deje en blanco para ver todos los activos: ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            listarPrestamosActivos();
        } else if (input.startsWith("US-") || consultaFacade.obtenerUsuarioPorId(input) != null) {
            verPrestamosPorUsuario(input);
        } else {
            Prestamo p = consultaFacade.obtenerPrestamoPorId(input);
            if (p == null) {
                System.out.println("Préstamo no encontrado");
                return;
            }
            mostrarDetallePrestamo(p);
        }
    }

    private void mostrarDetallePrestamo(Prestamo p) {
        System.out.println("\nDETALLE DEL PRÉSTAMO:");
        System.out.println("ID: " + p.getId());
        System.out.println("Usuario: " + p.getIdUsuario());
        System.out.println("Material: " + p.getIdMaterial());
        System.out.println("Fecha préstamo: " + p.getFechaPrestamo().format(formatter));
        System.out.println("Fecha devolución esperada: " + p.getFechaDevolucionEsperada().format(formatter));
        System.out.println("Estado: " + p.getEstado());
    }

    private void listarPrestamosActivos() {
        List<Prestamo> activos = consultaFacade.listarPrestamosActivos();

        if (activos.isEmpty()) {
            System.out.println("\nNo hay préstamos activos");
            return;
        }

        System.out.println("\nPRÉSTAMOS ACTIVOS:");
        for (Prestamo p : activos) {
            System.out.printf("  • %s - Usuario: %s - Material: %s%n",
                    p.getId(), p.getIdUsuario(), p.getIdMaterial());
        }
    }

    private void verPrestamosPorUsuario(String idUsuario) {
        List<Prestamo> prestamos = consultaFacade.obtenerPrestamosPorUsuario(idUsuario);
        if (prestamos.isEmpty()) {
            System.out.println("El usuario no tiene préstamos activos");
            return;
        }
        System.out.println("\nPRÉSTAMOS DEL USUARIO " + idUsuario + ":");
        for (Prestamo p : prestamos) {
            System.out.printf("  • %s - Material: %s%n", p.getId(), p.getIdMaterial());
        }
    }

    private void verPrestamosVencidos() {
        List<Prestamo> vencidos = consultaFacade.obtenerPrestamosVencidos();

        if (vencidos.isEmpty()) {
            System.out.println("\nNo hay préstamos vencidos");
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        System.out.println("\nPRÉSTAMOS VENCIDOS:");
        for (Prestamo p : vencidos) {
            long diasVencido = ChronoUnit.DAYS.between(p.getFechaDevolucionEsperada(), ahora);
            System.out.printf("  • %s - Usuario: %s - Vencido: %d días%n",
                    p.getId(), p.getIdUsuario(), diasVencido);
        }
    }

    private void renovarPrestamo() {
        System.out.print("\nIngrese ID del préstamo a renovar: ");
        String idPrestamo = scanner.nextLine();

        Resultado resultado = bibliotecaFacade.renovarPrestamo(idPrestamo);

        if (resultado.getExito()) {
            System.out.println("" + resultado.getMensaje());
        } else {
            System.out.println("Error: " + resultado.getMensaje());
        }
    }
    // MENÚ DE DEVOLUCIONES

    private void menuDevoluciones() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            ↩️  GESTIÓN DE DEVOLUCIONES");
                System.out.println("=".repeat(60));
                System.out.println("1.  Registrar devolución de material");
                System.out.println("2.  Ver historial de devoluciones");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        registrarDevolucion();
                        break;
                    case "2":
                        verHistorialDevoluciones();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
                pausa();
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado al gestionar devoluciones.");
                pausa();
            }
        }
    }

    private void registrarDevolucion() {
        System.out.print("\nIngrese ID del préstamo: ");
        String idPrestamo = scanner.nextLine();

        Prestamo p = consultaFacade.obtenerPrestamoPorId(idPrestamo);
        if (p == null) {
            System.out.println("Préstamo no encontrado");
            return;
        }

        System.out.print("¿Pasó inspección visual sin problemas? (s/n): ");
        String respuesta = scanner.nextLine();

        if (respuesta.equalsIgnoreCase("n")) {
            System.out.println("\nINSPECCIÓN DE DAÑOS");
            List<Dano> danos = new java.util.ArrayList<>();

            while (true) {
                System.out.println("\nAgregar daño (o 'fin' para terminar):");
                System.out.print("Descripción: ");
                String descripcion = scanner.nextLine();
                if (descripcion.equalsIgnoreCase("fin"))
                    break;

                System.out.println("Gravedad:");
                System.out.println("1. LEVE");
                System.out.println("2. MODERADO");
                System.out.println("3. GRAVE");
                System.out.println("4. IRREPARABLE");
                System.out.print("Seleccione: ");
                String g = scanner.nextLine();
                NivelGravedad gravedad = switch (g) {
                    case "1" -> NivelGravedad.LEVE;
                    case "2" -> NivelGravedad.MODERADO;
                    case "3" -> NivelGravedad.GRAVE;
                    case "4" -> NivelGravedad.IRREPARABLE;
                    default -> NivelGravedad.LEVE;
                };

                System.out.println("Tipo de daño:");
                System.out.println("1. PAGINAS_RASGADAS");
                System.out.println("2. MANCHAS");
                System.out.println("3. CUBIERTA_DANADA");
                System.out.println("4. RAYONES");
                System.out.println("5. NO_FUNCIONAL");
                System.out.print("Seleccione: ");
                String t = scanner.nextLine();
                TipoDano tipoDano = switch (t) {
                    case "1" -> TipoDano.PAGINAS_RASGADAS;
                    case "2" -> TipoDano.MANCHAS;
                    case "3" -> TipoDano.CUBIERTA_DANADA;
                    case "4" -> TipoDano.RAYONES;
                    case "5" -> TipoDano.NO_FUNCIONAL;
                    default -> TipoDano.PAGINAS_RASGADAS;
                };

                danos.add(new Dano(descripcion, gravedad, tipoDano));
            }

            boolean usable = danos.stream().noneMatch(d -> d.getGravedad() == NivelGravedad.IRREPARABLE);
            Evaluacion evaluacion = new Evaluacion(usable, danos);

            Resultado resultado = bibliotecaFacade.devolverMaterial(idPrestamo, evaluacion);

            if (resultado.getExito()) {
                System.out.println("" + resultado.getMensaje());
            } else {
                System.out.println("Error: " + resultado.getMensaje());
            }
        } else {
            Resultado resultado = bibliotecaFacade.devolverMaterialSinInspeccion(idPrestamo);

            if (resultado.getExito()) {
                System.out.println("" + resultado.getMensaje());
            } else {
                System.out.println("Error: " + resultado.getMensaje());
            }
        }
    }

    private void verHistorialDevoluciones() {
        List<Prestamo> devueltos = consultaFacade.verHistorialDevoluciones();

        if (devueltos.isEmpty()) {
            System.out.println("\nNo hay devoluciones registradas");
            return;
        }

        System.out.println("\nHISTORIAL DE DEVOLUCIONES:");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Prestamo p : devueltos) {
            System.out.printf("  • %s - Usuario: %s - Devuelto: %s%n",
                    p.getId(),
                    p.getIdUsuario(),
                    p.getFechaDevolucionReal().format(fmt));
        }
    }
    // MENÚ DE RESERVAS

    private void menuReservas() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE RESERVAS");
                System.out.println("=".repeat(60));
                System.out.println("1.  Crear reserva");
                System.out.println("2.  Cancelar reserva");
                System.out.println("3.  Buscar reservas (Todas, por Usuario o Material)");
                System.out.println("4.  Limpiar reservas expiradas");
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
                        buscarReservas();
                        break;
                    case "4":
                        limpiarReservasExpiradas();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
                pausa();
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado al gestionar reservas.");
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

        System.out.println("Tipo de reserva:");
        System.out.println("1. Normal");
        System.out.println("2. Interbibliotecaria");
        System.out.print("Seleccione: ");
        String tipo = scanner.nextLine();

        String tipoReserva = tipo.equals("2") ? "INTERBIBLIOTECARIA" : "NORMAL";

        Resultado resultado = bibliotecaFacade.crearReserva(idUsuario, idMaterial, tipoReserva);

        if (resultado.getExito()) {
            System.out.println("" + resultado.getMensaje());
        } else {
            System.out.println("Error: " + resultado.getMensaje());
        }
    }

    private void cancelarReserva() {
        System.out.print("\nIngrese ID de la reserva a cancelar: ");
        String idReserva = scanner.nextLine();

        Resultado resultado = bibliotecaFacade.cancelarReserva(idReserva);

        if (resultado.getExito()) {
            System.out.println("" + resultado.getMensaje());
        } else {
            System.out.println("Error: " + resultado.getMensaje());
        }
    }

    private void buscarReservas() {
        System.out.print("\nIngrese ID del usuario, ID del material, o deje en blanco para ver todas: ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            verReservasActivas();
        } else if (input.startsWith("US-") || consultaFacade.obtenerUsuarioPorId(input) != null) {
            verReservasPorUsuario(input);
        } else {
            verReservasPorMaterial(input);
        }
    }

    private void verReservasActivas() {
        List<Reserva> activas = consultaFacade.listarReservasActivas();

        if (activas.isEmpty()) {
            System.out.println("\nNo hay reservas activas");
            return;
        }
        System.out.println("\nRESERVAS ACTIVAS:");
        for (Reserva r : activas) {
            System.out.printf("  • %s - Usuario: %s - Material: %s%n", r.getId(), r.getIdUsuario(), r.getIdMaterial());
        }
    }

    private void verReservasPorMaterial(String idMaterial) {
        List<Reserva> reservas = consultaFacade.obtenerReservasPorMaterial(idMaterial);
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas para este material");
            return;
        }
        System.out.println("\nRESERVAS PARA MATERIAL " + idMaterial + ":");
        for (Reserva r : reservas) {
            System.out.printf("  Pos %d: %s - Usuario: %s%n", r.getPosicionCola(), r.getId(), r.getIdUsuario());
        }
    }

    private void verReservasPorUsuario(String idUsuario) {
        List<Reserva> reservas = consultaFacade.obtenerReservasPorUsuario(idUsuario);
        if (reservas.isEmpty()) {
            System.out.println("El usuario no tiene reservas activas");
            return;
        }
        System.out.println("\nRESERVAS DEL USUARIO " + idUsuario + ":");
        for (Reserva r : reservas) {
            System.out.printf("  • %s - Material: %s%n", r.getId(), r.getIdMaterial());
        }
    }

    private void limpiarReservasExpiradas() {
        bibliotecaFacade.limpiarReservasExpiradas();
        System.out.println("Reservas expiradas eliminadas");
    }
    // MENÚ DE MULTAS

    private void menuMultas() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            GESTIÓN DE MULTAS");
                System.out.println("=".repeat(60));
                System.out.println("1.  Ver multas pendientes");
                System.out.println("2.  Ver multas por usuario");
                System.out.println("3.  Pagar multa");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        verMultasPendientes();
                        break;
                    case "2":
                        verMultasPorUsuario();
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
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
                pausa();
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado al procesar multas.");
                pausa();
            }
        }
    }

    private void verMultasPendientes() {
        List<Multa> pendientes = consultaFacade.listarMultasPendientes();

        if (pendientes.isEmpty()) {
            System.out.println("\nNo hay multas pendientes");
            return;
        }

        System.out.println("\nMULTAS PENDIENTES:");
        double total = 0;

        for (Multa m : pendientes) {
            double monto = m.calcularMontoTotal();
            total += monto;
            System.out.printf("  • %s - Usuario: %s - $%.2f - %s%n",
                    m.getId(), m.getIdUsuario(), monto, m.getMotivo());
        }

        System.out.printf("\nTOTAL GENERAL: $%.2f%n", total);
    }

    private void verMultasPorUsuario() {
        System.out.print("\nIngrese ID del usuario: ");
        String idUsuario = scanner.nextLine();

        List<Multa> multas = consultaFacade.obtenerMultasPorUsuario(idUsuario);

        if (multas.isEmpty()) {
            System.out.println("El usuario no tiene multas");
            return;
        }

        System.out.println("\nMULTAS DEL USUARIO " + idUsuario + ":");
        double totalPendiente = 0;

        for (Multa m : multas) {
            double monto = m.calcularMontoTotal();
            String estado = m.getEstado().toString();
            System.out.printf("  • %s - $%.2f - %s - %s%n",
                    m.getId(), monto, m.getMotivo(), estado);

            if (m.getEstado() == EstadoMulta.PENDIENTE) {
                totalPendiente += monto;
            }
        }

        if (totalPendiente > 0) {
            System.out.printf("\nTOTAL PENDIENTE: $%.2f%n", totalPendiente);
        }
    }

    private void pagarMulta() {
        System.out.print("\nIngrese ID de la multa a pagar: ");
        String idMulta = scanner.nextLine();

        // Primero consultamos para mostrar info al usuario
        List<Multa> pendientes = consultaFacade.listarMultasPendientes();
        Multa multa = pendientes.stream()
                .filter(m -> m.getId().equals(idMulta))
                .findFirst()
                .orElse(null);

        if (multa == null) {
            System.out.println("Multa pendiente no encontrada con ese ID");
            return;
        }

        double monto = multa.calcularMontoTotal();
        System.out.printf("Monto a pagar: $%.2f%n", monto);
        System.out.print("¿Confirmar pago? (s/n): ");

        if (scanner.nextLine().equalsIgnoreCase("s")) {
            Resultado resultado = adminFacade.pagarMulta(idMulta);
            if (resultado.getExito()) {
                System.out.println("" + resultado.getMensaje());
            } else {
                System.out.println("Error: " + resultado.getMensaje());
            }
        }
    }

    // MENÚ DE CONSULTAS

    private void menuConsultas() {
        while (true) {
            try {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            CONSULTAS Y REPORTES");
                System.out.println("=".repeat(60));
                System.out.println("1.  Ver disponibilidad de material");
                System.out.println("2.  Ver estado de usuario");
                System.out.println("3.  Ver estadísticas generales");
                System.out.println("4.  Ver límites por tipo de usuario");
                System.out.println("5.  Ver políticas de tiempo");
                System.out.println("6.  Ver reporte completo");
                System.out.println("0.  Volver");
                System.out.println("=".repeat(60));
                System.out.print("Seleccione: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        consultarDisponibilidad();
                        break;
                    case "2":
                        verEstadoUsuario();
                        break;
                    case "3":
                        verEstadisticas();
                        break;
                    case "4":
                        verLimitesUsuario();
                        break;
                    case "5":
                        verPoliticasTiempo();
                        break;
                    case "6":
                        verReporteCompleto();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
                pausa();
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
                pausa();
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado al procesar consultas.");
                pausa();
            }
        }
    }

    private void consultarDisponibilidad() {
        System.out.print("\nIngrese ID del material: ");
        String idMaterial = scanner.nextLine();

        Material m = consultaFacade.obtenerMaterialPorId(idMaterial);

        if (m == null) {
            System.out.println("Material no encontrado");
            return;
        }

        boolean disponible = consultaFacade.verificarDisponibilidad(idMaterial);
        EstadoMaterial estado = consultaFacade.obtenerEstadoActual(idMaterial);

        System.out.println("\nINFORMACIÓN DE DISPONIBILIDAD:");
        System.out.println("ID: " + m.getId());
        System.out.println("Título: " + m.getTitulo());
        System.out.println("Estado actual: " + estado);
        System.out.println("¿Disponible?: " + (disponible ? "SÍ" : "NO"));
        System.out.println("¿Prestable?: " +
                (consultaFacade.materialEsPrestable(idMaterial, m.getTipo()) ? "SÍ" : "NO"));
    }

    private void verEstadoUsuario() {
        System.out.print("\nIngrese ID del usuario: ");
        String idUsuario = scanner.nextLine();
        System.out.println(consultaFacade.generarEstadoUsuario(idUsuario));
    }

    private void verEstadisticas() {
        System.out.println(consultaFacade.generarEstadisticasGenerales());
    }

    private void verLimitesUsuario() {
        System.out.println(consultaFacade.generarLimitesUsuario());
    }

    private void verPoliticasTiempo() {
        System.out.println(consultaFacade.generarPoliticasTiempo());
    }

    private void verReporteCompleto() {
        System.out.println(consultaFacade.generarReporteCompleto());
    }
    // UTILIDADES

    private void mostrarMaterialDetalle(Material m) {
        System.out.println("\nDETALLE DEL MATERIAL:");
        System.out.println("ID: " + m.getId());
        System.out.println("Título: " + m.getTitulo());
        System.out.println("Autor: " + m.getAutor());
        System.out.println("Tipo: " + m.getTipo());
        System.out.println("Estado: " + m.getEstado());
        System.out.println("Fecha adquisición: " + m.getFechaAdquisicion().format(formatter));

        if (m instanceof Libro) {
            Libro l = (Libro) m;
            System.out.println("ISBN: " + l.getIsbn());
            System.out.println("Páginas: " + l.getNumeroPaginas());
            System.out.println("Best Seller: " + (l.esBestSeller() ? "Sí" : "No"));
            System.out.println("Referencia: " + (l.esReferencia() ? "Sí" : "No"));
        } else if (m instanceof DVD) {
            DVD d = (DVD) m;
            System.out.println("Código: " + d.getCodigo());
            System.out.println("Duración: " + d.getDuracionMinutos() + " minutos");
            System.out.println("Director: " + d.getDirector());
        } else if (m instanceof Revista) {
            Revista r = (Revista) m;
            System.out.println("ISSN: " + r.getIssn());
            System.out.println("Edición: " + r.getNumeroEdicion());
            System.out.println("Último número: " + (r.esUltimoNumero() ? "Sí" : "No"));
        } else if (m instanceof EBook) {
            EBook e = (EBook) m;
            System.out.println("URL: " + e.getUrlDescarga());
            System.out.println("Licencias disponibles: " + e.getLicenciasDisponibles());
            System.out.println("Vencimiento licencia: " + e.getFechaVencimientoLicencia().format(formatter));
        }
    }

    private void mostrarUsuarioDetalle(Usuario u) {
        System.out.println("\nDETALLE DEL USUARIO:");
        System.out.println("ID: " + u.getId());
        System.out.println("Nombre: " + u.getNombre());
        System.out.println("Email: " + u.getEmail());
        System.out.println("Tipo: " + u.getTipo());
        System.out.println("Estado: " + u.getEstado());
        System.out.println("Fecha registro: " + u.getFechaRegistro().format(formatter));

        if (u instanceof Estudiante) {
            Estudiante e = (Estudiante) u;
            System.out.println("Carrera: " + e.getCarrera());
            System.out.println("Semestre: " + e.getSemestre());
            System.out.println("Universidad: " + e.getUniversidad());
        } else if (u instanceof Profesor) {
            Profesor p = (Profesor) u;
            System.out.println("Departamento: " + p.getDepartamento());
            System.out.println("Universidad: " + p.getUniversidad());
            System.out.println("Especialidad: " + p.getEspecialidad());
        } else if (u instanceof Investigador) {
            Investigador i = (Investigador) u;
            System.out.println("Línea investigación: " + i.getLineaInvestigacion());
            System.out.println("Institución: " + i.getInstitucion());
        } else if (u instanceof PublicoGeneral) {
            PublicoGeneral pg = (PublicoGeneral) u;
            System.out.println("Dirección: " + pg.getDireccion());
            System.out.println("Fiador: " + pg.getFiador());
        }
    }

    private String truncar(String s, int max) {
        if (s == null)
            return "";
        if (s.length() <= max)
            return s;
        return s.substring(0, max - 3) + "...";
    }

    private void pausa() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}
