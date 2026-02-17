package com.biblioteca;

import java.time.LocalDateTime;

import com.biblioteca.consola.MenuConsola;
import com.biblioteca.dominio.entidades.DVD;
import com.biblioteca.dominio.entidades.EBook;
import com.biblioteca.dominio.entidades.Estudiante;
import com.biblioteca.dominio.entidades.Investigador;
import com.biblioteca.dominio.entidades.Libro;
import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.PrestamoInterbibliotecario;
import com.biblioteca.dominio.entidades.PrestamoNormal;
import com.biblioteca.dominio.entidades.Profesor;
import com.biblioteca.dominio.entidades.PublicoGeneral;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Revista;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.repositorios.RepositorioMaterialEnMemoria;
import com.biblioteca.repositorios.RepositorioMultaEnMemoria;
import com.biblioteca.repositorios.RepositorioPrestamoEnMemoria;
import com.biblioteca.repositorios.RepositorioReservaEnMemoria;
import com.biblioteca.repositorios.RepositorioUsuarioEnMemoria;
import com.biblioteca.servicios.calculadores.CalculadorMultaPorDano;
import com.biblioteca.servicios.calculadores.CalculadorMultaPorPerdida;
import com.biblioteca.servicios.calculadores.CalculadorMultaPorRetraso;
import com.biblioteca.servicios.implementaciones.CalculadorCostoDanoService;
import com.biblioteca.servicios.implementaciones.DevolucionService;
import com.biblioteca.servicios.implementaciones.DisponibilidadStandardService;
import com.biblioteca.servicios.implementaciones.GestorBloqueoService;
import com.biblioteca.servicios.implementaciones.GestorMultasService;
import com.biblioteca.servicios.implementaciones.InspeccionMaterialService;
import com.biblioteca.servicios.implementaciones.LimitePorTipoUsuarioService;
import com.biblioteca.servicios.implementaciones.NotificacionEmailService;
import com.biblioteca.servicios.implementaciones.PoliticaTiempoPorTipoService;
import com.biblioteca.servicios.implementaciones.PrestamoService;
import com.biblioteca.servicios.implementaciones.RenovacionService;
import com.biblioteca.servicios.implementaciones.ReservaService;
import com.biblioteca.servicios.implementaciones.ValidadorReglasService;
import com.biblioteca.servicios.implementaciones.reglas.ReglaLimiteNoExcedido;
import com.biblioteca.servicios.implementaciones.reglas.ReglaMaterialDisponible;
import com.biblioteca.servicios.implementaciones.reglas.ReglaMaterialExiste;
import com.biblioteca.servicios.implementaciones.reglas.ReglaUsuarioActivo;
import com.biblioteca.servicios.interfaces.ICalculadorCostoDanoService;
import com.biblioteca.servicios.interfaces.IDevolucionService;
import com.biblioteca.servicios.interfaces.IDisponibilidadService;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;
import com.biblioteca.servicios.interfaces.IInspeccionMaterialService;
import com.biblioteca.servicios.interfaces.ILimitePrestamoService;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IPoliticaTiempoService;
import com.biblioteca.servicios.interfaces.IPrestamoService;
import com.biblioteca.servicios.interfaces.IRenovacionService;
import com.biblioteca.servicios.interfaces.IReservaService;

public class Main {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("  SISTEMA DE BIBLIOTECA - VERSIÓN FINAL");
        System.out.println("======================================");

        // =====================================================
        // 1. INICIALIZAR REPOSITORIOS
        // =====================================================
        IRepositorio<Material> repoMaterial = new RepositorioMaterialEnMemoria();
        IRepositorio<Usuario> repoUsuario = new RepositorioUsuarioEnMemoria();
        IRepositorio<Prestamo> repoPrestamo = new RepositorioPrestamoEnMemoria();
        IRepositorio<Reserva> repoReserva = new RepositorioReservaEnMemoria();
        IRepositorio<Multa> repoMulta = new RepositorioMultaEnMemoria();

        // =====================================================
        // 2. CREAR SERVICIOS BASE
        // =====================================================
        IDisponibilidadService disponibilidadService = new DisponibilidadStandardService(repoMaterial, repoPrestamo);
        ILimitePrestamoService limiteService = new LimitePorTipoUsuarioService(repoUsuario, repoPrestamo);
        IPoliticaTiempoService politicaTiempoService = new PoliticaTiempoPorTipoService();
        INotificacionService notificacionService = new NotificacionEmailService();

        // =====================================================
        // 3. CREAR CALCULADOR DE COSTOS DE DAÑOS
        // =====================================================
        ICalculadorCostoDanoService calculadorCostoDano = new CalculadorCostoDanoService();

        // =====================================================
        // 4. CREAR CALCULADORES DE MULTAS (STRATEGY PATTERN)
        // =====================================================
        CalculadorMultaPorRetraso calculadorRetraso = new CalculadorMultaPorRetraso(repoPrestamo);
        CalculadorMultaPorDano calculadorDano = new CalculadorMultaPorDano(calculadorCostoDano);
        CalculadorMultaPorPerdida calculadorPerdida = new CalculadorMultaPorPerdida(repoMaterial);

        // =====================================================
        // 5. CREAR GESTOR DE MULTAS Y REGISTRAR CALCULADORES
        // =====================================================
        GestorMultasService gestorMultas = new GestorMultasService();
        gestorMultas.registrarCalculador(calculadorRetraso);
        gestorMultas.registrarCalculador(calculadorDano);
        gestorMultas.registrarCalculador(calculadorPerdida);

        // =====================================================
        // 6. CREAR OTROS SERVICIOS
        // =====================================================
        IGestorBloqueoService gestorBloqueo = new GestorBloqueoService(
                repoUsuario,
                repoMulta,
                repoPrestamo // ← Este parámetro faltaba
        );
        IInspeccionMaterialService inspeccionService = new InspeccionMaterialService(repoMaterial);

        // 6.1 Validador de reglas
        ValidadorReglasService validadorReglas = new ValidadorReglasService(
                limiteService, disponibilidadService, gestorBloqueo,
                (RepositorioUsuarioEnMemoria) repoUsuario,
                (RepositorioMaterialEnMemoria) repoMaterial
        );

        // Registrar reglas de validación
        validadorReglas.registrarRegla(new ReglaUsuarioActivo());
        validadorReglas.registrarRegla(new ReglaMaterialExiste());
        validadorReglas.registrarRegla(new ReglaMaterialDisponible(disponibilidadService));
        validadorReglas.registrarRegla(new ReglaLimiteNoExcedido(limiteService));

        // 6.2 Servicios principales
        IRenovacionService renovacionService = new RenovacionService(
                repoPrestamo,
                repoReserva,
                repoMaterial,
                repoUsuario,
                politicaTiempoService
        );

        IReservaService reservaService = new ReservaService(
                repoReserva, repoMaterial, disponibilidadService, validadorReglas, notificacionService
        );

        IPrestamoService prestamoService = new PrestamoService(
                validadorReglas, disponibilidadService, politicaTiempoService,
                repoPrestamo, repoMaterial, repoUsuario, notificacionService
        );

        IDevolucionService devolucionService = new DevolucionService(
                inspeccionService, gestorMultas, repoPrestamo, repoMaterial, repoUsuario,
                repoMulta, reservaService, notificacionService, gestorBloqueo
        );

        // =====================================================
        // 7. CARGAR DATOS DE EJEMPLO
        // =====================================================
        cargarDatosEjemplo(repoMaterial, repoUsuario, repoPrestamo);

        // =====================================================
        // 8. INICIAR MENÚ DE CONSOLA
        // =====================================================
        MenuConsola menu = new MenuConsola(
                repoMaterial, repoUsuario, repoPrestamo, repoReserva, repoMulta,
                disponibilidadService, limiteService, gestorBloqueo, gestorMultas,
                prestamoService, devolucionService, reservaService, renovacionService,
                inspeccionService, validadorReglas, politicaTiempoService  
        );

        menu.iniciar();
    }

   private static void cargarDatosEjemplo(
        IRepositorio<Material> repoMaterial,
        IRepositorio<Usuario> repoUsuario,
        IRepositorio<Prestamo> repoPrestamo) {

    System.out.println("\n📦 Cargando datos de ejemplo...");

    // =====================================================
    // MATERIALES
    // =====================================================
    Libro libro1 = new Libro("LIB-001", "Cien años de soledad", "Gabriel García Márquez",
            "978-84-376-0494-7", 471, true, false);
    Libro libro2 = new Libro("LIB-002", "El principito", "Antoine de Saint-Exupéry",
            "978-84-261-1930-6", 96, false, false);
    Libro libro3 = new Libro("LIB-003", "Don Quijote de la Mancha", "Miguel de Cervantes",
            "978-84-376-0494-8", 863, false, true);
    DVD dvd1 = new DVD("DVD-001", "Inception", "Christopher Nolan", "DVD-001-2024", 148, "Christopher Nolan");
    DVD dvd2 = new DVD("DVD-002", "El Padrino", "Francis Ford Coppola", "DVD-002-2024", 175, "Francis Ford Coppola");
    Revista revista1 = new Revista("REV-001", "National Geographic", "Varios", "0027-9358", 2024, true);
    Revista revista2 = new Revista("REV-002", "Muy Interesante", "Varios", "1130-1234", 2023, false);
    EBook ebook1 = new EBook("EBO-001", "Clean Code", "Robert Martin",
            "http://biblioteca.com/ebooks/clean-code", 5, LocalDateTime.now().plusMonths(6));
    EBook ebook2 = new EBook("EBO-002", "The Pragmatic Programmer", "David Thomas",
            "http://biblioteca.com/ebooks/pragmatic", 3, LocalDateTime.now().plusMonths(3));

    repoMaterial.agregar(libro1);
    repoMaterial.agregar(libro2);
    repoMaterial.agregar(libro3);
    repoMaterial.agregar(dvd1);
    repoMaterial.agregar(dvd2);
    repoMaterial.agregar(revista1);
    repoMaterial.agregar(revista2);
    repoMaterial.agregar(ebook1);
    repoMaterial.agregar(ebook2);

    // =====================================================
    // USUARIOS
    // =====================================================
    Estudiante estudiante1 = new Estudiante("USR-001", "Juan Pérez", "juan@email.com",
            "Ingeniería", 5, "Universidad Nacional");
    Estudiante estudiante2 = new Estudiante("USR-002", "María García", "maria@email.com",
            "Medicina", 3, "Universidad de Antioquia");
    Profesor profesor1 = new Profesor("USR-003", "Carlos Rodríguez", "carlos@email.com",
            "Ciencias", "Universidad Nacional", "Física");
    Profesor profesor2 = new Profesor("USR-004", "Ana Martínez", "ana@email.com",
            "Literatura", "Universidad de Antioquia", "Poesía");
    Investigador investigador1 = new Investigador("USR-005", "Luis Fernández", "luis@email.com",
            "Inteligencia Artificial", "Centro de Investigación");
    PublicoGeneral publico1 = new PublicoGeneral("USR-006", "Sofía López", "sofia@email.com",
            "Calle 123 #45-67", "Pedro López");

    repoUsuario.agregar(estudiante1);
    repoUsuario.agregar(estudiante2);
    repoUsuario.agregar(profesor1);
    repoUsuario.agregar(profesor2);
    repoUsuario.agregar(investigador1);
    repoUsuario.agregar(publico1);

    // =====================================================
    // PRÉSTAMOS DE EJEMPLO
    // =====================================================
    LocalDateTime hoy = LocalDateTime.now();

    // Préstamo 1: Activo (normal)
    Prestamo prestamo1 = new PrestamoNormal("USR-001", "LIB-001",
            hoy.minusDays(5),
            hoy.plusDays(10),
            "Estante A1");
    
    // Préstamo 2: Activo (normal)
    Prestamo prestamo2 = new PrestamoNormal("USR-003", "DVD-001",
            hoy.minusDays(2),
            hoy.plusDays(5),
            "Estante D3");
    
    // Préstamo 3: Activo (interbibliotecario)
    Prestamo prestamo3 = new PrestamoInterbibliotecario("USR-005", "LIB-003",
            hoy.minusDays(1),
            hoy.plusDays(20),
            "Biblioteca Central",
            "Biblioteca Sucursal",
            5000);
    
    
    LocalDateTime fechaPrestamoVencido = hoy.minusDays(30);
    LocalDateTime fechaDevolucionEsperada = hoy.minusDays(15); // Ya pasó
    
    Prestamo prestamoVencido = new PrestamoNormal(
        "USR-002",                // María García (estudiante)
        "LIB-002",                // El principito
        fechaPrestamoVencido,
        fechaDevolucionEsperada,
        "Estante B2"
    );
    
    // El material ya debería estar marcado como PRESTADO
    libro2.setEstado(EstadoMaterial.PRESTADO);
    
    // =====================================================
    // AGREGAR TODOS LOS PRÉSTAMOS
    // =====================================================
    repoPrestamo.agregar(prestamo1);
    repoPrestamo.agregar(prestamo2);
    repoPrestamo.agregar(prestamo3);
    repoPrestamo.agregar(prestamoVencido);  // ← NUEVO

    // Actualizar estados de materiales
    libro1.setEstado(EstadoMaterial.PRESTADO);
    dvd1.setEstado(EstadoMaterial.PRESTADO);
    libro3.setEstado(EstadoMaterial.PRESTADO);
    libro2.setEstado(EstadoMaterial.PRESTADO);  // ← NUEVO
    
    repoMaterial.actualizar(libro1);
    repoMaterial.actualizar(dvd1);
    repoMaterial.actualizar(libro3);
    repoMaterial.actualizar(libro2);  // ← NUEVO

    System.out.println("✅ Datos cargados:");
    System.out.println("   - Materiales: 9");
    System.out.println("   - Usuarios: 6");
    System.out.println("   - Préstamos activos: 4");  // ← Actualizado
    System.out.println("   - ✅ PRÉSTAMO VENCIDO: LIB-002 (El principito) - Usuario: USR-002 (María García)");
    System.out.println("     Fecha préstamo: hace 30 días");
    System.out.println("     Fecha devolución esperada: hace 15 días");
    System.out.println("     Días de retraso: 15\n");
}
}
