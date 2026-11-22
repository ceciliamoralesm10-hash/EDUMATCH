package interfaz;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Scanner;

import modelo.DiaSemana;
import modelo.EstadoTutoria;

/**
 * Lee datos del teclado y vuelve a preguntar si el dato no es válido,
 * para que el programa no se caiga si el usuario escribe algo mal.
 * (Revisa carácter por carácter en lugar de usar excepciones,
 * que se ven en el Progreso 3.)
 */
public class Consola {

    private static Scanner sc = new Scanner(System.in);

    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    public static int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (soloDigitos(texto) && texto.length() <= 9) {
                return Integer.parseInt(texto);
            }
            System.out.println("   Escriba un número entero.");
        }
    }

    public static double leerDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).replace(',', '.');
            if (esDecimal(texto)) {
                return Double.parseDouble(texto);
            }
            System.out.println("   Escriba un número (ejemplo: 9.5).");
        }
    }

    // Formato AAAA-MM-DD, por ejemplo 2026-11-23
    public static LocalDate leerFecha(String mensaje) {
        while (true) {
            String t = leerTexto(mensaje + " (AAAA-MM-DD): ");
            if (t.length() == 10 && t.charAt(4) == '-' && t.charAt(7) == '-'
                    && soloDigitos(t.substring(0, 4)) && soloDigitos(t.substring(5, 7))
                    && soloDigitos(t.substring(8, 10))) {
                int anio = Integer.parseInt(t.substring(0, 4));
                int mes = Integer.parseInt(t.substring(5, 7));
                int dia = Integer.parseInt(t.substring(8, 10));
                if (mes >= 1 && mes <= 12 && dia >= 1 && dia <= YearMonth.of(anio, mes).lengthOfMonth()) {
                    return LocalDate.of(anio, mes, dia);
                }
            }
            System.out.println("   Fecha no válida. Ejemplo: 2026-11-23");
        }
    }

    // Formato HH:MM, por ejemplo 10:00
    public static LocalTime leerHora(String mensaje) {
        while (true) {
            String t = leerTexto(mensaje + " (HH:MM): ");
            if (t.length() == 4) {
                t = "0" + t;   // acepta 9:00
            }
            if (t.length() == 5 && t.charAt(2) == ':'
                    && soloDigitos(t.substring(0, 2)) && soloDigitos(t.substring(3, 5))) {
                int hora = Integer.parseInt(t.substring(0, 2));
                int minuto = Integer.parseInt(t.substring(3, 5));
                if (hora <= 23 && minuto <= 59) {
                    return LocalTime.of(hora, minuto);
                }
            }
            System.out.println("   Hora no válida. Ejemplo: 10:00");
        }
    }

    public static DiaSemana leerDia(String mensaje) {
        while (true) {
            String t = leerTexto(mensaje + " (LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO): ").toUpperCase();
            for (DiaSemana d : DiaSemana.values()) {
                if (d.name().equals(t)) {
                    return d;
                }
            }
            System.out.println("   Día no válido.");
        }
    }

    // Enter = sin filtro (devuelve null)
    public static EstadoTutoria leerEstadoOpcional(String mensaje) {
        while (true) {
            String t = leerTexto(mensaje + " (PENDIENTE, CONFIRMADA, REALIZADA, CANCELADA o Enter): ").toUpperCase();
            if (t.isEmpty()) {
                return null;
            }
            for (EstadoTutoria e : EstadoTutoria.values()) {
                if (e.name().equals(t)) {
                    return e;
                }
            }
            System.out.println("   Estado no válido.");
        }
    }

    private static boolean soloDigitos(String texto) {
        if (texto.isEmpty()) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isDigit(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean esDecimal(String texto) {
        if (texto.isEmpty() || texto.length() > 9 || texto.equals(".")) {
            return false;
        }
        int puntos = 0;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '.') {
                puntos++;
            } else if (!Character.isDigit(c)) {
                return false;
            }
        }
        return puntos <= 1;
    }
}
