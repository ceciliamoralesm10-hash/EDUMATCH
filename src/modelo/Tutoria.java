package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MÓDULO 3 - Sesión de tutoría.
 * Un Estudiante la solicita, un Tutor la atiende y reserva una Disponibilidad
 * del tutor en una fecha. Puede recibir 0..1 Calificacion (composición).
 * Ciclo de vida: PENDIENTE -> CONFIRMADA -> REALIZADA, o CANCELADA.
 */
public class Tutoria {

    public static final int HORAS_MINIMAS_CANCELAR = 2;
    public static final int DIAS_PARA_CALIFICAR = 7;

    private String codigo;
    private String tema;
    private LocalDate fecha;
    private EstadoTutoria estado;
    private String motivoCancelacion = "";

    private Estudiante estudiante;          // "solicita"
    private Tutor tutor;                    // "atiende"
    private Disponibilidad disponibilidad;  // "reserva"
    private Calificacion calificacion;      // "recibe" (0..1)

    public Tutoria(String codigo, String tema, LocalDate fecha, Estudiante estudiante, Tutor tutor,
                   Disponibilidad disponibilidad) {
        this.codigo = codigo;
        this.tema = tema;
        this.fecha = fecha;
        this.estudiante = estudiante;
        this.tutor = tutor;
        this.disponibilidad = disponibilidad;
        this.estado = EstadoTutoria.PENDIENTE;   // regla: toda tutoría nace Pendiente
        this.calificacion = null;
    }

    // Fecha y hora exactas en que empieza
    public LocalDateTime getInicio() {
        return fecha.atTime(disponibilidad.getHoraInicio());
    }

    // Ocupa horario y lugar mientras esté Pendiente o Confirmada
    public boolean estaActiva() {
        return estado == EstadoTutoria.PENDIENTE || estado == EstadoTutoria.CONFIRMADA;
    }

    // ¿Choca con otro horario el mismo día?
    public boolean seCruzaCon(LocalDate otraFecha, Disponibilidad otra) {
        return fecha.equals(otraFecha)
                && disponibilidad.getHoraInicio().isBefore(otra.getHoraFin())
                && otra.getHoraInicio().isBefore(disponibilidad.getHoraFin());
    }

    // ================= Cambios de estado =================

    // Solo el tutor de la tutoría puede confirmarla
    public void confirmar(String idTutor) {
        if (estado == EstadoTutoria.PENDIENTE && tutor.getId().equals(idTutor)) {
            estado = EstadoTutoria.CONFIRMADA;
        }
    }

    public void cancelar(String motivo) {
        if (puedeCancelarse()) {
            estado = EstadoTutoria.CANCELADA;
            motivoCancelacion = motivo;
        }
    }

    // Solo después de la hora de inicio y si estaba confirmada
    public void marcarComoRealizada() {
        if (estado == EstadoTutoria.CONFIRMADA && !LocalDateTime.now().isBefore(getInicio())) {
            estado = EstadoTutoria.REALIZADA;
        }
    }

    // Regla: se puede cancelar hasta 2 horas antes del inicio
    public boolean puedeCancelarse() {
        return estaActiva()
                && !LocalDateTime.now().plusHours(HORAS_MINIMAS_CANCELAR).isAfter(getInicio());
    }

    // Regla: Realizada, sin calificar y dentro de los 7 días siguientes
    public boolean puedeCalificarse() {
        return estado == EstadoTutoria.REALIZADA
                && calificacion == null
                && !LocalDate.now().isAfter(fecha.plusDays(DIAS_PARA_CALIFICAR));
    }

    public void registrarCalificacion(Calificacion calificacion) {
        if (puedeCalificarse()) {
            this.calificacion = calificacion;
        }
    }

    // Cuando un estudiante se convierte en tutor, se actualiza la referencia
    public void reemplazarEstudiante(Estudiante anterior, Tutor nuevo) {
        if (estudiante == anterior) {
            estudiante = nuevo;
        }
    }

    // Solo para cargar el historial de los datos de prueba
    public void establecerEstadoHistorico(EstadoTutoria estado) {
        this.estado = estado;
    }

    // ================= Getters =================

    public String getCodigo() {
        return codigo;
    }

    public String getTema() {
        return tema;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoTutoria getEstado() {
        return estado;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    public Materia getMateria() {
        return disponibilidad.getMateria();
    }

    public Lugar getLugar() {
        return disponibilidad.getLugar();
    }

    public Calificacion getCalificacion() {
        return calificacion;
    }

    @Override
    public String toString() {
        String texto = codigo + " | " + fecha + " " + disponibilidad.getHoraInicio() + "-"
                + disponibilidad.getHoraFin() + " | " + getMateria().getNombre()
                + " | Tutor: " + tutor.getNombreCompleto() + " | Estudiante: " + estudiante.getNombreCompleto()
                + " | " + getLugar().getNombre() + " | Tema: " + tema + " | " + estado;
        if (estado == EstadoTutoria.CANCELADA) {
            texto += " (motivo: " + motivoCancelacion + ")";
        }
        if (calificacion != null) {
            texto += " | Calificada: " + String.format("%.2f", calificacion.calcularPuntajeFinal());
        }
        return texto;
    }
}
