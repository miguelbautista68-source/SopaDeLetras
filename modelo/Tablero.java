
package org.uabc.ic.sopainterfaz.modelo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Tablero {

    private int coordenadaA; // Filas
    private int coordenadaB; // Columnas
    private Sopa[][] sopa;

    // Ocho direcciones posibles de búsqueda y colocación.
    // 1 derecha, 2 abajo, 3 izquierda, 4 arriba,
    // 5 diagonal abajo-derecha, 6 arriba-derecha,
    // 7 abajo-izquierda, 8 arriba-izquierda.
    private static final int[][] DIRECCIONES = {
            {0, 1},
            {1, 0},
            {0, -1},
            {-1, 0},
            {1, 1},
            {-1, 1},
            {1, -1},
            {-1, -1}
    };

    // Constructor
    public Tablero(int filas, int columnas) {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException(
                    "Las filas y columnas deben ser mayores que cero."
            );
        }

        this.coordenadaA = filas;
        this.coordenadaB = columnas;
        this.sopa = new Sopa[filas][columnas];

        // Inicializar todas las casillas vacías.
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                sopa[i][j] = new Sopa(' ', i + 1, j + 1, 0);
            }
        }
    }

    // Obtener número de filas
    public int getCoordenadaA() {
        return coordenadaA;
    }

    // Obtener número de columnas
    public int getCoordenadaB() {
        return coordenadaB;
    }

    // Obtener una casilla mediante índices desde cero
    public Sopa getSopa(int i, int j) {
        return sopa[i][j];
    }

    // Modificar una casilla mediante índices desde cero
    public void setSopa(int i, int j, Sopa s) {
        if (i < 0 || i >= coordenadaA ||
                j < 0 || j >= coordenadaB) {
            throw new IndexOutOfBoundsException(
                    "La posición está fuera del tablero."
            );
        }

        if (s == null) {
            sopa[i][j] = new Sopa(' ', i + 1, j + 1, 0);
        } else {
            sopa[i][j] = s;
        }
    }

    /**
     * Coloca una palabra en una posición aleatoria.
     * Si no encuentra lugar, recorre el tablero completo.
     */
    public boolean colocarPalabra(String palabra, Random random) {
        if (palabra == null || random == null) {
            throw new IllegalArgumentException(
                    "La palabra y Random no pueden ser null."
            );
        }

        String texto = normalizarPalabra(palabra);

        if (texto.isEmpty()) {
            return false;
        }

        if (texto.length() > Math.max(coordenadaA, coordenadaB)) {
            return false;
        }

        // Primero se intenta colocar en posiciones aleatorias.
        for (int intento = 0; intento < 500; intento++) {
            int fila = random.nextInt(coordenadaA);
            int columna = random.nextInt(coordenadaB);
            int direccion = random.nextInt(8) + 1;

            if (puedeColocar(texto, fila, columna, direccion)) {
                escribirPalabra(texto, fila, columna, direccion);
                return true;
            }
        }

        // Si falla, se revisan todas las posiciones y direcciones.
        for (int direccion = 1; direccion <= 8; direccion++) {
            for (int fila = 0; fila < coordenadaA; fila++) {
                for (int columna = 0; columna < coordenadaB; columna++) {
                    if (puedeColocar(
                            texto, fila, columna, direccion
                    )) {
                        escribirPalabra(
                                texto, fila, columna, direccion
                        );
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Comprueba que la palabra quepa y que no sobrescriba
     * letras diferentes.
     */
    private boolean puedeColocar(
            String palabra, int fila, int columna, int direccion
    ) {
        int df = DIRECCIONES[direccion - 1][0];
        int dc = DIRECCIONES[direccion - 1][1];

        for (int k = 0; k < palabra.length(); k++) {
            int f = fila + k * df;
            int c = columna + k * dc;

            if (f < 0 || f >= coordenadaA ||
                    c < 0 || c >= coordenadaB) {
                return false;
            }

            char actual = sopa[f][c].getLetra();

            if (actual != ' ' &&
                    Character.toLowerCase(actual) != palabra.charAt(k)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Escribe una palabra en el tablero.
     * La dirección se almacena en la primera casilla.
     */
    private void escribirPalabra(
            String palabra, int fila, int columna, int direccion
    ) {
        int df = DIRECCIONES[direccion - 1][0];
        int dc = DIRECCIONES[direccion - 1][1];

        for (int k = 0; k < palabra.length(); k++) {
            int f = fila + k * df;
            int c = columna + k * dc;

            sopa[f][c].setLetra(palabra.charAt(k));
            sopa[f][c].setCoordenadaA(f + 1);
            sopa[f][c].setCoordenadaB(c + 1);

            if (k == 0) {
                sopa[f][c].setDireccion(direccion);
            } else {
                sopa[f][c].setDireccion(0);
            }
        }
    }

    /**
     * Rellena las casillas vacías con letras aleatorias.
     */
    public void rellenarVacios(Random random) {
        if (random == null) {
            throw new IllegalArgumentException(
                    "Random no puede ser null."
            );
        }

        for (int i = 0; i < coordenadaA; i++) {
            for (int j = 0; j < coordenadaB; j++) {
                if (sopa[i][j] == null) {
                    sopa[i][j] = new Sopa(' ', i + 1, j + 1, 0);
                }

                if (sopa[i][j].getLetra() == ' ') {
                    char letra = (char) ('a' + random.nextInt(26));
                    sopa[i][j].setLetra(letra);
                }
            }
        }
    }

    /**
     * Busca una palabra en las ocho direcciones.
     * Devuelve una Sopa con la coordenada inicial y dirección.
     * Devuelve null si la palabra no se encuentra.
     */
    public Sopa buscar(String palabra) {
        if (palabra == null) {
            return null;
        }

        String texto = normalizarPalabra(palabra);

        if (texto.isEmpty()) {
            return null;
        }

        for (int i = 0; i < coordenadaA; i++) {
            for (int j = 0; j < coordenadaB; j++) {
                for (int direccion = 1; direccion <= 8; direccion++) {
                    if (coincide(texto, i, j, direccion)) {
                        return new Sopa(
                                sopa[i][j].getLetra(),
                                i + 1,
                                j + 1,
                                direccion
                        );
                    }
                }
            }
        }

        return null;
    }

    /**
     * Comprueba si la palabra coincide a partir de una posición.
     */
    private boolean coincide(
            String palabra, int fila, int columna, int direccion
    ) {
        int df = DIRECCIONES[direccion - 1][0];
        int dc = DIRECCIONES[direccion - 1][1];

        for (int k = 0; k < palabra.length(); k++) {
            int f = fila + k * df;
            int c = columna + k * dc;

            if (f < 0 || f >= coordenadaA ||
                    c < 0 || c >= coordenadaB) {
                return false;
            }

            if (sopa[f][c] == null) {
                return false;
            }

            char letra = Character.toLowerCase(
                    sopa[f][c].getLetra()
            );

            if (letra != palabra.charAt(k)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Muestra el tablero como texto.
     * Es String porque Controlador utiliza tablero.mostrar()
     * dentro de System.out.println().
     */
    public String mostrar() {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < coordenadaA; i++) {
            for (int j = 0; j < coordenadaB; j++) {
                char letra = sopa[i][j] == null
                        ? ' '
                        : sopa[i][j].getLetra();

                if (letra == ' ') {
                    resultado.append(". ");
                } else {
                    resultado.append(
                            Character.toUpperCase(letra)
                    ).append(' ');
                }
            }

            if (i < coordenadaA - 1) {
                resultado.append(System.lineSeparator());
            }
        }

        return resultado.toString();
    }

    /**
     * Guarda el tablero en un archivo de texto.
     * La primera línea contiene filas y columnas.
     */
    public void guardarEnArchivo(String ruta) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La ruta del archivo no puede estar vacía."
            );
        }

        Path archivo = Paths.get(ruta.trim());
        Path padre = archivo.toAbsolutePath().getParent();

        if (padre != null) {
            Files.createDirectories(padre);
        }

        StringBuilder contenido = new StringBuilder();

        contenido.append(coordenadaA)
                .append(' ')
                .append(coordenadaB)
                .append(System.lineSeparator());

        for (int i = 0; i < coordenadaA; i++) {
            for (int j = 0; j < coordenadaB; j++) {
                char letra = sopa[i][j] == null
                        ? ' '
                        : sopa[i][j].getLetra();

                contenido.append(
                        letra == ' '
                                ? '.'
                                : Character.toLowerCase(letra)
                );
            }

            if (i < coordenadaA - 1) {
                contenido.append(System.lineSeparator());
            }
        }

        Files.write(
                archivo,
                contenido.toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Carga un tablero desde un archivo.
     * Admite el formato generado por guardarEnArchivo().
     */
    public static Tablero desdeArchivo(String ruta) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La ruta del archivo no puede estar vacía."
            );
        }

        Path archivo = Paths.get(ruta.trim());

        List<String> lineas = Files.readAllLines(
                archivo,
                StandardCharsets.UTF_8
        );

        List<String> utiles = new ArrayList<>();

        for (String linea : lineas) {
            if (linea != null && !linea.trim().isEmpty()) {
                utiles.add(linea.trim());
            }
        }

        if (utiles.isEmpty()) {
            throw new IOException("El archivo está vacío.");
        }

        int filas;
        int columnas;
        int inicioDatos;

        String[] dimensiones = utiles.get(0).split("\\s+");

        if (dimensiones.length == 2 &&
                esEntero(dimensiones[0]) &&
                esEntero(dimensiones[1])) {

            filas = Integer.parseInt(dimensiones[0]);
            columnas = Integer.parseInt(dimensiones[1]);
            inicioDatos = 1;

            if (filas <= 0 || columnas <= 0) {
                throw new IOException(
                        "Las dimensiones del archivo no son válidas."
                );
            }

        } else {
            // También admite archivos sin encabezado de dimensiones.
            inicioDatos = 0;
            filas = utiles.size();
            columnas = limpiarFila(utiles.get(0)).length();
        }

        if (filas <= 0 || columnas <= 0 ||
                utiles.size() - inicioDatos != filas) {
            throw new IOException(
                    "El número de filas no coincide con las dimensiones."
            );
        }

        Tablero tablero = new Tablero(filas, columnas);

        for (int i = 0; i < filas; i++) {
            String fila = limpiarFila(utiles.get(i + inicioDatos));

            if (fila.length() != columnas) {
                throw new IOException(
                        "La fila " + (i + 1)
                                + " debe tener exactamente "
                                + columnas + " caracteres."
                );
            }

            for (int j = 0; j < columnas; j++) {
                char letra = fila.charAt(j);

                if (letra == '.') {
                    letra = ' ';
                }

                tablero.sopa[i][j] = new Sopa(
                        letra,
                        i + 1,
                        j + 1,
                        0
                );
            }
        }

        return tablero;
    }

    /**
     * Normaliza una palabra eliminando espacios externos
     * e internos y convirtiéndola a minúsculas.
     */
    private static String normalizarPalabra(String palabra) {
        return palabra.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");
    }

    /**
     * Limpia una línea del archivo.
     */
    private static String limpiarFila(String fila) {
        return fila.replaceAll("\\s+", "")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Comprueba si una cadena es un número entero.
     */
    private static boolean esEntero(String texto) {
        try {
            Integer.parseInt(texto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
