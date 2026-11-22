package modelo;

import java.time.LocalDate;

/**
 * MÓDULO 4 - Calificación que el estudiante le da al tutor
 * después de una tutoría realizada. Tres criterios de 1 a 5.
 */
public class Calificacion {

    public static final int PUNTAJE_MINIMO = 1;
    public static final int PUNTAJE_MAXIMO = 5;

    private int dominio;
    private int claridad;
    private int puntualidad;
    private String comentario;
    private LocalDate fecha;

    public Calificacion(int dominio, int claridad, int puntualidad, String comentario, LocalDate fecha) {
        this.dominio = dominio;
        this.claridad = claridad;
        this.puntualidad = puntualidad;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public static boolean criterioValido(int puntaje) {
        return puntaje >= PUNTAJE_MINIMO && puntaje <= PUNTAJE_MAXIMO;
    }

    // Regla de negocio: puntaje final = promedio de los tres criterios
    public double calcularPuntajeFinal() {
        return (dominio + claridad + puntualidad) / 3.0;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        String texto = fecha + " | Dominio " + dominio + ", claridad " + claridad + ", puntualidad " + puntualidad
                + " -> " + String.format("%.2f", calcularPuntajeFinal());
        if (!comentario.isEmpty()) {
            texto += " | \"" + comentario + "\"";
        }
        return texto;
    }
}
