package negocio;

import java.time.LocalDate;
import java.util.ArrayList;

import modelo.EstadoTutoria;
import modelo.Materia;
import modelo.Tutor;
import modelo.Tutoria;

/**
 * MÓDULO 4 - Ranking de tutores: promedio y tutorías realizadas,
 * ordenado de mayor a menor promedio.
 * Responsable: Elías Navarrete.
 */
public class ReporteRankingTutores extends Reporte {

    public ReporteRankingTutores(String codigoMateria, LocalDate desde, LocalDate hasta) {
        super("RANKING DE TUTORES", codigoMateria, desde, hasta);
    }

    @Override
    public String generar(GestorUsuarios gestorUsuarios, GestorTutorias gestorTutorias) {
        // 1. Juntar los tutores (de la materia pedida, si hay filtro)
        ArrayList<Tutor> tutores = new ArrayList<>();
        for (Tutor t : gestorUsuarios.getTutores()) {
            if (getCodigoMateria().isEmpty() || esTutorDeLaMateria(t)) {
                tutores.add(t);
            }
        }

        // 2. Ordenar de mayor a menor promedio (ordenamiento burbuja)
        for (int i = 0; i < tutores.size() - 1; i++) {
            for (int j = 0; j < tutores.size() - 1 - i; j++) {
                if (tutores.get(j).getPromedio() < tutores.get(j + 1).getPromedio()) {
                    Tutor aux = tutores.get(j);
                    tutores.set(j, tutores.get(j + 1));
                    tutores.set(j + 1, aux);
                }
            }
        }

        // 3. Armar el texto
        String texto = encabezado();
        if (tutores.isEmpty()) {
            texto += "No hay tutores para mostrar.\n";
        }
        int puesto = 1;
        for (Tutor tutor : tutores) {
            texto += puesto + ". " + tutor.getNombreCompleto()
                    + " | Promedio: " + String.format("%.2f", tutor.getPromedio())
                    + " | Nivel: " + tutor.getNivel()
                    + " | Tutorías realizadas: " + contarRealizadas(gestorTutorias, tutor) + "\n";
            puesto++;
        }
        return texto;
    }

    private int contarRealizadas(GestorTutorias gestorTutorias, Tutor tutor) {
        int cantidad = 0;
        for (Tutoria t : gestorTutorias.getTutorias()) {
            if (t.getTutor() == tutor && t.getEstado() == EstadoTutoria.REALIZADA && cumpleFiltros(t)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private boolean esTutorDeLaMateria(Tutor tutor) {
        for (Materia m : tutor.getMateriasHabilitadas()) {
            if (m.getCodigo().equalsIgnoreCase(getCodigoMateria())) {
                return true;
            }
        }
        return false;
    }
}
