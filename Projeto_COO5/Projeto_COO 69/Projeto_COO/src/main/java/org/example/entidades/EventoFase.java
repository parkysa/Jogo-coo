package org.example.entidades;


public class EventoFase {
    public enum Tipo { INIMIGO, POWERUP, CHEFE }

    public Tipo tipo;
    public int subtipo;
    public int vidaChefe; // só usado se for CHEFE
    public long momento;
    public double x, y;

    public EventoFase(Tipo tipo, int subtipo, int vidaChefe, long momento, double x, double y) {
        this.tipo = tipo;
        this.subtipo = subtipo;
        this.vidaChefe = vidaChefe;
        this.momento = momento;
        this.x = x;
        this.y = y;
    }

    // Construtor alternativo para INIMIGO e POWERUP
    public EventoFase(Tipo tipo, int subtipo, long momento, double x, double y) {
        this(tipo, subtipo, 0, momento, x, y);
    }
}
