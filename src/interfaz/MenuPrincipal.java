package interfaz;

import modelo.Administrador;
import modelo.Estudiante;
import negocio.SistemaEduMach;

/**
 * EduMach - Tutorías entre compañeros de la UDLA.
 * Punto de entrada del programa (main) y menú principal de consola.
 *
 * Usuarios de prueba:
 *   admin / admin123  (Administrador)
 *   A001  / 1234      (Ana Torres, tutora de Programación II y Cálculo I)
 *   A002  / 1234      (Luis Pérez, estudiante)
 *   A003  / 1234      (Marta Ruiz, tutora de Programación II)
 */
public class MenuPrincipal {

    private SistemaEduMach sistema;

    public MenuPrincipal(SistemaEduMach sistema) {
        this.sistema = sistema;
    }

    public static void main(String[] args) {
        SistemaEduMach sistema = new SistemaEduMach();
        sistema.cargarDatosDePrueba();
        new MenuPrincipal(sistema).mostrar();
    }

    public void mostrar() {
        System.out.println("=========================================");
        System.out.println("   EduMach - Tutorías entre compañeros");
        System.out.println("=========================================");

        int opcion;
        do {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Iniciar sesión como estudiante o tutor");
            System.out.println("2. Iniciar sesión como administrador");
            System.out.println("3. Registrarme como estudiante");
            System.out.println("0. Salir");
            opcion = Consola.leerEntero("Opción: ");

            switch (opcion) {
                case 1:
                    iniciarSesionEstudiante();
                    break;
                case 2:
                    iniciarSesionAdministrador();
                    break;
                case 3:
                    registrarEstudiante();
                    break;
                case 0:
                    System.out.println("¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción no válida. Elija un número del menú.");
            }
        } while (opcion != 0);
    }

    private void iniciarSesionEstudiante() {
        String id = Consola.leerTexto("ID: ");
        String contrasena = Consola.leerTexto("Contraseña: ");
        Estudiante e = sistema.getGestorUsuarios().iniciarSesionEstudiante(id, contrasena);
        if (e == null) {
            System.out.println("Error: ID o contraseña incorrectos.");
            return;
        }
        System.out.println("Bienvenido/a, " + e.getNombreCompleto() + " (" + e.getTipo() + ")");
        new MenuEstudiante(sistema, e.getId()).mostrar();
    }

    private void iniciarSesionAdministrador() {
        String id = Consola.leerTexto("ID: ");
        String contrasena = Consola.leerTexto("Contraseña: ");
        Administrador a = sistema.getGestorUsuarios().iniciarSesionAdministrador(id, contrasena);
        if (a == null) {
            System.out.println("Error: ID o contraseña incorrectos.");
            return;
        }
        System.out.println("Bienvenido/a, " + a.getNombre());
        new MenuAdministrador(sistema).mostrar();
    }

    private void registrarEstudiante() {
        System.out.println("\n--- REGISTRO DE ESTUDIANTE ---");
        String id = Consola.leerTexto("ID de estudiante: ");
        String nombres = Consola.leerTexto("Nombres: ");
        String apellidos = Consola.leerTexto("Apellidos: ");
        String correo = Consola.leerTexto("Correo institucional: ");
        String carrera = Consola.leerTexto("Carrera: ");
        int semestre = Consola.leerEntero("Semestre actual (1 a 10): ");
        String contrasena = Consola.leerTexto("Contraseña (mínimo 4 caracteres): ");
        System.out.println(sistema.getGestorUsuarios().registrarEstudiante(
                id, nombres, apellidos, correo, carrera, semestre, contrasena));
    }
}
