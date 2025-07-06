package org.example.entidades;

import org.example.GameLib;
import java.awt.Color;

public class InimigoTipo1 extends Inimigo {

    private double velocidade;
    private double angulo;
    private double velocidadeRotacao;
    private long proximoTiro;
    private double velocidadeBase;
    private boolean powerupLentidaoAtivo = false;

    public InimigoTipo1(double x, double y) {
        super(x, y, 9.0);
        this.velocidade = 0.20 + Math.random() * 0.15;
        this.velocidadeBase = this.velocidade;
        this.angulo = (3 * Math.PI) / 2;
        this.velocidadeRotacao = 0.0;
        this.proximoTiro = System.currentTimeMillis() + 500;
    }

    @Override
    public void atualizar(long delta, long currentTime) {
        super.atualizar(delta, currentTime); //chama a lógica de explosão da classe pai

        if (getEstado() == Estado.ATIVO) {
            setX(getX() + velocidade * Math.cos(angulo) * delta);
            setY(getY() + velocidade * Math.sin(angulo) * delta * (-1.0));
            this.angulo += velocidadeRotacao * delta;

            if (getY() > GameLib.HEIGHT + 10) {
                setEstado(Estado.INATIVO);
            }
        }
    }

    @Override
    public ProjetilInimigo atirar(long currentTime, double playerY) {
        if (getEstado() == Estado.ATIVO && currentTime > proximoTiro && getY() < playerY) {
            this.proximoTiro = (long) (currentTime + 200 + Math.random() * 500);
            double vx = Math.cos(angulo) * 0.45;
            double vy = Math.sin(angulo) * 0.45 * (-1.0);
            return new ProjetilInimigo(getX(), getY(), vx, vy);
        }
        return null;
    }

    @Override
    public void desenharAtivo() {
        GameLib.setColor(Color.CYAN);
        GameLib.drawCircle(getX(), getY(), getRaio());
    }

    public void aplicarPowerupLentidao(boolean ativo) {
        if (ativo && !powerupLentidaoAtivo) {
            velocidade = velocidadeBase * 0.5;
            powerupLentidaoAtivo = true;
        } else if (!ativo && powerupLentidaoAtivo) {
            velocidade = velocidadeBase; //restaura velocidade original
            powerupLentidaoAtivo = false;
        }
    }
}
