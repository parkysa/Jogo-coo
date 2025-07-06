package org.example.entidades;

import org.example.GameLib;
import java.awt.Color;

public class PowerupLentidao extends Entidade implements Powerup {
    
    private long duracao = 4000; // REDUZIDO: 4 segundos (era 6)
    private long inicioAtivacao;
    private boolean ativo = false;
    private boolean aplicado = false;
    
    public PowerupLentidao(double x, double y) {
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
            System.out.println("Power-up de lentidão acabou");
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
            GameLib.setColor(Color.CYAN);
            GameLib.drawDiamond(getX(), getY(), getRaio());
            GameLib.setColor(Color.BLUE);
            GameLib.drawDiamond(getX(), getY(), getRaio() - 2);
        }
    }
    
    @Override
    public void aplicar(Player player) {
        if (!aplicado) {
            player.setPowerupLentidao(true);
            aplicado = true;
            System.out.println("Power-up de lentidão ativado por " + (duracao/1000) + " segundos");
        }
    }
    
    @Override
    public void remover(Player player) {
        if (aplicado) {
            player.setPowerupLentidao(false);
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
