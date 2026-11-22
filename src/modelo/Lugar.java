package modelo;

/**
 * MÓDULO 2 - Espacio de la universidad donde se dan tutorías
 * (salas de estudio, biblioteca, etc.).
 */
public class Lugar {

    public static final int CAPACIDAD_MINIMA = 2;   // tutor + estudiante

    private String nombre;
    private String edificio;
    private int capacidad;

    public Lugar(String nombre, String edificio, int capacidad) {
        this.nombre = nombre;
        this.edificio = edificio;
        this.capacidad = capacidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEdificio() {
        return edificio;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setEdificio(String edificio) {
        if (!edificio.isEmpty()) {
            this.edificio = edificio;
        }
    }

    public void setCapacidad(int capacidad) {
        if (capacidad >= CAPACIDAD_MINIMA) {
            this.capacidad = capacidad;
        }
    }

    @Override
    public String toString() {
        return nombre + " (" + edificio + ", " + capacidad + " personas)";
    }
}
