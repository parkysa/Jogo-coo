package org.example.entidades;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.example.GameLib;

public class InimigoTipo2 extends Inimigo {

    private double velocidade;
    private double angulo;
    private double velocidadeRotacao;
    private boolean atirou;
    private double velocidadeBase;
    private boolean powerupLentidaoAtivo = false;

    public InimigoTipo2(double x, double y) {
        super(x, y, 12.0);
        this.velocidade = 0.42;
        this.velocidadeBase = this.velocidade;
        this.angulo = (3 * Math.PI) / 2;
        this.velocidadeRotacao = 0.0;
        this.atirou = false;
    }

    @Override
    public void atualizar(long delta, long currentTime) {
        super.atualizar(delta, currentTime);

        if (getEstado() == Estado.ATIVO) {
            double previousY = getY();
            setX(getX() + velocidade * Math.cos(angulo) * delta);
            setY(getY() + velocidade * Math.sin(angulo) * delta * (-1.0));
            this.angulo += velocidadeRotacao * delta;

            if (getX() < -10 || getX() > GameLib.WIDTH + 10) {
                setEstado(Estado.INATIVO);
            }

            double threshold = GameLib.HEIGHT * 0.30;
            if (previousY < threshold && getY() >= threshold) {
                if (getX() < GameLib.WIDTH / 2) this.velocidadeRotacao = 0.003;
                else this.velocidadeRotacao = -0.003;
            }

            if (velocidadeRotacao > 0 && Math.abs(angulo - 3 * Math.PI) < 0.05) {
                this.velocidadeRotacao = 0.0;
                this.angulo = 3 * Math.PI;
                this.atirou = true;
            }

            if (velocidadeRotacao < 0 && Math.abs(angulo) < 0.05) {
                this.velocidadeRotacao = 0.0;
                this.angulo = 0.0;
                this.atirou = true;
            }
        }
    }

    public List<ProjetilInimigo> atirarEspecial() {
        if (atirou) {
            this.atirou = false; //atira apenas uma vez
            List<ProjetilInimigo> projeteis = new ArrayList<>();
            double[] angulos = { Math.PI / 2 + Math.PI / 8, Math.PI / 2, Math.PI / 2 - Math.PI / 8 };

            for(double anguloBase : angulos) {
                double a = anguloBase + Math.random() * Math.PI / 6 - Math.PI / 12;
                double vx = Math.cos(a) * 0.30;
                double vy = Math.sin(a) * 0.30;
                projeteis.add(new ProjetilInimigo(getX(), getY(), vx, vy));
            }
            return projeteis;
        }
        return null;
    }

    @Override
    public void desenharAtivo() {
        GameLib.setColor(Color.MAGENTA);
        GameLib.drawDiamond(getX(), getY(), getRaio());
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
