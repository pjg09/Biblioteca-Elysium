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
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.repositorios.IRepositorioFactory;
import com.biblioteca.repositorios.RepositorioEnMemoriaFactory;

import com.biblioteca.servicios.calculadores.CalculadorMultaPorDano;
import com.biblioteca.servicios.calculadores.CalculadorMultaPorPerdida;
import com.biblioteca.servicios.calculadores.CalculadorMultaPorRetraso;
import com.biblioteca.servicios.implementaciones.CalculadorCostoDanoService;
import com.biblioteca.servicios.implementaciones.DevolucionService;
import com.biblioteca.servicios.implementaciones.DisponibilidadStandardService;
import com.biblioteca.servicios.implementaciones.GestorBloqueoService;
import com.biblioteca.servicios.implementaciones.GestorMultasService;

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

import com.biblioteca.servicios.interfaces.ILimitePrestamoService;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IPoliticaTiempoService;
import com.biblioteca.servicios.interfaces.IPrestamoService;
import com.biblioteca.servicios.interfaces.IRenovacionService;
import com.biblioteca.servicios.interfaces.IReservaService;
import com.biblioteca.servicios.interfaces.IServicioReportes;
import com.biblioteca.servicios.implementaciones.ServicioReportes;

public class Main {

        public static void main(String[] args) {
                System.out.println("======================================");
                System.out.println("  SISTEMA DE BIBLIOTECA - VERSIÓN FINAL");
                System.out.println("======================================");
        // FACTORY DE REPOSITORIOS
        // Usamos una fábrica para abstraer la creación de repositorios
        IRepositorioFactory repoFactory = new RepositorioEnMemoriaFactory();
        
        IRepositorio<Material> repoMaterial = repoFactory.crearRepositorioMaterial();
        IRepositorio<Usuario> repoUsuario = repoFactory.crearRepositorioUsuario();
        IRepositorio<Prestamo> repoPrestamo = repoFactory.crearRepositorioPrestamo();
        IRepositorio<Reserva> repoReserva = repoFactory.crearRepositorioReserva();
        IRepositorio<Multa> repoMulta = repoFactory.crearRepositorioMulta();
                // 2. CREAR SERVICIOS BASE
                IDisponibilidadService disponibilidadService = new DisponibilidadStandardService(repoMaterial,
                                repoPrestamo);
                ILimitePrestamoService limiteService = new LimitePorTipoUsuarioService(repoUsuario, repoPrestamo);
                IPoliticaTiempoService politicaTiempoService = new PoliticaTiempoPorTipoService();
                INotificacionService notificacionService = new NotificacionEmailService();
                // 3. CREAR CALCULADOR DE COSTOS DE DAÑOS
                ICalculadorCostoDanoService calculadorCostoDano = new CalculadorCostoDanoService();
                // 4. CREAR CALCULADORES DE MULTAS (STRATEGY PATTERN)
                CalculadorMultaPorRetraso calculadorRetraso = new CalculadorMultaPorRetraso(repoPrestamo, repoUsuario);
                CalculadorMultaPorDano calculadorDano = new CalculadorMultaPorDano(calculadorCostoDano);
                CalculadorMultaPorPerdida calculadorPerdida = new CalculadorMultaPorPerdida(repoMaterial, repoUsuario);
                // 5. CREAR SERVICIOS DE BLOQUEO E INSPECCIÓN
                IGestorBloqueoService gestorBloqueo = new GestorBloqueoService(
                                repoUsuario,
                                repoMulta,
                                repoPrestamo // ← Este parámetro faltaba
                );
                // 6. CREAR GESTOR DE MULTAS Y REGISTRAR CALCULADORES
                com.biblioteca.servicios.interfaces.IGestorMultasService gestorMultas = new GestorMultasService(repoMulta, repoUsuario, gestorBloqueo);
                gestorMultas.registrarCalculador(calculadorRetraso);
                gestorMultas.registrarCalculador(calculadorDano);
                gestorMultas.registrarCalculador(calculadorPerdida);

                // 6.1 Validador de reglas
                ValidadorReglasService validadorReglas = new ValidadorReglasService(
                                limiteService, disponibilidadService, gestorBloqueo,
                                repoUsuario,
                                repoMaterial);

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
                                politicaTiempoService);

                IReservaService reservaService = new ReservaService(
                                repoReserva, repoMaterial, disponibilidadService, validadorReglas, notificacionService);

                IPrestamoService prestamoService = new PrestamoService(
                                validadorReglas, disponibilidadService, politicaTiempoService,
                                repoPrestamo, repoMaterial, repoUsuario, notificacionService);

                IDevolucionService devolucionService = new DevolucionService(
                                gestorMultas, repoPrestamo, repoMaterial, repoUsuario,
                                repoMulta, reservaService, notificacionService, gestorBloqueo);

                IServicioReportes servicioReportes = new ServicioReportes(
                                repoMaterial, repoUsuario, repoPrestamo, repoReserva, repoMulta,
                                limiteService, politicaTiempoService);
                // 7. CREAR FACHADAS ESPECIALIZADAS
                com.biblioteca.servicios.interfaces.IBibliotecaFacade bibliotecaFacade = new com.biblioteca.servicios.BibliotecaFacade(
                                prestamoService, devolucionService, reservaService, renovacionService);

                com.biblioteca.servicios.interfaces.IConsultaFacade consultaFacade = new com.biblioteca.servicios.ConsultaFacade(
                                repoMaterial, repoUsuario, repoPrestamo, repoReserva, repoMulta,
                                disponibilidadService, servicioReportes);

                com.biblioteca.servicios.interfaces.IAdministracionFacade adminFacade = new com.biblioteca.servicios.AdministracionFacade(
                                repoMaterial, repoUsuario, gestorBloqueo, gestorMultas);
                // 8. CARGAR DATOS DE EJEMPLO
                cargarDatosEjemplo(repoMaterial, repoUsuario, repoPrestamo, repoReserva, repoMulta);
                // 9. INICIAR MENÚ DE CONSOLA (solo 3 fachadas)
                MenuConsola menu = new MenuConsola(bibliotecaFacade, consultaFacade, adminFacade);

                menu.iniciar();
        }

        private static void cargarDatosEjemplo(
                        IRepositorio<Material> repoMaterial,
                        IRepositorio<Usuario> repoUsuario,
                        IRepositorio<Prestamo> repoPrestamo,
                        IRepositorio<Reserva> repoReserva,
                        IRepositorio<Multa> repoMulta) {

                System.out.println("\nCargando datos de ejemplo...");
                // MATERIALES
                Libro libro1 = new Libro(("MAT-000001"), "Cien años de soledad", "Gabriel García Márquez",
                                "978-84-376-0494-7", 471, true, false, 80000.0);
                Libro libro2 = new Libro(("MAT-000002"), "El principito", "Antoine de Saint-Exupéry",
                                "978-84-261-1930-6", 96, false, false, 50000.0);
                Libro libro3 = new Libro(("MAT-000003"), "Don Quijote de la Mancha",
                                "Miguel de Cervantes",
                                "978-84-376-0494-8", 863, false, true, 120000.0);
                DVD dvd1 = new DVD(("MAT-000004"), "Inception", "Christopher Nolan", "DVD-001-2024", 148,
                                "Christopher Nolan", 35000.0);
                DVD dvd2 = new DVD(("MAT-000005"), "El Padrino", "Francis Ford Coppola", "DVD-002-2024", 175,
                                "Francis Ford Coppola", 40000.0);
                Revista revista1 = new Revista(("MAT-000006"), "National Geographic", "Varios", "0027-9358",
                                2024, true, 20000.0);
                Revista revista2 = new Revista(("MAT-000007"), "Muy Interesante", "Varios", "1130-1234",
                                2023, false, 15000.0);
                EBook ebook1 = new EBook(("MAT-000008"), "Clean Code", "Robert Martin",
                                "http://biblioteca.com/ebooks/clean-code", 5, LocalDateTime.now().plusMonths(6), 60000.0);
                EBook ebook2 = new EBook(("MAT-000009"), "The Pragmatic Programmer", "David Thomas",
                                "http://biblioteca.com/ebooks/pragmatic", 3, LocalDateTime.now().plusMonths(3), 60000.0);

                repoMaterial.agregar(libro1);
                repoMaterial.agregar(libro2);
                repoMaterial.agregar(libro3);
                repoMaterial.agregar(dvd1);
                repoMaterial.agregar(dvd2);
                repoMaterial.agregar(revista1);
                repoMaterial.agregar(revista2);
                repoMaterial.agregar(ebook1);
                repoMaterial.agregar(ebook2);
                // USUARIOS
                Estudiante estudiante1 = new Estudiante(("USR-000001"), "Juan Pérez", "juan@email.com",
                                "Ingeniería", 5, "Universidad Nacional");
                Estudiante estudiante2 = new Estudiante(("USR-000002"), "María García", "maria@email.com",
                                "Medicina", 3, "Universidad de Antioquia");
                Profesor profesor1 = new Profesor(("USR-000003"), "Carlos Rodríguez", "carlos@email.com",
                                "Ciencias", "Universidad Nacional", "Física");
                Profesor profesor2 = new Profesor(("USR-000004"), "Ana Martínez", "ana@email.com",
                                "Literatura", "Universidad de Antioquia", "Poesía");
                Investigador investigador1 = new Investigador(("USR-000005"), "Luis Fernández",
                                "luis@email.com",
                                "Inteligencia Artificial", "Centro de Investigación");
                PublicoGeneral publico1 = new PublicoGeneral(("USR-000006"), "Sofía López", "sofia@email.com",
                                "Calle 123 #45-67", "Pedro López");

                repoUsuario.agregar(estudiante1);
                repoUsuario.agregar(estudiante2);
                repoUsuario.agregar(profesor1);
                repoUsuario.agregar(profesor2);
                repoUsuario.agregar(investigador1);
                repoUsuario.agregar(publico1);
                // PRÉSTAMOS DE EJEMPLO
                LocalDateTime hoy = LocalDateTime.now();

                // Préstamo 1: Activo (normal)
                Prestamo prestamo1 = new PrestamoNormal(("PRE-000001"), ("USR-000001"), ("MAT-000001"),
                                hoy.plusDays(10),
                                "Estante A1");

                // Préstamo 2: Activo (normal)
                Prestamo prestamo2 = new PrestamoNormal(("PRE-000002"), ("USR-000003"), ("MAT-000004"),
                                hoy.plusDays(5),
                                "Estante D3");

                // Préstamo 3: Activo (interbibliotecario)
                Prestamo prestamo3 = new PrestamoInterbibliotecario(("PRE-000003"), ("USR-000005"),
                                ("MAT-000003"),
                                hoy.plusDays(20),
                                "Biblioteca Central",
                                "Biblioteca Sucursal",
                                5000);

                LocalDateTime fechaPrestamoVencido = hoy.minusDays(30);
                LocalDateTime fechaDevolucionEsperada = hoy.minusDays(15); // Ya pasó

                Prestamo prestamoVencido = new PrestamoNormal(
                                ("PRE-000004"),
                                ("USR-000002"), // María García (estudiante)
                                ("MAT-000002"), // El principito
                                hoy.plusDays(1), // Dummy value para pasar validación inicial
                                "Estante B2");

                try {
                    java.lang.reflect.Field fp = Prestamo.class.getDeclaredField("fechaPrestamo");
                    fp.setAccessible(true);
                    fp.set(prestamoVencido, fechaPrestamoVencido);
                    
                    java.lang.reflect.Field fd = Prestamo.class.getDeclaredField("fechaDevolucionEsperada");
                    fd.setAccessible(true);
                    fd.set(prestamoVencido, fechaDevolucionEsperada);
                } catch (Exception e) {
                    System.out.println("Error simulando fechas vencidas: " + e.getMessage());
                }
                // AGREGAR TODOS LOS PRÉSTAMOS
                repoPrestamo.agregar(prestamo1);
                repoPrestamo.agregar(prestamo2);
                repoPrestamo.agregar(prestamo3);
                repoPrestamo.agregar(prestamoVencido); // ← NUEVO

                // Actualizar estados de materiales
                libro1.marcarComoPrestado();
                dvd1.marcarComoPrestado();
                libro3.marcarComoPrestado();
                libro2.marcarComoPrestado(); // ← NUEVO

                repoMaterial.actualizar(libro1);
                repoMaterial.actualizar(dvd1);
                repoMaterial.actualizar(libro3);
                repoMaterial.actualizar(libro2); // ← NUEVO

                // RESERVAS DE EJEMPLO
                com.biblioteca.dominio.entidades.ReservaNormal reserva1 = new com.biblioteca.dominio.entidades.ReservaNormal("RES-000001", "USR-000004", "MAT-000001", "Sede Central");
                com.biblioteca.dominio.entidades.ReservaNormal reserva2 = new com.biblioteca.dominio.entidades.ReservaNormal("RES-000002", "USR-000006", "MAT-000004", "Sede Norte");
                
                repoReserva.agregar(reserva1);
                repoReserva.agregar(reserva2);
                
                // MULTAS DE EJEMPLO (Usuario USR-000002, por el préstamo vencido)
                com.biblioteca.dominio.entidades.MultaPorRetraso multa1 = new com.biblioteca.dominio.entidades.MultaPorRetraso("PRE-000004", "USR-000002", 15, 2000.0);
                repoMulta.agregar(multa1);

                System.out.println("Datos cargados:");
                System.out.println("   - Materiales: 9");
                System.out.println("   - Usuarios: 6");
                System.out.println("   - Préstamos activos: 4");
                System.out.println("   - Reservas activas: 2");
                System.out.println("   - Multas pendientes: 1");
                System.out.println(
                                "   - PRÉSTAMO VENCIDO: MAT-000002 (El principito) - Usuario: USR-000002 (María García)");
                System.out.println("     Fecha préstamo: hace 30 días");
                System.out.println("     Fecha devolución esperada: hace 15 días");
                System.out.println("     Días de retraso: 15\n");
        }
}

