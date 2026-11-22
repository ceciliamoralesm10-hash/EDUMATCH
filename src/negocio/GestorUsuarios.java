package negocio;

import java.util.ArrayList;

import modelo.Administrador;
import modelo.Estudiante;
import modelo.Materia;
import modelo.Tutor;

/**
 * MÓDULO 1 - Usuarios y perfiles de tutor.
 * Responsable: Diego Espin.
 *
 * RF: Registrar estudiante, Habilitar estudiante como tutor en una materia,
 *     Consultar, actualizar y dar de baja el perfil.
 */
public class GestorUsuarios {

    private ArrayList<Estudiante> estudiantes = new ArrayList<>();   // incluye a los tutores
    private ArrayList<Administrador> administradores = new ArrayList<>();
    private GestorTutorias gestorTutorias;

    public GestorUsuarios(GestorTutorias gestorTutorias) {
        this.gestorTutorias = gestorTutorias;
    }

    // ---------- Inicio de sesión ----------
    public Estudiante iniciarSesionEstudiante(String id, String contrasena) {
        Estudiante e = buscarEstudiante(id);
        if (e != null && e.verificarContrasena(contrasena)) {
            return e;
        }
        return null;
    }

    public Administrador iniciarSesionAdministrador(String id, String contrasena) {
        for (Administrador a : administradores) {
            if (a.getId().equalsIgnoreCase(id) && a.verificarContrasena(contrasena)) {
                return a;
            }
        }
        return null;
    }

    // ---------- RF: Registrar estudiante ----------
    public String registrarEstudiante(String id, String nombres, String apellidos, String correo,
                                      String carrera, int semestre, String contrasena) {
        if (id.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || carrera.isEmpty()) {
            return "Error: el ID, los nombres, los apellidos y la carrera son obligatorios.";
        }
        if (buscarEstudiante(id) != null) {
            return "Error: ya existe un estudiante con el ID " + id + ". No se guardó.";
        }
        if (semestre < 1 || semestre > 10) {
            return "Error: el semestre debe estar entre 1 y 10.";
        }
        if (contrasena.length() < 4) {
            return "Error: la contraseña debe tener al menos 4 caracteres.";
        }
        Estudiante nuevo = new Estudiante(id, nombres, apellidos, correo, carrera, semestre, contrasena);
        if (!nuevo.correoInstitucionalValido()) {
            return "Error: el correo debe terminar en " + Estudiante.DOMINIO_UDLA + ". No se guardó.";
        }
        estudiantes.add(nuevo);
        return "Estudiante " + nuevo.getNombreCompleto() + " registrado correctamente.";
    }

    public void registrarAdministrador(String id, String nombre, String contrasena) {
        administradores.add(new Administrador(id, nombre, contrasena));
    }

    // ---------- RF: Habilitar estudiante como tutor (regla de negocio) ----------
    public String habilitarTutor(Estudiante estudiante, Materia materia, double nota, int semestreAprobado) {
        if (materia == null) {
            return "Rechazado: la materia no existe.";
        }
        if (nota < 0 || nota > 10) {
            return "Rechazado: la nota debe estar entre 0 y 10.";
        }
        if (nota < Tutor.NOTA_MINIMA) {
            return "Rechazado: necesita una nota de " + Tutor.NOTA_MINIMA + " o más (tiene " + nota + ").";
        }
        if (semestreAprobado < 1 || semestreAprobado > estudiante.getSemestre()) {
            return "Rechazado: el semestre en que la aprobó debe estar entre 1 y " + estudiante.getSemestre() + ".";
        }

        // Si todavía no es tutor, se crea el Tutor con sus datos y reemplaza al Estudiante
        Tutor tutor;
        if (estudiante instanceof Tutor) {
            tutor = (Tutor) estudiante;
        } else {
            tutor = new Tutor(estudiante);
        }
        if (tutor.estaHabilitadoEn(materia)) {
            return "Rechazado: ya es tutor de " + materia.getNombre() + ".";
        }
        if (!tutor.puedeHabilitarOtraMateria()) {
            return "Rechazado: ya es tutor de " + Tutor.MAX_MATERIAS + " materias (máximo).";
        }
        tutor.habilitarMateria(materia, nota);
        if (tutor != estudiante) {
            estudiantes.set(estudiantes.indexOf(estudiante), tutor);
            gestorTutorias.reemplazarEstudiante(estudiante, tutor);
        }
        return "Habilitado como tutor de " + materia.getNombre() + ".";
    }

    // ---------- RF: Actualizar perfil ("" o 0 = no cambiar) ----------
    public String actualizarPerfil(Estudiante e, String nombres, String apellidos, String correo,
                                   String carrera, int semestre, String contrasena) {
        if (!correo.isEmpty() && !correo.endsWith(Estudiante.DOMINIO_UDLA)) {
            return "Error: el correo debe terminar en " + Estudiante.DOMINIO_UDLA + ". No se cambió nada.";
        }
        if (semestre != 0 && (semestre < 1 || semestre > 10)) {
            return "Error: el semestre debe estar entre 1 y 10. No se cambió nada.";
        }
        if (!contrasena.isEmpty() && contrasena.length() < 4) {
            return "Error: la contraseña debe tener al menos 4 caracteres. No se cambió nada.";
        }
        e.actualizarDatos(nombres, apellidos, correo, carrera, semestre);
        if (!contrasena.isEmpty()) {
            e.setContrasena(contrasena);
        }
        return "Perfil actualizado.";
    }

    // ---------- RF: Retirarse como tutor de una materia ----------
    public String retirarComoTutor(Estudiante usuario, Materia materia) {
        if (!(usuario instanceof Tutor)) {
            return "Error: usted no es tutor.";
        }
        Tutor tutor = (Tutor) usuario;
        if (materia == null || !tutor.estaHabilitadoEn(materia)) {
            return "Error: no es tutor de esa materia.";
        }
        if (gestorTutorias.tutorTieneActivasEnMateria(tutor, materia)) {
            return "No puede retirarse: tiene tutorías pendientes en " + materia.getNombre() + ".";
        }
        tutor.retirarMateria(materia);
        return "Se retiró como tutor de " + materia.getNombre() + ".";
    }

    // ---------- RF: Dar de baja la cuenta ----------
    public String eliminarCuenta(Estudiante estudiante) {
        if (gestorTutorias.usuarioTieneActivas(estudiante)) {
            return "No se puede eliminar la cuenta: tiene tutorías pendientes.";
        }
        estudiantes.remove(estudiante);
        return "Cuenta eliminada.";
    }

    // ---------- Consultas ----------
    public Estudiante buscarEstudiante(String id) {
        for (Estudiante e : estudiantes) {
            if (e.getId().equalsIgnoreCase(id)) {
                return e;
            }
        }
        return null;
    }

    public Tutor buscarTutor(String id) {
        Estudiante e = buscarEstudiante(id);
        if (e instanceof Tutor) {
            return (Tutor) e;
        }
        return null;
    }

    public ArrayList<Tutor> getTutores() {
        ArrayList<Tutor> lista = new ArrayList<>();
        for (Estudiante e : estudiantes) {
            if (e instanceof Tutor) {
                lista.add((Tutor) e);
            }
        }
        return lista;
    }

    public ArrayList<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public ArrayList<Administrador> getAdministradores() {
        return administradores;
    }
}
