package org.example.entidades;

import java.awt.Color;
import org.example.GameLib;

public class ProjetilInimigo extends Entidade {

    private double velocidadeX;
    private double velocidadeY;
    private double velocidadeBaseX, velocidadeBaseY;
    private boolean powerupLentidaoAtivo = false;

    public ProjetilInimigo(double x, double y, double vx, double vy) {
        super(x, y, 2.0);
        this.velocidadeX = vx;
        this.velocidadeY = vy;
        this.velocidadeBaseX = vx;
        this.velocidadeBaseY = vy;
    }

    @Override
    public void atualizar(long delta, long currentTime) {
        if (getEstado() == Estado.ATIVO) {
            setX(getX() + velocidadeX * delta);
            setY(getY() + velocidadeY * delta);

            if (getY() > GameLib.HEIGHT) {
                setEstado(Estado.INATIVO);
            }
        }
    }

    @Override
    public void desenhar() {
        if(getEstado() == Estado.ATIVO){
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(getX(), getY(), getRaio());
        }
    }

    public void aplicarPowerupLentidao(boolean ativo) {
        if (ativo && !powerupLentidaoAtivo) {
            velocidadeX = velocidadeBaseX * 0.5; //reduzir velocidade pela metade
            velocidadeY = velocidadeBaseY * 0.5;
            powerupLentidaoAtivo = true;
        } else if (!ativo && powerupLentidaoAtivo) {
            velocidadeY = velocidadeBaseY;
            powerupLentidaoAtivo = false;
        }
    }
}
