package org.uabc.ic.sopainterfaz.modelo;

/**
 * Una casilla de la sopa: la letra, su posición (renglón = coordenadaA, columna = coordenadaB,
 * los dos contando desde 1) y la dirección en la que empieza una palabra.
 * Direcciones, igual que la figura de la práctica:
 * 1 →, 2 ↓, 3 ←, 4 ↑, 5 ↘, 6 ↗, 7 ↙, 8 ↖ (0 = ninguna).
 */
public class Sopa {
    private static final String[] FLECHAS = {"", "→", "↓", "←", "↑", "↘", "↗", "↙", "↖"};
    private int coordenadaA;
    private int coordenadaB;
    private char letra;
    private int direccion;

    public Sopa(){
        this.letra = 'j';
        this.coordenadaA = 3;
        this.coordenadaB = 6;
        this.direccion = 7;
    }

    public Sopa(int coordenadaA, int coordenadaB) {
        this.coordenadaA = coordenadaA;
        this.coordenadaB = coordenadaB;
        this.letra = ' ';   // casilla vacía, todavía sin letra
        this.direccion = 0;
    }

    public Sopa(char letra, int coordenadaA, int coordenadaB, int direccion){
        this.letra = letra;
        this.coordenadaA = coordenadaA;
        this.coordenadaB = coordenadaB;
        this.direccion = direccion;
    }

    public char getLetra(){
        return letra;
    }

    public void setLetra(char letra) {
        this.letra = letra;
    }

    public int getCoordenadaA() {
        return coordenadaA;
    }

    public void setCoordenadaA(int coordenadaA) {
        this.coordenadaA = coordenadaA;
    }

    public int getCoordenadaB() {
        return coordenadaB;
    }

    public void setCoordenadaB(int coordenadaB) {
        this.coordenadaB = coordenadaB;
    }

    public int getDireccion() {
        return direccion;
    }

    public void setDireccion(int direccion) {
        this.direccion = direccion;
    }

    public String getFlecha() {
        if (direccion < 0 || direccion >= FLECHAS.length) {
            return "";
        }
        return FLECHAS[direccion];
    }

    public String toString(){
        switch(direccion){
            case 1:
                return "\u27A1\uFE0F";
            case 2:
                return "\u2B07\uFE0F";
            case 3:
                return "\u2B05\uFE0F";
            case 4:
                return "\u2B06\uFE0F";
            case 5:
                return "\u2198\uFE0F";
            case 6:
                return "\u2197\uFE0F";
            case 7:
                return "\u2199\uFE0F";
            case 8:
                return "\u2196\uFE0F";
        }
        return "La letra " + letra + " con la coordenada [" + coordenadaA + "," + coordenadaB + "] y sin direccion (" + direccion + ")";
    }

}
