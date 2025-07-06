package org.example.entidades;

import java.awt.Color;


/**
 * Classe abstrata que representa uma entidade base no jogo.
 * Contém atributos e comportamentos comuns a todos os elementos
 * (Player, Inimigos, Projéteis).
 */
public abstract class Entidade {

    private double x;
    private double y;
    private double raio;
    private Estado estado;

    public Entidade(double x, double y, double raio) {
        this.x = x;
        this.y = y;
        this.raio = raio;
        this.estado = Estado.ATIVO;
    }

    //métodos abstratos que devem ser implementados por todas as subclasses
    public abstract void atualizar(long delta, long currentTime);
    public abstract void desenhar();

    //getters e setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getRaio() { return raio; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    //metodo para verificar colisão
    public boolean colideCom(Entidade outra) {
        if (this.getEstado() != Estado.ATIVO || outra.getEstado() != Estado.ATIVO) {
            return false;
        }
        double dx = this.x - outra.x;
        double dy = this.y - outra.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (this.raio + outra.raio) * 0.8;
    }
}
