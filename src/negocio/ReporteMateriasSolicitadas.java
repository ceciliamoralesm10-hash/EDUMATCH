package negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import modelo.Tutoria;

/**
 * MÓDULO 4 - Materias donde más se pide ayuda,
 * ordenadas por número de tutorías agendadas.
 * Usa un HashMap (clase 13): materia -> cantidad de tutorías.
 * Responsable: Elías Navarrete.
 */
public class ReporteMateriasSolicitadas extends Reporte {

    public ReporteMateriasSolicitadas(String codigoMateria, LocalDate desde, LocalDate hasta) {
        super("MATERIAS MÁS SOLICITADAS", codigoMateria, desde, hasta);
    }

    @Override
    public String generar(GestorUsuarios gestorUsuarios, GestorTutorias gestorTutorias) {
        // 1. Contar tutorías por materia con un Map
        HashMap<String, Integer> conteo = new HashMap<>();
        for (Tutoria t : gestorTutorias.getTutorias()) {
            if (cumpleFiltros(t)) {
                String nombre = t.getMateria().getNombre();
                if (conteo.containsKey(nombre)) {
                    conteo.put(nombre, conteo.get(nombre) + 1);
                } else {
                    conteo.put(nombre, 1);
                }
            }
        }

        // 2. Pasar las claves a una lista y ordenarla de mayor a menor
        ArrayList<String> materias = new ArrayList<>(conteo.keySet());
        for (int i = 0; i < materias.size() - 1; i++) {
            for (int j = 0; j < materias.size() - 1 - i; j++) {
                if (conteo.get(materias.get(j)) < conteo.get(materias.get(j + 1))) {
                    String aux = materias.get(j);
                    materias.set(j, materias.get(j + 1));
                    materias.set(j + 1, aux);
                }
            }
        }

        // 3. Armar el texto
        String texto = encabezado();
        if (materias.isEmpty()) {
            texto += "No hay tutorías agendadas en ese rango.\n";
        }
        for (int i = 0; i < materias.size(); i++) {
            texto += (i + 1) + ". " + materias.get(i) + ": " + conteo.get(materias.get(i)) + " tutoría(s)\n";
        }
        return texto;
    }
}
