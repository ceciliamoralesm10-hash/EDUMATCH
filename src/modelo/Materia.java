package modelo;

/**
 * MÓDULO 2 - Asignatura en la que se puede pedir o dar tutoría.
 */
public class Materia {

    private String codigo;
    private String nombre;
    private String carrera;

    public Materia(String codigo, String nombre, String carrera) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.carrera = carrera;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setNombre(String nombre) {
        if (!nombre.isEmpty()) {
            this.nombre = nombre;
        }
    }

    public void setCarrera(String carrera) {
        if (!carrera.isEmpty()) {
            this.carrera = carrera;
        }
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + carrera + ")";
    }
}
