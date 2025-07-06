package org.example.entidades;

import org.example.GameLib;

public abstract class Inimigo extends Entidade {

    private long inicioExplosao;
    private long fimExplosao;

    public Inimigo(double x, double y, double raio) {
        super(x, y, raio);
    }

    public void explodir(long currentTime) {
        if (getEstado() == Estado.ATIVO) {
            setEstado(Estado.EXPLODINDO);
            this.inicioExplosao = currentTime;
            this.fimExplosao = currentTime + 500;
        }
    }

    //explosão e remoção
    @Override
    public void atualizar(long delta, long currentTime) {
        if (getEstado() == Estado.EXPLODINDO) {
            if (currentTime > this.fimExplosao) {
                setEstado(Estado.INATIVO);
            }
        }
    }

    @Override
    public void desenhar() {
        if (getEstado() == Estado.EXPLODINDO) {
            double alpha = (System.currentTimeMillis() - inicioExplosao) / (double) (fimExplosao - inicioExplosao);
            GameLib.drawExplosion(getX(), getY(), alpha);
        } else if (getEstado() == Estado.ATIVO) {
            desenharAtivo();
        }
    }

    //metodo abstrato para o desenho específico de cada inimigo
    public abstract void desenharAtivo();

    public ProjetilInimigo atirar(long currentTime, double playerY) {
        return null;
    }
}
