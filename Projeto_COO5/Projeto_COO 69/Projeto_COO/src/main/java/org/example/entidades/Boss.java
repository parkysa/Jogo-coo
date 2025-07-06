package org.example.entidades;

import java.util.List;

//interface para bosses do jogo
public interface Boss {
    void atualizar(long delta, long currentTime);
    void desenhar();
    boolean isDestruido();
    void receberDano();
    int getVida();
    List<ProjetilInimigo> atirar(long currentTime, double playerY);
    double getX();
    double getY();
    double getRaio();
    boolean colideCom(Entidade outra);
    
    //métodos para power-ups
    void aplicarPowerupLentidao(boolean ativo);
}
