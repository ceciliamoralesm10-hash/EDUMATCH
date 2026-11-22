package interfaz;

import java.time.LocalDate;

import modelo.Estudiante;
import modelo.Lugar;
import modelo.Materia;
import negocio.GestorCalificaciones;
import negocio.GestorMateriasLugares;
import negocio.SistemaEduMach;

/**
 * Menú de consola del administrador: materias, lugares y reportes.
 * Solo pide datos y muestra resultados; las reglas están en el paquete negocio.
 */
public class MenuAdministrador {

    private SistemaEduMach sistema;
    private GestorMateriasLugares gestorMateriasLugares;
    private GestorCalificaciones gestorCalificaciones;

    public MenuAdministrador(SistemaEduMach sistema) {
        this.sistema = sistema;
        this.gestorMateriasLugares = sistema.getGestorMateriasLugares();
        this.gestorCalificaciones = sistema.getGestorCalificaciones();
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ ADMINISTRADOR ---");
            System.out.println("1. Ver materias");
            System.out.println("2. Crear materia");
            System.out.println("3. Modificar materia");
            System.out.println("4. Eliminar materia");
            System.out.println("5. Ver lugares");
            System.out.println("6. Crear lugar");
            System.out.println("7. Modificar lugar");
            System.out.println("8. Eliminar lugar");
            System.out.println("9. Reportes y ranking de tutores");
            System.out.println("10. Ver estudiantes y tutores");
            System.out.println("0. Cerrar sesión");
            opcion = Consola.leerEntero("Opción: ");

            switch (opcion) {
                case 1:
                    listarMaterias();
                    break;
                case 2:
                    System.out.println(gestorMateriasLugares.crearMateria(Consola.leerTexto("Código: "),
                            Consola.leerTexto("Nombre: "), Consola.leerTexto("Carrera: ")));
                    break;
                case 3:
                    System.out.println(gestorMateriasLugares.modificarMateria(
                            Consola.leerTexto("Código de la materia: "),
                            Consola.leerTexto("Nuevo nombre (Enter = no cambiar): "),
                            Consola.leerTexto("Nueva carrera (Enter = no cambiar): ")));
                    break;
                case 4:
                    System.out.println(gestorMateriasLugares.eliminarMateria(
                            Consola.leerTexto("Código de la materia: ")));
                    break;
                case 5:
                    listarLugares();
                    break;
                case 6:
                    System.out.println(gestorMateriasLugares.crearLugar(Consola.leerTexto("Nombre: "),
                            Consola.leerTexto("Edificio: "), Consola.leerEntero("Capacidad (personas): ")));
                    break;
                case 7:
                    System.out.println(gestorMateriasLugares.modificarLugar(
                            Consola.leerTexto("Nombre del lugar: "),
                            Consola.leerTexto("Nuevo edificio (Enter = no cambiar): "),
                            Consola.leerEntero("Nueva capacidad: ")));
                    break;
                case 8:
                    System.out.println(gestorMateriasLugares.eliminarLugar(
                            Consola.leerTexto("Nombre del lugar: ")));
                    break;
                case 9:
                    String materia = Consola.leerTexto("Código de materia (Enter = todas): ");
                    LocalDate desde = Consola.leerFecha("Desde");
                    LocalDate hasta = Consola.leerFecha("Hasta");
                    System.out.println(gestorCalificaciones.generarReportes(materia, desde, hasta));
                    break;
                case 10:
                    for (Estudiante e : sistema.getGestorUsuarios().getEstudiantes()) {
                        System.out.println("   " + e);
                    }
                    break;
                case 0:
                    System.out.println("Sesión cerrada.");
                    break;
                default:
                    System.out.println("Opción no válida. Elija un número del menú.");
            }
        } while (opcion != 0);
    }

    private void listarMaterias() {
        if (gestorMateriasLugares.getMaterias().isEmpty()) {
            System.out.println("   (no hay materias)");
        }
        for (Materia m : gestorMateriasLugares.getMaterias()) {
            System.out.println("   " + m);
        }
    }

    private void listarLugares() {
        if (gestorMateriasLugares.getLugares().isEmpty()) {
            System.out.println("   (no hay lugares)");
        }
        for (Lugar l : gestorMateriasLugares.getLugares()) {
            System.out.println("   " + l);
        }
    }
}
