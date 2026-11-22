package interfaz;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import modelo.Calificacion;
import modelo.DiaSemana;
import modelo.Disponibilidad;
import modelo.EstadoTutoria;
import modelo.Estudiante;
import modelo.Materia;
import modelo.Tutor;
import modelo.Tutoria;
import negocio.GestorCalificaciones;
import negocio.GestorMateriasLugares;
import negocio.GestorTutorias;
import negocio.GestorUsuarios;
import negocio.SistemaEduMach;

/**
 * Menú de consola del estudiante y del tutor.
 * Solo pide datos y muestra resultados; las reglas están en el paquete negocio.
 */
public class MenuEstudiante {

    private String idUsuario;
    private Estudiante usuario;   // se vuelve a buscar en cada vuelta: puede pasar a ser Tutor
    private GestorUsuarios gestorUsuarios;
    private GestorMateriasLugares gestorMateriasLugares;
    private GestorTutorias gestorTutorias;
    private GestorCalificaciones gestorCalificaciones;

    public MenuEstudiante(SistemaEduMach sistema, String idUsuario) {
        this.idUsuario = idUsuario;
        this.gestorUsuarios = sistema.getGestorUsuarios();
        this.gestorMateriasLugares = sistema.getGestorMateriasLugares();
        this.gestorTutorias = sistema.getGestorTutorias();
        this.gestorCalificaciones = sistema.getGestorCalificaciones();
    }

    public void mostrar() {
        int opcion;
        do {
            usuario = gestorUsuarios.buscarEstudiante(idUsuario);
            if (usuario == null) {
                return;   // la cuenta fue eliminada
            }
            System.out.println("\n--- MENÚ " + usuario.getTipo().toUpperCase() + ": "
                    + usuario.getNombreCompleto() + " --- [hoy " + LocalDate.now() + "]");
            System.out.println(" PERFIL (Módulo 1)");
            System.out.println("  1. Ver mi perfil");
            System.out.println("  2. Actualizar mis datos");
            System.out.println("  3. Habilitarme como tutor en una materia");
            System.out.println("  4. Retirarme como tutor de una materia");
            System.out.println(" HORARIOS Y BÚSQUEDA (Módulo 2)");
            System.out.println("  5. Publicar mi disponibilidad (tutor)");
            System.out.println("  6. Ver mis horarios (tutor)");
            System.out.println("  7. Buscar tutores disponibles");
            System.out.println(" TUTORÍAS (Módulo 3)");
            System.out.println("  8. Agendar tutoría");
            System.out.println("  9. Mis tutorías (historial y pendientes)");
            System.out.println("  10. Confirmar una tutoría (tutor)");
            System.out.println("  11. Cancelar una tutoría");
            System.out.println("  12. Marcar una tutoría como realizada (tutor)");
            System.out.println(" CALIFICACIONES (Módulo 4)");
            System.out.println("  13. Calificar una tutoría realizada");
            System.out.println("  14. Ver mi reputación (tutor)");
            System.out.println(" OTROS");
            System.out.println("  15. Eliminar mi cuenta");
            System.out.println("  0. Cerrar sesión");
            opcion = Consola.leerEntero("Opción: ");

            switch (opcion) {
                case 1:
                    mostrarPerfil();
                    break;
                case 2:
                    actualizarPerfil();
                    break;
                case 3:
                    habilitarTutor();
                    break;
                case 4:
                    System.out.println(gestorUsuarios.retirarComoTutor(usuario,
                            gestorMateriasLugares.buscarMateria(Consola.leerTexto("Código de la materia: "))));
                    break;
                case 5:
                    publicarDisponibilidad();
                    break;
                case 6:
                    verHorarios();
                    break;
                case 7:
                    buscarTutores();
                    break;
                case 8:
                    agendarTutoria();
                    break;
                case 9:
                    verHistorial();
                    break;
                case 10:
                    System.out.println(gestorTutorias.confirmarTutoria(usuario,
                            Consola.leerTexto("Código de la tutoría: ")));
                    break;
                case 11:
                    System.out.println(gestorTutorias.cancelarTutoria(usuario,
                            Consola.leerTexto("Código de la tutoría: "), Consola.leerTexto("Motivo: ")));
                    break;
                case 12:
                    System.out.println(gestorTutorias.marcarRealizada(usuario,
                            Consola.leerTexto("Código de la tutoría: ")));
                    break;
                case 13:
                    calificar();
                    break;
                case 14:
                    verReputacion();
                    break;
                case 15:
                    if (eliminarCuenta()) {
                        opcion = 0;
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

    // Devuelve el usuario como Tutor, o null con un aviso si no lo es
    private Tutor comoTutor() {
        if (usuario instanceof Tutor) {
            return (Tutor) usuario;
        }
        System.out.println("Esta opción es solo para tutores. Primero habilítese en una materia (opción 3).");
        return null;
    }

    private void listarMaterias() {
        for (Materia m : gestorMateriasLugares.getMaterias()) {
            System.out.println("   " + m);
        }
    }

    // ===================== MÓDULO 1 =====================

    private void mostrarPerfil() {
        System.out.println("   " + usuario);
        if (usuario instanceof Tutor) {
            Tutor tutor = (Tutor) usuario;
            System.out.println("   Materias como tutor (" + tutor.getMateriasHabilitadas().size()
                    + " de " + Tutor.MAX_MATERIAS + "):");
            for (Materia m : tutor.getMateriasHabilitadas()) {
                System.out.println("     - " + m);
            }
            System.out.println("   " + gestorCalificaciones.consultarReputacion(tutor));
        }
    }

    private void actualizarPerfil() {
        System.out.println("Deje en blanco (Enter) lo que no quiera cambiar.");
        String nombres = Consola.leerTexto("Nombres (" + usuario.getNombres() + "): ");
        String apellidos = Consola.leerTexto("Apellidos (" + usuario.getApellidos() + "): ");
        String correo = Consola.leerTexto("Correo (" + usuario.getCorreo() + "): ");
        String carrera = Consola.leerTexto("Carrera (" + usuario.getCarrera() + "): ");
        int semestre = Consola.leerEntero("Semestre (actual " + usuario.getSemestre() + ", 0 = no cambiar): ");
        String contrasena = Consola.leerTexto("Nueva contraseña: ");
        System.out.println(gestorUsuarios.actualizarPerfil(usuario, nombres, apellidos, correo, carrera,
                semestre, contrasena));
    }

    private void habilitarTutor() {
        listarMaterias();
        Materia materia = gestorMateriasLugares.buscarMateria(Consola.leerTexto("Código de la materia: "));
        double nota = Consola.leerDecimal("Nota final (sobre 10): ");
        int semestre = Consola.leerEntero("Semestre en que la aprobó: ");
        System.out.println(gestorUsuarios.habilitarTutor(usuario, materia, nota, semestre));
    }

    private boolean eliminarCuenta() {
        String respuesta = Consola.leerTexto("¿Seguro que quiere eliminar su cuenta? (s/n): ");
        if (!respuesta.equalsIgnoreCase("s")) {
            return false;
        }
        String mensaje = gestorUsuarios.eliminarCuenta(usuario);
        System.out.println(mensaje);
        return mensaje.equals("Cuenta eliminada.");
    }

    // ===================== MÓDULO 2 =====================

    private void publicarDisponibilidad() {
        Tutor tutor = comoTutor();
        if (tutor == null) {
            return;
        }
        for (Materia m : tutor.getMateriasHabilitadas()) {
            System.out.println("   " + m);
        }
        String materia = Consola.leerTexto("Código de la materia: ");
        DiaSemana dia = Consola.leerDia("Día");
        LocalTime inicio = Consola.leerHora("Hora de inicio");
        LocalTime fin = Consola.leerHora("Hora de fin");
        for (int i = 0; i < gestorMateriasLugares.getLugares().size(); i++) {
            System.out.println("   " + gestorMateriasLugares.getLugares().get(i));
        }
        String lugar = Consola.leerTexto("Nombre del lugar: ");
        System.out.println(gestorMateriasLugares.registrarDisponibilidad(tutor, materia, dia, inicio, fin, lugar));
    }

    private void verHorarios() {
        Tutor tutor = comoTutor();
        if (tutor == null) {
            return;
        }
        if (tutor.getDisponibilidades().isEmpty()) {
            System.out.println("   (no tiene horarios publicados)");
        }
        for (Disponibilidad d : tutor.getDisponibilidades()) {
            System.out.println("   " + d);
        }
    }

    private void buscarTutores() {
        listarMaterias();
        String codigo = Consola.leerTexto("Código de la materia: ");
        Materia materia = gestorMateriasLugares.buscarMateria(codigo);
        if (materia == null) {
            System.out.println("Error: no existe la materia " + codigo + ".");
            return;
        }
        DiaSemana dia = Consola.leerDia("Día");
        LocalTime desde = Consola.leerHora("Desde");
        LocalTime hasta = Consola.leerHora("Hasta");
        double minima = Consola.leerDecimal("Calificación mínima (0 = cualquiera): ");

        ArrayList<Tutor> tutores = gestorMateriasLugares.buscarTutores(materia, dia, desde, hasta, minima);
        if (tutores.isEmpty()) {
            System.out.println("No se encontraron tutores con esos filtros.");
        }
        for (Tutor tutor : tutores) {
            System.out.println("-> " + tutor.getNombreCompleto() + " (ID " + tutor.getId() + ") | Promedio: "
                    + String.format("%.2f", tutor.getPromedio()) + " | Nivel: " + tutor.getNivel());
            for (Disponibilidad d : gestorMateriasLugares.horariosEnRango(tutor, materia, dia, desde, hasta)) {
                System.out.println("     " + d);
            }
        }
    }

    // ===================== MÓDULO 3 =====================

    private void agendarTutoria() {
        String idTutor = Consola.leerTexto("ID del tutor: ");
        Tutor tutor = gestorUsuarios.buscarTutor(idTutor);
        if (tutor == null) {
            System.out.println("Error: no existe un tutor con el ID " + idTutor + ".");
            return;
        }
        String codigo = Consola.leerTexto("Código de la materia: ");
        Materia materia = gestorMateriasLugares.buscarMateria(codigo);
        if (materia == null) {
            System.out.println("Error: no existe la materia " + codigo + ".");
            return;
        }
        LocalDate fecha = Consola.leerFecha("Fecha de la tutoría");
        DiaSemana dia = GestorTutorias.diaDeLaFecha(fecha);
        if (dia == null) {
            System.out.println("Error: no hay tutorías los domingos.");
            return;
        }
        ArrayList<Disponibilidad> horarios = gestorMateriasLugares.horariosEnRango(tutor, materia, dia,
                LocalTime.MIN, LocalTime.MAX);
        if (horarios.isEmpty()) {
            System.out.println("El tutor no tiene horarios de " + materia.getNombre() + " los " + dia + ".");
            return;
        }
        System.out.println("Horarios del tutor el " + dia + ":");
        for (int i = 0; i < horarios.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + horarios.get(i));
        }
        int elegido = Consola.leerEntero("Elija el horario: ");
        if (elegido < 1 || elegido > horarios.size()) {
            System.out.println("Error: elija un número entre 1 y " + horarios.size() + ".");
            return;
        }
        String tema = Consola.leerTexto("Tema que quiere tratar: ");
        System.out.println(gestorTutorias.agendarTutoria(usuario, tutor, horarios.get(elegido - 1), fecha, tema));
    }

    private void verHistorial() {
        System.out.println("Filtros (Enter = sin filtro):");
        EstadoTutoria estado = Consola.leerEstadoOpcional("Estado");
        String materia = Consola.leerTexto("Código de materia: ");
        LocalDate desde = null;
        LocalDate hasta = null;
        if (Consola.leerTexto("¿Filtrar por fechas? (s/n): ").equalsIgnoreCase("s")) {
            desde = Consola.leerFecha("Desde");
            hasta = Consola.leerFecha("Hasta");
        }
        ArrayList<Tutoria> lista = gestorTutorias.consultarHistorial(usuario, estado, materia, desde, hasta);
        if (lista.isEmpty()) {
            System.out.println("   (no hay tutorías con esos filtros)");
        }
        for (Tutoria t : lista) {
            System.out.println("   " + t);
        }
    }

    // ===================== MÓDULO 4 =====================

    private void calificar() {
        String codigo = Consola.leerTexto("Código de la tutoría: ");
        System.out.println("Califique de 1 a 5:");
        int dominio = Consola.leerEntero("  Dominio del tema: ");
        int claridad = Consola.leerEntero("  Claridad al explicar: ");
        int puntualidad = Consola.leerEntero("  Puntualidad: ");
        String comentario = Consola.leerTexto("  Comentario (opcional): ");
        System.out.println(gestorCalificaciones.calificarTutoria(usuario, codigo, dominio, claridad,
                puntualidad, comentario));
    }

    private void verReputacion() {
        Tutor tutor = comoTutor();
        if (tutor == null) {
            return;
        }
        System.out.println(gestorCalificaciones.consultarReputacion(tutor));
        for (Calificacion c : gestorCalificaciones.calificacionesDe(tutor)) {
            System.out.println("   - " + c);
        }
    }
}
