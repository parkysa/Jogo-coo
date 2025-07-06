package org.example.entidades;

import org.example.GameLib;
import java.awt.Color;

public class PowerupVelocidade extends Entidade implements Powerup {
    
    private long duracao = 3000; // REDUZIDO: 3 segundos (era 5)
    private long inicioAtivacao;
    private boolean ativo = false;
    private boolean aplicado = false;
    
    public PowerupVelocidade(double x, double y) {
        super(x, y, 8.0);
    }
    
    @Override
    public void atualizar(long delta, long currentTime) {
        // Implementação do método da interface Entidade
        atualizarMovimento(delta, currentTime);
        atualizar(currentTime);
    }
    
    @Override
    public void atualizar(long currentTime) {
        // Implementação do método da interface Powerup
        if (ativo && currentTime - inicioAtivacao > duracao) {
            ativo = false;
            System.out.println("O Power-up de velocidade acabou");
        }
    }
    
    @Override
    public void atualizarMovimento(long delta, long currentTime) {
        if (getEstado() == Estado.ATIVO) {
            setY(getY() + 0.1 * delta);
            if (getY() > GameLib.HEIGHT + 10) {
                setEstado(Estado.INATIVO);
            }
        }
    }
    
    @Override
    public void desenhar() {
        if (getEstado() == Estado.ATIVO) {
            GameLib.setColor(Color.YELLOW);
            GameLib.drawCircle(getX(), getY(), getRaio());
            GameLib.setColor(Color.ORANGE);
            GameLib.drawCircle(getX(), getY(), getRaio() - 2);
        }
    }
    
    @Override
    public void aplicar(Player player) {
        if (!aplicado) {
            player.setPowerupVelocidade(true);
            aplicado = true;
            System.out.println("Power-up de velocidade ativado por " + (duracao/1000) + " segundos!");
        }
    }
    
    @Override
    public void remover(Player player) {
        if (aplicado) {
            player.setPowerupVelocidade(false);
            aplicado = false;
        }
    }
    
    @Override
    public long getDuracao() {
        return duracao;
    }
    
    @Override
    public boolean isAtivo() {
        return ativo;
    }
    
    @Override
    public void ativar(long currentTime) {
        this.inicioAtivacao = currentTime;
        this.ativo = true;
    }
}
