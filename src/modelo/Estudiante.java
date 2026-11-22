package modelo;

/**
 * MÓDULO 1 - Estudiante de la UDLA que usa el sistema.
 * Puede solicitar tutorías. Si se habilita en una materia, pasa a ser un Tutor
 * (Tutor hereda de Estudiante).
 */
public class Estudiante {

    public static final String DOMINIO_UDLA = "@udla.edu.ec";

    private String id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String carrera;
    private int semestre;
    private String contrasena;

    public Estudiante(String id, String nombres, String apellidos, String correo,
                      String carrera, int semestre, String contrasena) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.carrera = carrera;
        this.semestre = semestre;
        this.contrasena = contrasena;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    // Regla de negocio: el correo debe ser institucional
    public boolean correoInstitucionalValido() {
        return correo != null
                && correo.endsWith(DOMINIO_UDLA)
                && correo.length() > DOMINIO_UDLA.length();
    }

    // Cambia solo los datos que llegan con valor ("" o 0 = no cambiar).
    // El gestor valida los datos antes de llamar a este método.
    public void actualizarDatos(String nombres, String apellidos, String correo,
                                String carrera, int semestre) {
        if (!nombres.isEmpty()) {
            this.nombres = nombres;
        }
        if (!apellidos.isEmpty()) {
            this.apellidos = apellidos;
        }
        if (!correo.isEmpty()) {
            this.correo = correo;
        }
        if (!carrera.isEmpty()) {
            this.carrera = carrera;
        }
        if (semestre != 0) {
            this.semestre = semestre;
        }
    }

    public boolean verificarContrasena(String contrasena) {
        return this.contrasena.equals(contrasena);
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // Solo la usa Tutor para copiar los datos al habilitarse
    protected String getContrasena() {
        return contrasena;
    }

    public String getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCarrera() {
        return carrera;
    }

    public int getSemestre() {
        return semestre;
    }

    public String getTipo() {
        return "Estudiante";
    }

    @Override
    public String toString() {
        return getTipo() + " | ID: " + id + " | " + getNombreCompleto() + " | " + correo
                + " | " + carrera + ", semestre " + semestre;
    }
}
