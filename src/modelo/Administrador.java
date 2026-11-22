package modelo;

/**
 * MÓDULO 2 - Persona que administra las materias y los lugares
 * donde se pueden dar tutorías.
 */
public class Administrador {

    private String id;
    private String nombre;
    private String contrasena;

    public Administrador(String id, String nombre, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
    }

    public boolean verificarContrasena(String contrasena) {
        return this.contrasena.equals(contrasena);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "Administrador | ID: " + id + " | " + nombre;
    }
}
