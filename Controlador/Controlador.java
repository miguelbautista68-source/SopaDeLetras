package org.uabc.ic.sopainterfaz.Controlador;

import org.uabc.ic.sopainterfaz.modelo.Sopa;
import org.uabc.ic.sopainterfaz.modelo.Tablero;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Controlador {

    private List<String> nombreSopa;
    private List<String> noColocadas;
    private Random rdm;
    private Tablero tablero;
    private Scanner teclado;

    public Controlador() {
        this.nombreSopa = new ArrayList<>();
        this.noColocadas = new ArrayList<>();
        this.rdm = new Random();
        this.teclado = new Scanner(System.in);

        // Palabras de la sopa
        this.nombreSopa.add("avion");
        this.nombreSopa.add("carro");
        this.nombreSopa.add("perro");
        this.nombreSopa.add("juego");
        this.nombreSopa.add("camino");
        this.nombreSopa.add("ingenieria");
        this.nombreSopa.add("ferrocarril");
        this.nombreSopa.add("computacion");
        this.nombreSopa.add("futbol");
        this.nombreSopa.add("clase");
    }

    /**
     * Bucle principal del menú administrado por el Controlador
     */
    public void ejecutar() {
        int opcion;
        do {
            System.out.println("\n=== SOPA DE LETRAS ===");
            System.out.println("1) Generar una sopa de letras y guardarla en un archivo");
            System.out.println("2) Buscar palabras en una sopa de un archivo");
            System.out.println("0) Salir");

            opcion = pedirEntero("Opción", 0, 2);

            switch (opcion) {
                case 1:
                    generar();
                    break;
                case 2:
                    jugar();
                    break;
                case 0:
                    System.out.println("¡Hasta luego!");
                    break;
            }
        } while (opcion != 0);
    }

    private void generar() {
        int renglones = 20;
        int columnas = 20;

        /* int renglones = pedirEntero("Número de renglones", 1, 30);
        int columnas = pedirEntero("Número de columnas", 1, 30); */
        String ruta;
        System.out.println("Nombre del archivo donde guardarla, puede ser sopa.txt");
        ruta = new Scanner(System.in).nextLine();

        try {
            this.generarTablero(renglones, columnas, ruta);

            System.out.println("\nSopa guardada en " + ruta);
            System.out.println(tablero.mostrar());

            System.out.println("Palabras escondidas: " + getNombreSopa());
            if (!getNoColocadas().isEmpty()) {
                System.out.println("No cupieron: " + getNoColocadas() + " (prueba con una cuadrícula más grande).");
            }
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    private void jugar() {
        String ruta = pedirTexto("Nombre del archivo con la sopa", "sopa.txt");

        try {
            this.cargarTablero(ruta);
            System.out.println("\n" + tablero.mostrar());

            while (true) {
                String palabra = pedirTexto("Palabra a buscar (o 'salir')", "salir");
                if (palabra.equalsIgnoreCase("salir")) {
                    break;
                }

                String resultado = buscarPalabra(palabra);
                System.out.println(resultado);
            }

        } catch (IOException e) {
            System.out.println("No se pudo leer la sopa: " + e.getMessage());
        }
    }

    public Tablero generarTablero(int renglones, int columnas, String ruta) throws IOException {
        tablero = new Tablero(renglones, columnas);
        noColocadas.clear();

        List<String> porLongitud = new ArrayList<>(nombreSopa);
        porLongitud.sort((x, y) -> Integer.compare(y.length(), x.length()));

        for (int i = 0; i < porLongitud.size(); i++) {
            String palabra = porLongitud.get(i);
            if (!tablero.colocarPalabra(palabra, rdm)) {
                noColocadas.add(palabra);
            }
        }

        tablero.rellenarVacios(rdm);
        tablero.guardarEnArchivo(ruta);

        return tablero;
    }

    public Tablero cargarTablero(String ruta) throws IOException {
        this.tablero = Tablero.desdeArchivo(ruta);
        return this.tablero;
    }

    public String buscarPalabra(String palabra) {
        if (tablero == null) {
            throw new IllegalStateException("Primero hay que cargar o generar un tablero.");
        }

        Sopa encontrada = tablero.buscar(palabra);
        if (encontrada == null) {
            return "La palabra \"" + palabra + "\" NO se encuentra en la cuadrícula.";
        }

        return "La palabra \"" + palabra + "\" SÍ está: empieza en el renglón "
                + encontrada.getCoordenadaA() + ", columna "
                + encontrada.getCoordenadaB() + " y su dirección es "
                + encontrada.getNombreDireccion() + " (" + encontrada.getDireccion() + ").";
    }

    private int pedirEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje + " [" + minimo + "-" + maximo + "]: ");
            try {
                int valor = Integer.parseInt(teclado.nextLine().trim());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Escribe un número entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException e) {
                System.out.println("Escribe un número válido.");
            }
        }
    }

    private String pedirTexto(String mensaje, String porDefecto) {
        System.out.print(mensaje + " [" + porDefecto + "]: ");
        String linea = teclado.nextLine().trim();
        return linea.isEmpty() ? porDefecto : linea;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public List<String> getNoColocadas() {
        return new ArrayList<>(noColocadas);
    }

    public List<String> getNombreSopa() {
        return nombreSopa;
    }

    public void setNombreSopaLetras(List<String> nombreSopa) {
        this.nombreSopa = nombreSopa;
    }
}