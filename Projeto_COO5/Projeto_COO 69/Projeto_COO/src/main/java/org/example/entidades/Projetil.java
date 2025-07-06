package org.example.entidades;

import java.awt.Color;
import org.example.GameLib;

public class Projetil extends Entidade {

    private double velocidadeX;
    private double velocidadeY;
    private double velocidadeBase;
    private boolean powerupAtivo = false;

    public Projetil(double x, double y, double vx, double vy) {
        super(x, y, 0);
        this.velocidadeX = vx;
        this.velocidadeY = vy;
        this.velocidadeBase = Math.abs(vy); //guarda velocidade base
    }

    @Override
    public void atualizar(long delta, long currentTime) {
        if (getEstado() == Estado.ATIVO) {
            setX(getX() + velocidadeX * delta);
            setY(getY() + velocidadeY * delta);

            if (getY() < 0) {
                setEstado(Estado.INATIVO);
            }
        }
    }

    public void aplicarPowerupVelocidade(boolean ativo) {
        if (ativo && !powerupAtivo) {
            velocidadeY *= 2.0;
            powerupAtivo = true;
        } else if (!ativo && powerupAtivo) {
            velocidadeY = -velocidadeBase;
            powerupAtivo = false;
        }
    }

    @Override
    public void desenhar() {
        if (getEstado() == Estado.ATIVO){
            if (powerupAtivo) {
                GameLib.setColor(Color.CYAN);
            } else {
                GameLib.setColor(Color.GREEN);
            }
            GameLib.drawLine(getX(), getY() - 5, getX(), getY() + 5);
            GameLib.drawLine(getX() - 1, getY() - 3, getX() - 1, getY() + 3);
            GameLib.drawLine(getX() + 1, getY() - 3, getX() + 1, getY() + 3);
        }
    }
}
