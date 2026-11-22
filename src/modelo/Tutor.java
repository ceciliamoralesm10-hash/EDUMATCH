package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * MÓDULOS 1 y 4 - Estudiante habilitado para dar tutorías.
 * Un Tutor ES UN Estudiante (herencia).
 * Publica su disponibilidad (composición) y tiene una reputación
 * que se recalcula con sus calificaciones (dependencia con Calificacion).
 */
public class Tutor extends Estudiante {

    public static final int MAX_MATERIAS = 5;
    public static final double NOTA_MINIMA = 9.0;

    private ArrayList<Materia> materiasHabilitadas = new ArrayList<>();
    private ArrayList<Disponibilidad> disponibilidades = new ArrayList<>();
    private double promedio = 0;
    private NivelTutor nivel = NivelTutor.NUEVO;
    private EstadoTutor estado = EstadoTutor.ACTIVO;

    // Crea el tutor copiando los datos del estudiante que se habilita
    public Tutor(Estudiante e) {
        super(e.getId(), e.getNombres(), e.getApellidos(), e.getCorreo(),
                e.getCarrera(), e.getSemestre(), e.getContrasena());
    }

    @Override
    public String getTipo() {
        return "Tutor";
    }

    // ================= Materias habilitadas (Módulo 1) =================

    public boolean puedeHabilitarOtraMateria() {
        return materiasHabilitadas.size() < MAX_MATERIAS;
    }

    // Regla de negocio: nota de 9,0 o más y menos de 5 materias
    public boolean habilitarMateria(Materia materia, double nota) {
        if (nota < NOTA_MINIMA || !puedeHabilitarOtraMateria() || estaHabilitadoEn(materia)) {
            return false;
        }
        materiasHabilitadas.add(materia);
        return true;
    }

    // Quita la materia y los horarios publicados para ella
    public void retirarMateria(Materia materia) {
        materiasHabilitadas.remove(materia);
        for (int i = disponibilidades.size() - 1; i >= 0; i--) {
            if (disponibilidades.get(i).getMateria() == materia) {
                disponibilidades.remove(i);
            }
        }
    }

    public boolean estaHabilitadoEn(Materia materia) {
        return materiasHabilitadas.contains(materia);
    }

    public ArrayList<Materia> getMateriasHabilitadas() {
        return materiasHabilitadas;
    }

    // ================= Disponibilidad (Módulo 2) =================

    public void publicarDisponibilidad(Disponibilidad disponibilidad) {
        disponibilidades.add(disponibilidad);
    }

    public boolean horarioSeCruza(Disponibilidad nueva) {
        for (Disponibilidad d : disponibilidades) {
            if (d.seCruzaCon(nueva)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Disponibilidad> getDisponibilidades() {
        return disponibilidades;
    }

    // ================= Reputación (Módulo 4) =================

    // Recalcula promedio, nivel y estado con todas sus calificaciones
    public void recalcularReputacion(List<Calificacion> calificaciones) {
        if (calificaciones.isEmpty()) {
            promedio = 0;
        } else {
            double suma = 0;
            for (Calificacion c : calificaciones) {
                suma += c.calcularPuntajeFinal();
            }
            promedio = suma / calificaciones.size();
        }

        if (calificaciones.size() < 3) {
            nivel = NivelTutor.NUEVO;
        } else if (promedio < 3.5) {
            nivel = NivelTutor.REGULAR;
        } else if (promedio < 4.5) {
            nivel = NivelTutor.BUENO;
        } else {
            nivel = NivelTutor.EXCELENTE;
        }

        // Regla de negocio: 5 o más calificaciones y promedio menor a 3,0 -> suspendido
        if (calificaciones.size() >= 5 && promedio < 3.0) {
            estado = EstadoTutor.SUSPENDIDO;
        } else {
            estado = EstadoTutor.ACTIVO;
        }
    }

    public double getPromedio() {
        return promedio;
    }

    public NivelTutor getNivel() {
        return nivel;
    }

    public EstadoTutor getEstado() {
        return estado;
    }
}
