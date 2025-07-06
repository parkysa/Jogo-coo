package org.example.entidades;

/**
 * Interface para powerups do jogo.
 */
public interface Powerup {
    void aplicar(Player player);
    void remover(Player player);
    long getDuracao();
    boolean isAtivo();
    void ativar(long currentTime);
    void atualizar(long currentTime);
    void atualizarMovimento(long delta, long currentTime);
    void desenhar();
    Estado getEstado();
    void setEstado(Estado estado);
    double getX();
    double getY();
    double getRaio();
}
