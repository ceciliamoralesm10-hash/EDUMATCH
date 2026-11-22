package negocio;

import java.time.LocalTime;
import java.util.ArrayList;

import modelo.DiaSemana;
import modelo.Disponibilidad;
import modelo.EstadoTutor;
import modelo.Lugar;
import modelo.Materia;
import modelo.Tutor;

/**
 * MÓDULO 2 - Materias, lugares y disponibilidad.
 * Responsable: Daniela Herrera.
 *
 * RF: Administrar materias y lugares (CRUD), Registrar disponibilidad del tutor,
 *     Buscar tutores disponibles.
 */
public class GestorMateriasLugares {

    private ArrayList<Materia> materias = new ArrayList<>();
    private ArrayList<Lugar> lugares = new ArrayList<>();
    private GestorUsuarios gestorUsuarios;
    private GestorTutorias gestorTutorias;

    public GestorMateriasLugares(GestorUsuarios gestorUsuarios, GestorTutorias gestorTutorias) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorTutorias = gestorTutorias;
    }

    // =================== MATERIAS (CRUD) ===================

    public String crearMateria(String codigo, String nombre, String carrera) {
        if (codigo.isEmpty() || nombre.isEmpty() || carrera.isEmpty()) {
            return "Error: el código, el nombre y la carrera son obligatorios.";
        }
        if (buscarMateria(codigo) != null) {
            return "Error: ya existe una materia con el código " + codigo + ".";
        }
        materias.add(new Materia(codigo.toUpperCase(), nombre, carrera));
        return "Materia creada.";
    }

    public String modificarMateria(String codigo, String nuevoNombre, String nuevaCarrera) {
        Materia materia = buscarMateria(codigo);
        if (materia == null) {
            return "Error: no existe la materia " + codigo + ".";
        }
        materia.setNombre(nuevoNombre);
        materia.setCarrera(nuevaCarrera);
        return "Materia actualizada: " + materia;
    }

    public String eliminarMateria(String codigo) {
        Materia materia = buscarMateria(codigo);
        if (materia == null) {
            return "Error: no existe la materia " + codigo + ".";
        }
        if (gestorTutorias.materiaTieneActivas(materia)) {
            return "No se puede eliminar: la materia tiene tutorías pendientes.";
        }
        for (Tutor t : gestorUsuarios.getTutores()) {
            t.retirarMateria(materia);
        }
        materias.remove(materia);
        return "Materia eliminada.";
    }

    public Materia buscarMateria(String codigo) {
        for (Materia m : materias) {
            if (m.getCodigo().equalsIgnoreCase(codigo)) {
                return m;
            }
        }
        return null;
    }

    public ArrayList<Materia> getMaterias() {
        return materias;
    }

    // =================== LUGARES (CRUD) ===================

    public String crearLugar(String nombre, String edificio, int capacidad) {
        if (nombre.isEmpty() || edificio.isEmpty()) {
            return "Error: el nombre y el edificio son obligatorios.";
        }
        if (buscarLugar(nombre) != null) {
            return "Error: ya existe un lugar llamado " + nombre + ".";
        }
        if (capacidad < Lugar.CAPACIDAD_MINIMA) {
            return "Error: la capacidad mínima es " + Lugar.CAPACIDAD_MINIMA + " personas.";
        }
        lugares.add(new Lugar(nombre, edificio, capacidad));
        return "Lugar creado.";
    }

    public String modificarLugar(String nombre, String nuevoEdificio, int nuevaCapacidad) {
        Lugar lugar = buscarLugar(nombre);
        if (lugar == null) {
            return "Error: no existe el lugar " + nombre + ".";
        }
        if (nuevaCapacidad < Lugar.CAPACIDAD_MINIMA) {
            return "Error: la capacidad mínima es " + Lugar.CAPACIDAD_MINIMA + " personas. No se cambió nada.";
        }
        lugar.setEdificio(nuevoEdificio);
        lugar.setCapacidad(nuevaCapacidad);
        return "Lugar actualizado: " + lugar;
    }

    public String eliminarLugar(String nombre) {
        Lugar lugar = buscarLugar(nombre);
        if (lugar == null) {
            return "Error: no existe el lugar " + nombre + ".";
        }
        if (gestorTutorias.lugarTieneActivas(lugar)) {
            return "No se puede eliminar: el lugar tiene tutorías pendientes.";
        }
        for (Tutor t : gestorUsuarios.getTutores()) {
            ArrayList<Disponibilidad> lista = t.getDisponibilidades();
            for (int i = lista.size() - 1; i >= 0; i--) {
                if (lista.get(i).getLugar() == lugar) {
                    lista.remove(i);
                }
            }
        }
        lugares.remove(lugar);
        return "Lugar eliminado.";
    }

    public Lugar buscarLugar(String nombre) {
        for (Lugar l : lugares) {
            if (l.getNombre().equalsIgnoreCase(nombre)) {
                return l;
            }
        }
        return null;
    }

    public ArrayList<Lugar> getLugares() {
        return lugares;
    }

    // =================== RF: Registrar disponibilidad ===================

    public String registrarDisponibilidad(Tutor tutor, String codigoMateria, DiaSemana dia,
                                          LocalTime horaInicio, LocalTime horaFin, String nombreLugar) {
        Materia materia = buscarMateria(codigoMateria);
        if (materia == null || !tutor.estaHabilitadoEn(materia)) {
            return "Error: no está habilitado como tutor en la materia " + codigoMateria + ".";
        }
        if (dia == null) {
            return "Error: el día debe ser LUNES, MARTES, MIERCOLES, JUEVES, VIERNES o SABADO.";
        }
        Lugar lugar = buscarLugar(nombreLugar);
        if (lugar == null) {
            return "Error: no existe el lugar " + nombreLugar + ".";
        }
        Disponibilidad nueva = new Disponibilidad(dia, horaInicio, horaFin, materia, lugar);
        if (!nueva.duracionValida()) {
            return "Error: el horario debe durar entre 1 y 2 horas.";
        }
        if (!nueva.dentroDeHorarioPermitido()) {
            return "Error: el horario debe estar entre las 07:00 y las 21:00.";
        }
        if (tutor.horarioSeCruza(nueva)) {
            return "Error: el horario se cruza con otro horario suyo.";
        }
        tutor.publicarDisponibilidad(nueva);
        return "Disponibilidad registrada: " + nueva;
    }

    // =================== RF: Buscar tutores disponibles ===================

    // Tutores activos que cumplen el filtro, de mayor a menor calificación
    public ArrayList<Tutor> buscarTutores(Materia materia, DiaSemana dia, LocalTime desde, LocalTime hasta,
                                          double calificacionMinima) {
        ArrayList<Tutor> encontrados = new ArrayList<>();
        if (materia == null || dia == null) {
            return encontrados;
        }
        for (Tutor tutor : gestorUsuarios.getTutores()) {
            if (tutor.getEstado() == EstadoTutor.ACTIVO && tutor.estaHabilitadoEn(materia)
                    && tutor.getPromedio() >= calificacionMinima
                    && !horariosEnRango(tutor, materia, dia, desde, hasta).isEmpty()) {
                encontrados.add(tutor);
            }
        }
        // Ordenamiento burbuja de mayor a menor promedio
        for (int i = 0; i < encontrados.size() - 1; i++) {
            for (int j = 0; j < encontrados.size() - 1 - i; j++) {
                if (encontrados.get(j).getPromedio() < encontrados.get(j + 1).getPromedio()) {
                    Tutor aux = encontrados.get(j);
                    encontrados.set(j, encontrados.get(j + 1));
                    encontrados.set(j + 1, aux);
                }
            }
        }
        return encontrados;
    }

    // Horarios del tutor para esa materia y ese día, dentro del rango de horas
    public ArrayList<Disponibilidad> horariosEnRango(Tutor tutor, Materia materia, DiaSemana dia,
                                                     LocalTime desde, LocalTime hasta) {
        ArrayList<Disponibilidad> lista = new ArrayList<>();
        for (Disponibilidad d : tutor.getDisponibilidades()) {
            if (d.getMateria() == materia && d.getDia() == dia
                    && !d.getHoraInicio().isBefore(desde) && !d.getHoraFin().isAfter(hasta)) {
                lista.add(d);
            }
        }
        return lista;
    }
}
