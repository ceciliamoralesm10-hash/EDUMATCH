package negocio;

import java.time.LocalDate;
import java.util.ArrayList;

import modelo.Calificacion;
import modelo.EstadoTutor;
import modelo.EstadoTutoria;
import modelo.Estudiante;
import modelo.Tutor;
import modelo.Tutoria;

/**
 * MÓDULO 4 - Calificación y reputación del tutor.
 * Responsable: Elías Navarrete.
 *
 * RF: Calificar una tutoría realizada, Calcular reputación y nivel del tutor,
 *     Generar reportes y ranking de tutores.
 */
public class GestorCalificaciones {

    private GestorTutorias gestorTutorias;
    private GestorUsuarios gestorUsuarios;

    public GestorCalificaciones(GestorTutorias gestorTutorias, GestorUsuarios gestorUsuarios) {
        this.gestorTutorias = gestorTutorias;
        this.gestorUsuarios = gestorUsuarios;
    }

    // ---------- RF: Calificar una tutoría realizada (regla de negocio) ----------
    public String calificarTutoria(Estudiante estudiante, String codigo, int dominio, int claridad,
                                   int puntualidad, String comentario) {
        Tutoria t = gestorTutorias.buscarTutoria(codigo);
        if (t == null) {
            return "Error: no existe la tutoría " + codigo + ".";
        }
        if (!t.getEstudiante().getId().equals(estudiante.getId())) {
            return "Error: solo el estudiante que recibió la tutoría puede calificarla.";
        }
        if (t.getEstado() != EstadoTutoria.REALIZADA) {
            return "Error: solo se puede calificar una tutoría REALIZADA (está " + t.getEstado() + ").";
        }
        if (t.getCalificacion() != null) {
            return "Error: esta tutoría ya fue calificada.";
        }
        if (!t.puedeCalificarse()) {
            return "Error: pasaron más de " + Tutoria.DIAS_PARA_CALIFICAR + " días; ya no se puede calificar.";
        }
        if (!Calificacion.criterioValido(dominio) || !Calificacion.criterioValido(claridad)
                || !Calificacion.criterioValido(puntualidad)) {
            return "Error: cada criterio debe estar entre 1 y 5.";
        }
        Calificacion c = new Calificacion(dominio, claridad, puntualidad, comentario, LocalDate.now());
        t.registrarCalificacion(c);
        recalcularReputacion(t.getTutor());
        return "Calificación guardada. Puntaje: " + String.format("%.2f", c.calcularPuntajeFinal()) + ". "
                + consultarReputacion(t.getTutor());
    }

    // ---------- RF: Calcular reputación y nivel del tutor ----------
    public void recalcularReputacion(Tutor tutor) {
        tutor.recalcularReputacion(gestorTutorias.calificacionesDe(tutor));
    }

    public String consultarReputacion(Tutor tutor) {
        String texto = "Reputación de " + tutor.getNombreCompleto() + ": promedio "
                + String.format("%.2f", tutor.getPromedio()) + " con "
                + gestorTutorias.calificacionesDe(tutor).size() + " calificación(es), nivel " + tutor.getNivel() + ".";
        if (tutor.getEstado() == EstadoTutor.SUSPENDIDO) {
            texto += " Estado: SUSPENDIDO (no aparece en las búsquedas).";
        }
        return texto;
    }

    public ArrayList<Calificacion> calificacionesDe(Tutor tutor) {
        return gestorTutorias.calificacionesDe(tutor);
    }

    // ---------- RF: Generar reportes y ranking de tutores ----------
    public String generarReportes(String codigoMateria, LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            return "Error: la fecha inicial no puede ser posterior a la fecha final.";
        }
        // Lista de la superclase: cada reporte responde con su propio generar()
        ArrayList<Reporte> reportes = new ArrayList<>();
        reportes.add(new ReporteRankingTutores(codigoMateria, desde, hasta));
        reportes.add(new ReporteMateriasSolicitadas(codigoMateria, desde, hasta));
        String resultado = "";
        for (Reporte r : reportes) {
            resultado += r.generar(gestorUsuarios, gestorTutorias) + "\n";
        }
        return resultado;
    }
}
