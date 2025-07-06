package org.example.entidades;

import java.awt.Color;
import org.example.GameLib;

/**
 * Projétil especial do Boss da Fase 2 que inverte controles
 */
public class ProjetilBoss extends ProjetilInimigo {
    
    public ProjetilBoss(double x, double y, double vx, double vy) {
        super(x, y, vx, vy);
    }
    
    @Override
    public void desenhar() {
        if (getEstado() == Estado.ATIVO) {
            //desenhar interrogação maior e mais visível
            GameLib.setColor(Color.MAGENTA);
            
            //corpo principal da interrogação
            GameLib.drawCircle(getX(), getY() - 4, 4); // Círculo maior
            GameLib.drawLine(getX(), getY() - 8, getX(), getY() - 2); // Linha vertical
            GameLib.drawLine(getX() - 2, getY() + 2, getX() + 2, getY() + 2); // Ponto
            
            //contorno para destaque
            GameLib.setColor(Color.WHITE);
            GameLib.drawCircle(getX(), getY() - 4, 5);
            
            //efeito brilhante maior
            GameLib.setColor(new Color(255, 0, 255, 80));
            GameLib.drawCircle(getX(), getY(), getRaio() + 3);
        }
    }
    
    //metodo para identificar que este é um projétil especial
    public boolean inverteControles() {
        return true;
    }
}
