package com.biblioteca;

import java.time.LocalDateTime;

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
import com.biblioteca.servicios.implementaciones.GestorMultasService;
import com.biblioteca.servicios.interfaces.ICalculadorCostoDanoService;

public class Main {
    public static void main(String[] args) {
      
        
        // 1.  REPOS
    
        IRepositorio<Material> repoMaterial = new RepositorioMaterialEnMemoria();
        IRepositorio<Usuario> repoUsuario = new RepositorioUsuarioEnMemoria();
        IRepositorio<Prestamo> repoPrestamo = new RepositorioPrestamoEnMemoria();
        IRepositorio<Reserva> repoReserva = new RepositorioReservaEnMemoria();
        IRepositorio<Multa> repoMulta = new RepositorioMultaEnMemoria();
        
        // 2. SERVICIOS BASE

    
        
        // 3.  CALCULADOR DE COSTOS DE DAÑOS 
        ICalculadorCostoDanoService calculadorCostoDano = new CalculadorCostoDanoService();
        
        // 4. CREAR CALCULADORES DE MULTAS

        CalculadorMultaPorRetraso calculadorRetraso = new CalculadorMultaPorRetraso(repoPrestamo);
        CalculadorMultaPorDano calculadorDano = new CalculadorMultaPorDano(calculadorCostoDano); 
        CalculadorMultaPorPerdida calculadorPerdida = new CalculadorMultaPorPerdida(repoMaterial);
        
        // 5. CREAR GESTOR DE MULTAS Y REGISTRAR CALCULADORES

        GestorMultasService gestorMultas = new GestorMultasService();
        gestorMultas.registrarCalculador(calculadorRetraso);
        gestorMultas.registrarCalculador(calculadorDano);
        gestorMultas.registrarCalculador(calculadorPerdida);
        
        // 6.  REGLAS DE VALIDACIÓN
        
        
    
       
        
        // 9. CARGAR DATOS DE EJEMPLO

        cargarDatosEjemplo(repoMaterial, repoUsuario);
        
        // 10. INICIAR MENÚ DE CONSOLA

        
    }
    
    private static void cargarDatosEjemplo(IRepositorio<Material> repoMaterial, IRepositorio<Usuario> repoUsuario) {
        // Crear algunos materiales de ejemplo
        Libro libro1 = new Libro("LIB-001", "Cien años de soledad", "Gabriel García Márquez", 
                                 "978-84-376-0494-7", 471, true, false);
        Libro libro2 = new Libro("LIB-002", "El principito", "Antoine de Saint-Exupéry", 
                                 "978-84-261-1930-6", 96, false, false);
        DVD dvd1 = new DVD("DVD-001", "Inception", "Christopher Nolan", "DVD-001-2024", 148, "Christopher Nolan");
        Revista revista1 = new Revista("REV-001", "National Geographic", "Varios", "0027-9358", 2024, true);
        EBook ebook1 = new EBook("EBO-001", "Clean Code", "Robert Martin", 
                                 "http://biblioteca.com/ebooks/clean-code", 5, LocalDateTime.now().plusMonths(6));
        
        repoMaterial.agregar(libro1);
        repoMaterial.agregar(libro2);
        repoMaterial.agregar(dvd1);
        repoMaterial.agregar(revista1);
        repoMaterial.agregar(ebook1);
        
        // Crear usuarios de ejemplo
        Estudiante estudiante = new Estudiante("USR-001", "Juan Pérez", "juan@email.com", 
                                              "Ingeniería", 5, "Universidad Nacional");
        Profesor profesor = new Profesor("USR-002", "María García", "maria@email.com", 
                                        "Ciencias", "Universidad Nacional", "Física");
        Investigador investigador = new Investigador("USR-003", "Carlos López", "carlos@email.com", 
                                                    "Inteligencia Artificial", "Centro de Investigación");
        PublicoGeneral publico = new PublicoGeneral("USR-004", "Ana Martínez", "ana@email.com", 
                                                   "Calle 123", "Pedro Martínez");
        
        repoUsuario.agregar(estudiante);
        repoUsuario.agregar(profesor);
        repoUsuario.agregar(investigador);
        repoUsuario.agregar(publico);
        
       
    }
}