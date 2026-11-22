package modelo;

import java.time.LocalTime;

/**
 * MÓDULO 2 - Horario semanal que publica un tutor.
 * Ejemplo: LUNES de 10:00 a 12:00, para Programación II, en la Biblioteca.
 */
public class Disponibilidad {

    public static final LocalTime HORA_APERTURA = LocalTime.of(7, 0);
    public static final LocalTime HORA_CIERRE = LocalTime.of(21, 0);

    private DiaSemana dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Materia materia;   // "para"
    private Lugar lugar;       // "en"

    public Disponibilidad(DiaSemana dia, LocalTime horaInicio, LocalTime horaFin, Materia materia, Lugar lugar) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.materia = materia;
        this.lugar = lugar;
    }

    // Regla de negocio: dura entre 1 y 2 horas
    public boolean duracionValida() {
        return !horaFin.isBefore(horaInicio.plusHours(1))
                && !horaFin.isAfter(horaInicio.plusHours(2));
    }

    // Regla de negocio: entre las 07:00 y las 21:00
    public boolean dentroDeHorarioPermitido() {
        return !horaInicio.isBefore(HORA_APERTURA) && !horaFin.isAfter(HORA_CIERRE);
    }

    // Dos horarios se cruzan si son el mismo día y sus horas se solapan
    public boolean seCruzaCon(Disponibilidad otra) {
        return dia == otra.dia
                && horaInicio.isBefore(otra.horaFin)
                && otra.horaInicio.isBefore(horaFin);
    }

    public DiaSemana getDia() {
        return dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public Materia getMateria() {
        return materia;
    }

    public Lugar getLugar() {
        return lugar;
    }

    @Override
    public String toString() {
        return dia + " " + horaInicio + "-" + horaFin + " | " + materia.getNombre() + " | " + lugar.getNombre();
    }
}
