package org.example.entidades;

public class Spawn{
    String spawn;
    int tipo;
    int vida;
    long tempo;
    double x;
    double y;

    public Spawn(String spawn, int tipo, int vida, long tempo, double x, double y){
        this.spawn = spawn;
        this.tipo = tipo;
        this.tempo = tempo;
        this.vida = vida;
        this.x = x;
        this.y = y;
    }

    public String getSpawn(){
        return this.spawn;
    }

    public int getTipo(){
        return this.tipo;
    }

    public int getVida(){
        return this.vida;
    }

    public long getTempo(){
        return this.tempo;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }
}