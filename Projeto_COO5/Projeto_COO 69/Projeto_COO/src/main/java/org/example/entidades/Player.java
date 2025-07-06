package org.example.entidades;

import java.awt.Color;
import org.example.GameLib;

public class Player extends Entidade {

    private double velocidadeX = 0.25;
    private double velocidadeY = 0.25;
    private long proximoTiro;
    private long inicioExplosao;
    private long fimExplosao;
    private boolean powerupVelocidade = false;
    private boolean powerupLentidao = false;
    private boolean controlesInvertidos = false;
    private long fimInversao = 0;
    private long inicioInversao = 0;
    private int vida;

    public Player(int vida) {
        super(GameLib.WIDTH / 2.0, GameLib.HEIGHT * 0.90, 12.0);
        this.proximoTiro = 0;
        this.vida = vida;
    }

    public int getVida(){
        return this.vida;
    }

    @Override
    public void atualizar(long delta, long currentTime) {
        if (getEstado() == Estado.EXPLODINDO) {
            if (currentTime > this.fimExplosao) {
                setEstado(Estado.ATIVO);
            }
        }

        // Verificar se a inversão de controles deve terminar
        if (controlesInvertidos && currentTime > fimInversao) {
            controlesInvertidos = false;
            System.out.println("Controles voltaram ao normal!");
        }

        if (getEstado() == Estado.ATIVO) {
            // Movimentação com controles possivelmente invertidos
            boolean up = GameLib.iskeyPressed(GameLib.KEY_UP);
            boolean down = GameLib.iskeyPressed(GameLib.KEY_DOWN);
            boolean left = GameLib.iskeyPressed(GameLib.KEY_LEFT);
            boolean right = GameLib.iskeyPressed(GameLib.KEY_RIGHT);

            if (controlesInvertidos) {
                // Inverter apenas esquerda e direita
                boolean temp = left;
                left = right;
                right = temp;
            }

            if (up) setY(getY() - velocidadeY * delta);
            if (down) setY(getY() + velocidadeY * delta);
            if (left) setX(getX() - velocidadeX * delta);
            if (right) setX(getX() + velocidadeX * delta);

            // Mantém o jogador dentro da tela
            if (getX() < 0.0) setX(0.0);
            if (getX() >= GameLib.WIDTH) setX(GameLib.WIDTH - 1);
            if (getY() < 25.0) setY(25.0);
            if (getY() >= GameLib.HEIGHT) setY(GameLib.HEIGHT - 1);
        }
    }

    public Projetil atirar(long currentTime){
        if (GameLib.iskeyPressed(GameLib.KEY_CONTROL) && currentTime > proximoTiro) {
            this.proximoTiro = currentTime + 100; // Cooldown de 100ms
            return new Projetil(getX(), getY() - 2 * getRaio(), 0.0, -1.0);
        }
        return null;
    }

    public void explodir(long currentTime) {
        if (getEstado() == Estado.ATIVO) {
            setEstado(Estado.EXPLODINDO);
            this.inicioExplosao = currentTime;
            this.fimExplosao = currentTime + 2000;
            this.vida = vida - 1;
        }
    }

    @Override
    public void desenhar() {
        if (getEstado() == Estado.EXPLODINDO) {
            double alpha = (System.currentTimeMillis() - inicioExplosao) / (double) (fimExplosao - inicioExplosao);
            GameLib.drawExplosion(getX(), getY(), alpha);
        } else {
            GameLib.setColor(Color.BLUE);
            GameLib.drawPlayer(getX(), getY(), getRaio());
        }
    }

    public void setPowerupVelocidade(boolean ativo) {
        this.powerupVelocidade = ativo;
    }

    public boolean hasPowerupVelocidade() {
        return powerupVelocidade;
    }

    public void setPowerupLentidao(boolean ativo) {
        this.powerupLentidao = ativo;
    }

    public boolean hasPowerupLentidao() {
        return powerupLentidao;
    }

    public void inverterControles(long currentTime, long duracao) {
        this.controlesInvertidos = true;
        this.inicioInversao = currentTime;
        this.fimInversao = currentTime + duracao;
    }

    public boolean temControlesInvertidos() {
        return controlesInvertidos;
    }
    
    public long getInicioInversao() {
        return inicioInversao;
    }
    
    public long getFimInversao() {
        return fimInversao;
    }
}
