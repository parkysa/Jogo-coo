package org.example.entidades;

import java.awt.Color;
import org.example.GameLib;

public class ProjetilInterrogacao extends Entidade {
    
    private double velocidadeX;
    private double velocidadeY;
    
    public ProjetilInterrogacao(double x, double y, double vx, double vy) {
        super(x, y, 3.0);
        this.velocidadeX = vx;
        this.velocidadeY = vy;
    }
    
    @Override
    public void atualizar(long delta, long currentTime) {
        if (getEstado() == Estado.ATIVO) {
            setX(getX() + velocidadeX * delta);
            setY(getY() + velocidadeY * delta);
            
            if (getY() > GameLib.HEIGHT || getX() < 0 || getX() > GameLib.WIDTH) {
                setEstado(Estado.INATIVO);
            }
        }
    }
    
    @Override
    public void desenhar() {
        if (getEstado() == Estado.ATIVO) {
            GameLib.setColor(Color.MAGENTA);
            //desenha um ?
            GameLib.drawCircle(getX(), getY() - 2, 2);
            GameLib.drawLine(getX(), getY() - 4, getX(), getY() - 1);
            GameLib.drawLine(getX() - 1, getY() + 1, getX() + 1, getY() + 1);
        }
    }
}
