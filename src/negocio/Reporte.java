package negocio;

import java.time.LocalDate;

import modelo.Tutoria;

/**
 * MÓDULO 4 - Superclase ABSTRACTA de los reportes.
 * Responsable: Elías Navarrete.
 * Cada tipo de reporte se genera de forma distinta, por eso
 * generar() es abstracto y cada subclase lo sobrescribe.
 */
public abstract class Reporte {

    private String titulo;
    private String codigoMateria;   // "" = todas las materias
    private LocalDate desde;
    private LocalDate hasta;

    public Reporte(String titulo, String codigoMateria, LocalDate desde, LocalDate hasta) {
        this.titulo = titulo;
        this.codigoMateria = codigoMateria;
        this.desde = desde;
        this.hasta = hasta;
    }

    // Devuelve el texto del reporte
    public abstract String generar(GestorUsuarios gestorUsuarios, GestorTutorias gestorTutorias);

    // ¿La tutoría entra en los filtros del reporte?
    protected boolean cumpleFiltros(Tutoria t) {
        boolean materiaOk = codigoMateria.isEmpty()
                || t.getMateria().getCodigo().equalsIgnoreCase(codigoMateria);
        boolean fechaOk = !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta);
        return materiaOk && fechaOk;
    }

    protected String encabezado() {
        String materia = codigoMateria.isEmpty() ? "todas" : codigoMateria;
        return "\n===== " + titulo + " =====\nMateria: " + materia + " | Del " + desde + " al " + hasta + "\n";
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCodigoMateria() {
        return codigoMateria;
    }
}
