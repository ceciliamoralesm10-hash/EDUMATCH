package negocio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import modelo.Calificacion;
import modelo.DiaSemana;
import modelo.Disponibilidad;
import modelo.EstadoTutoria;
import modelo.Estudiante;
import modelo.Lugar;
import modelo.Materia;
import modelo.Tutor;
import modelo.Tutoria;

/**
 * MÓDULO 3 - Gestión de tutorías.
 * Responsable: Cecilia Morales.
 *
 * RF: Agendar tutoría, Cambiar el estado de una tutoría,
 *     Consultar historial y tutorías pendientes.
 */
public class GestorTutorias {

    private ArrayList<Tutoria> tutorias = new ArrayList<>();

    // Convierte la fecha en el día de la semana del enum (domingo = null)
    public static DiaSemana diaDeLaFecha(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        if (dia == DayOfWeek.SUNDAY) {
            return null;
        }
        return DiaSemana.values()[dia.getValue() - 1];
    }

    // ---------- RF: Agendar tutoría ----------
    public String agendarTutoria(Estudiante estudiante, Tutor tutor, Disponibilidad horario,
                                 LocalDate fecha, String tema) {
        if (tutor == null) {
            return "Rechazado: el tutor no existe.";
        }
        if (horario == null) {
            return "Rechazado: debe elegir un horario del tutor.";
        }
        if (tema.isEmpty()) {
            return "Rechazado: escriba el tema que quiere tratar.";
        }
        Materia materia = horario.getMateria();
        if (!tutor.estaHabilitadoEn(materia)) {
            return "Rechazado: el tutor no está habilitado en " + materia.getNombre() + ".";
        }
        if (tutor.getEstado() == modelo.EstadoTutor.SUSPENDIDO) {
            return "Rechazado: el tutor está suspendido.";
        }
        if (estudiante.getId().equals(tutor.getId())) {
            return "Rechazado: no puede agendarse una tutoría consigo mismo.";
        }
        if (diaDeLaFecha(fecha) != horario.getDia()) {
            return "Rechazado: el " + fecha + " no es " + horario.getDia() + ".";
        }
        if (!fecha.atTime(horario.getHoraInicio()).isAfter(LocalDateTime.now())) {
            return "Rechazado: esa fecha y hora ya pasaron.";
        }
        for (Tutoria t : tutorias) {
            if (t.estaActiva() && t.seCruzaCon(fecha, horario)) {
                if (t.getTutor().getId().equals(tutor.getId())) {
                    return "Rechazado: el tutor ya tiene una tutoría en ese horario.";
                }
                if (t.getLugar() == horario.getLugar()) {
                    return "Rechazado: el lugar " + horario.getLugar().getNombre() + " ya está ocupado.";
                }
                if (t.getEstudiante().getId().equals(estudiante.getId())
                        || t.getTutor().getId().equals(estudiante.getId())) {
                    return "Rechazado: usted ya tiene otra tutoría a esa hora.";
                }
            }
        }
        String codigo = nuevoCodigo();
        tutorias.add(new Tutoria(codigo, tema, fecha, estudiante, tutor, horario));
        return "Tutoría agendada con código " + codigo + " (estado: PENDIENTE).";
    }

    private String nuevoCodigo() {
        return String.format("TUT-%03d", tutorias.size() + 1);
    }

    // ---------- RF: Cambiar el estado de una tutoría ----------
    public String confirmarTutoria(Estudiante usuario, String codigo) {
        Tutoria t = buscarTutoria(codigo);
        if (t == null) {
            return "Error: no existe la tutoría " + codigo + ".";
        }
        if (!t.getTutor().getId().equals(usuario.getId())) {
            return "Error: solo el tutor puede confirmar la tutoría.";
        }
        if (t.getEstado() != EstadoTutoria.PENDIENTE) {
            return "Error: solo se puede confirmar una tutoría PENDIENTE (está " + t.getEstado() + ").";
        }
        t.confirmar(usuario.getId());
        return "Tutoría " + codigo + " confirmada.";
    }

    public String cancelarTutoria(Estudiante usuario, String codigo, String motivo) {
        Tutoria t = buscarTutoria(codigo);
        if (t == null) {
            return "Error: no existe la tutoría " + codigo + ".";
        }
        if (!esParticipante(t, usuario)) {
            return "Error: solo el tutor o el estudiante de la tutoría pueden cancelarla.";
        }
        if (motivo.isEmpty()) {
            return "Error: debe indicar el motivo de la cancelación.";
        }
        if (!t.estaActiva()) {
            return "Error: la tutoría ya está " + t.getEstado() + ".";
        }
        if (!t.puedeCancelarse()) {
            return "Error: solo se puede cancelar hasta " + Tutoria.HORAS_MINIMAS_CANCELAR
                    + " horas antes del inicio.";
        }
        t.cancelar(motivo);
        return "Tutoría " + codigo + " cancelada. El horario y el lugar quedan libres.";
    }

    public String marcarRealizada(Estudiante usuario, String codigo) {
        Tutoria t = buscarTutoria(codigo);
        if (t == null) {
            return "Error: no existe la tutoría " + codigo + ".";
        }
        if (!t.getTutor().getId().equals(usuario.getId())) {
            return "Error: solo el tutor puede marcarla como realizada.";
        }
        if (t.getEstado() != EstadoTutoria.CONFIRMADA) {
            return "Error: primero debe estar CONFIRMADA (está " + t.getEstado() + ").";
        }
        if (LocalDateTime.now().isBefore(t.getInicio())) {
            return "Error: solo se puede marcar como realizada después de la hora de inicio ("
                    + t.getInicio().toLocalDate() + " " + t.getInicio().toLocalTime() + ").";
        }
        t.marcarComoRealizada();
        return "Tutoría " + codigo + " marcada como realizada.";
    }

    // ---------- RF: Consultar historial (null o "" = sin filtro) ----------
    public ArrayList<Tutoria> consultarHistorial(Estudiante usuario, EstadoTutoria estado, String codigoMateria,
                                                 LocalDate desde, LocalDate hasta) {
        ArrayList<Tutoria> resultado = new ArrayList<>();
        for (Tutoria t : tutorias) {
            boolean estadoOk = estado == null || t.getEstado() == estado;
            boolean materiaOk = codigoMateria.isEmpty()
                    || t.getMateria().getCodigo().equalsIgnoreCase(codigoMateria);
            boolean desdeOk = desde == null || !t.getFecha().isBefore(desde);
            boolean hastaOk = hasta == null || !t.getFecha().isAfter(hasta);
            if (esParticipante(t, usuario) && estadoOk && materiaOk && desdeOk && hastaOk) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    // ---------- Consultas que usan los otros módulos ----------
    public Tutoria buscarTutoria(String codigo) {
        for (Tutoria t : tutorias) {
            if (t.getCodigo().equalsIgnoreCase(codigo)) {
                return t;
            }
        }
        return null;
    }

    private boolean esParticipante(Tutoria t, Estudiante usuario) {
        return t.getEstudiante().getId().equals(usuario.getId())
                || t.getTutor().getId().equals(usuario.getId());
    }

    public boolean tutorTieneActivasEnMateria(Tutor tutor, Materia materia) {
        for (Tutoria t : tutorias) {
            if (t.estaActiva() && t.getTutor() == tutor && t.getMateria() == materia) {
                return true;
            }
        }
        return false;
    }

    public boolean usuarioTieneActivas(Estudiante usuario) {
        for (Tutoria t : tutorias) {
            if (t.estaActiva() && esParticipante(t, usuario)) {
                return true;
            }
        }
        return false;
    }

    public boolean materiaTieneActivas(Materia materia) {
        for (Tutoria t : tutorias) {
            if (t.estaActiva() && t.getMateria() == materia) {
                return true;
            }
        }
        return false;
    }

    public boolean lugarTieneActivas(Lugar lugar) {
        for (Tutoria t : tutorias) {
            if (t.estaActiva() && t.getLugar() == lugar) {
                return true;
            }
        }
        return false;
    }

    // Todas las calificaciones que ha recibido un tutor
    public ArrayList<Calificacion> calificacionesDe(Tutor tutor) {
        ArrayList<Calificacion> lista = new ArrayList<>();
        for (Tutoria t : tutorias) {
            if (t.getTutor() == tutor && t.getCalificacion() != null) {
                lista.add(t.getCalificacion());
            }
        }
        return lista;
    }

    // Cuando un estudiante pasa a ser tutor, sus tutorías apuntan al nuevo objeto
    public void reemplazarEstudiante(Estudiante anterior, Tutor nuevo) {
        for (Tutoria t : tutorias) {
            t.reemplazarEstudiante(anterior, nuevo);
        }
    }

    // Solo para los datos de prueba: agrega una tutoría pasada
    public Tutoria agregarHistorica(Estudiante estudiante, Tutor tutor, Disponibilidad horario,
                                    LocalDate fecha, String tema, EstadoTutoria estado) {
        Tutoria t = new Tutoria(nuevoCodigo(), tema, fecha, estudiante, tutor, horario);
        t.establecerEstadoHistorico(estado);
        tutorias.add(t);
        return t;
    }

    public ArrayList<Tutoria> getTutorias() {
        return tutorias;
    }
}
