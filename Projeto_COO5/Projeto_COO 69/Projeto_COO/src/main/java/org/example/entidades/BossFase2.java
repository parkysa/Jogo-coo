package org.example.entidades;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.example.GameLib;

public class BossFase2 implements Boss {
    
    private double x, y;
    private double velocidadeX = 0.25; // Mais rápido
    private double velocidadeBase = 0.25;
    private boolean powerupLentidaoAtivo = false;
    private boolean movendoDireita = true;
    private int vida = 69; // Era 25
    private boolean destruido = false;
    private long proximoTiro = 0;
    private double raio = 30;
    
    public BossFase2() {
        this.x = GameLib.WIDTH / 2.0;
        this.y = 120;
        this.proximoTiro = System.currentTimeMillis() + 1000;
    }
    
    @Override
    public void atualizar(long delta, long currentTime) {
        if (destruido) return;
        
        //movimento horizontal
        if (movendoDireita) {
            x += velocidadeX * delta;
            if (x > GameLib.WIDTH - 40) {
                movendoDireita = false;
            }
        } else {
            x -= velocidadeX * delta;
            if (x < 40) {
                movendoDireita = true;
            }
        }
    }
    
    @Override
    public void desenhar() {
        if (destruido) return;
        
        try {
            //desenho do boss
            GameLib.setColor(Color.DARK_GRAY);
            GameLib.drawCircle(x, y, raio); // 30 em vez de 20
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(x, y, raio - 8); // 22 em vez de 15
            GameLib.setColor(Color.YELLOW);
            GameLib.drawCircle(x, y, raio - 18); // 12 em vez de 8

            GameLib.setColor(Color.RED);
            GameLib.drawLine(x - 40, y - 15, x + 40, y - 15);
            GameLib.drawLine(x - 40, y + 15, x + 40, y + 15);
            GameLib.drawLine(x - 15, y - 40, x - 15, y + 40);
            GameLib.drawLine(x + 15, y - 40, x + 15, y + 40);
            
            //canhões especiais
            GameLib.setColor(Color.MAGENTA);
            GameLib.drawCircle(x - 20, y + 25, 3);
            GameLib.drawCircle(x + 20, y + 25, 3);
            GameLib.drawCircle(x, y + 30, 4);
        } catch (Exception e) {
            System.err.println("Erro ao desenhar boss fase 2: " + e.getMessage());
        }
    }
    
    @Override
    public List<ProjetilInimigo> atirar(long currentTime, double playerY) {
        List<ProjetilInimigo> projeteis = new ArrayList<>();
        
        try {
            if (currentTime > proximoTiro) {
                //criar múltiplos projéteis especiais (mais frequente e mais projéteis)
                ProjetilBoss proj1 = new ProjetilBoss(x - 20, y + 30, -0.1, 0.35);
                ProjetilBoss proj2 = new ProjetilBoss(x, y + 35, 0, 0.4);
                ProjetilBoss proj3 = new ProjetilBoss(x + 20, y + 30, 0.1, 0.35);
                
                projeteis.add(proj1);
                projeteis.add(proj2);
                projeteis.add(proj3);

                proximoTiro = currentTime + 400 + (long)(Math.random() * 600);
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar projéteis do boss 2: " + e.getMessage());
        }
        
        return projeteis;
    }
    
    @Override
    public boolean colideCom(Entidade outra) {
        if (destruido || outra == null || outra.getEstado() != Estado.ATIVO) return false;
        
        try {
            double dx = x - outra.getX();
            double dy = y - outra.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            return dist < (raio + outra.getRaio()) * 0.8;
        } catch (Exception e) {
            System.err.println("Erro na verificação de colisão do boss fase 2: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void aplicarPowerupLentidao(boolean ativo) {
        if (ativo && !powerupLentidaoAtivo) {
            velocidadeX = velocidadeBase * 0.3; //reduz velocidade
            powerupLentidaoAtivo = true;
            System.out.println("Mystherio foi afetado por lentidão");
        } else if (!ativo && powerupLentidaoAtivo) {
            velocidadeX = velocidadeBase; //restaura velocidade
            powerupLentidaoAtivo = false;
            System.out.println("Mystherio recuperou velocidade normal");
        }
    }
    
    @Override
    public boolean isDestruido() {
        return destruido;
    }
    
    @Override
    public void receberDano() {
        if (!destruido) {
            vida--;
            System.out.println("Mystherio recebeu dano. Vida restante: " + vida);
            if (vida <= 0) {
                destruido = true;
                System.out.println("Você destruiu o Mystherio!");
            }
        }
    }
    
    @Override
    public int getVida() {
        return Math.max(0, vida);
    }
    
    @Override
    public double getX() {
        return x;
    }
    
    @Override
    public double getY() {
        return y;
    }
    
    @Override
    public double getRaio() {
        return raio;
    }
}
