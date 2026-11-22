package negocio;

import java.time.LocalDate;
import java.time.LocalTime;

import modelo.Calificacion;
import modelo.DiaSemana;
import modelo.Disponibilidad;
import modelo.EstadoTutoria;
import modelo.Estudiante;
import modelo.Tutor;
import modelo.Tutoria;

/**
 * Arma el sistema: crea los cuatro gestores, los conecta entre sí
 * y carga los datos de prueba. La interfaz solo habla con esta clase
 * y con los gestores.
 */
public class SistemaEduMach {

    private GestorTutorias gestorTutorias;
    private GestorUsuarios gestorUsuarios;
    private GestorMateriasLugares gestorMateriasLugares;
    private GestorCalificaciones gestorCalificaciones;

    public SistemaEduMach() {
        gestorTutorias = new GestorTutorias();
        gestorUsuarios = new GestorUsuarios(gestorTutorias);
        gestorMateriasLugares = new GestorMateriasLugares(gestorUsuarios, gestorTutorias);
        gestorCalificaciones = new GestorCalificaciones(gestorTutorias, gestorUsuarios);
    }

    public GestorTutorias getGestorTutorias() {
        return gestorTutorias;
    }

    public GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }

    public GestorMateriasLugares getGestorMateriasLugares() {
        return gestorMateriasLugares;
    }

    public GestorCalificaciones getGestorCalificaciones() {
        return gestorCalificaciones;
    }

    // =================== DATOS DE PRUEBA ===================
    public void cargarDatosDePrueba() {
        gestorUsuarios.registrarAdministrador("admin", "Administrador EduMach", "admin123");

        gestorMateriasLugares.crearMateria("PROG2", "Programación II", "Ingeniería en IA");
        gestorMateriasLugares.crearMateria("CALC1", "Cálculo I", "Ingeniería");
        gestorMateriasLugares.crearMateria("FIS1", "Física I", "Ingeniería");

        gestorMateriasLugares.crearLugar("Sala 1", "Bloque 2", 6);
        gestorMateriasLugares.crearLugar("Biblioteca", "Edificio central", 4);

        gestorUsuarios.registrarEstudiante("A001", "Ana", "Torres", "ana.torres@udla.edu.ec", "Ingeniería en IA", 5, "1234");
        gestorUsuarios.registrarEstudiante("A002", "Luis", "Pérez", "luis.perez@udla.edu.ec", "Ingeniería en IA", 2, "1234");
        gestorUsuarios.registrarEstudiante("A003", "Marta", "Ruiz", "marta.ruiz@udla.edu.ec", "Ingeniería", 6, "1234");

        gestorUsuarios.habilitarTutor(gestorUsuarios.buscarEstudiante("A001"),
                gestorMateriasLugares.buscarMateria("PROG2"), 9.5, 2);
        gestorUsuarios.habilitarTutor(gestorUsuarios.buscarEstudiante("A001"),
                gestorMateriasLugares.buscarMateria("CALC1"), 9.2, 1);
        gestorUsuarios.habilitarTutor(gestorUsuarios.buscarEstudiante("A003"),
                gestorMateriasLugares.buscarMateria("PROG2"), 9.0, 2);

        Tutor ana = gestorUsuarios.buscarTutor("A001");
        Tutor marta = gestorUsuarios.buscarTutor("A003");
        Estudiante luis = gestorUsuarios.buscarEstudiante("A002");

        // Ana y Marta dan Programación II todos los días (así la prueba funciona cualquier día)
        for (DiaSemana dia : DiaSemana.values()) {
            gestorMateriasLugares.registrarDisponibilidad(ana, "PROG2", dia,
                    LocalTime.of(10, 0), LocalTime.of(12, 0), "Sala 1");
            gestorMateriasLugares.registrarDisponibilidad(marta, "PROG2", dia,
                    LocalTime.of(16, 0), LocalTime.of(17, 0), "Biblioteca");
        }
        gestorMateriasLugares.registrarDisponibilidad(ana, "CALC1", DiaSemana.MARTES,
                LocalTime.of(14, 0), LocalTime.of(15, 0), "Biblioteca");

        // Historial: dos tutorías de días anteriores (que no sean domingo)
        LocalDate haceTresDias = diaHabilAnterior(LocalDate.now().minusDays(3));
        LocalDate ayer = diaHabilAnterior(LocalDate.now().minusDays(1));

        // TUT-001: Luis con Ana, ya realizada y calificada
        Tutoria t1 = gestorTutorias.agregarHistorica(luis, ana, horarioDe(ana, haceTresDias, 10),
                haceTresDias, "Herencia", EstadoTutoria.REALIZADA);
        t1.registrarCalificacion(new Calificacion(5, 4, 5, "Explica muy claro", haceTresDias));
        gestorCalificaciones.recalcularReputacion(ana);

        // TUT-002: Luis con Marta, confirmada; Marta puede marcarla como realizada y Luis calificarla
        gestorTutorias.agregarHistorica(luis, marta, horarioDe(marta, ayer, 16),
                ayer, "Arreglos", EstadoTutoria.CONFIRMADA);
    }

    private LocalDate diaHabilAnterior(LocalDate fecha) {
        if (GestorTutorias.diaDeLaFecha(fecha) == null) {   // domingo
            return fecha.minusDays(1);
        }
        return fecha;
    }

    private Disponibilidad horarioDe(Tutor tutor, LocalDate fecha, int hora) {
        DiaSemana dia = GestorTutorias.diaDeLaFecha(fecha);
        for (Disponibilidad d : tutor.getDisponibilidades()) {
            if (d.getDia() == dia && d.getHoraInicio().getHour() == hora) {
                return d;
            }
        }
        return null;
    }
}
