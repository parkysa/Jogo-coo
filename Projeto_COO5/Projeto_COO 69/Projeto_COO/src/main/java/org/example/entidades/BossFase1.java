package org.example.entidades;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.example.GameLib;

//boss da fase 1 (duas naves)
public class BossFase1 implements Boss {
    
    //posições das duas naves
    private double nave1X, nave1Y;
    private double nave2X, nave2Y;
    
    //movimento
    private double velocidade = 0.1;
    private double velocidadeBase = 0.1;
    private boolean powerupLentidaoAtivo = false;
    private boolean nave1MovendoDireita = true;
    private boolean nave2MovendoDireita = false; // Movimento alternado
    
    //sistema de vida
    private int vida; // Era 30
    private boolean destruido = false;
    
    //sistema de tiro pausado
    private boolean periodoTiro = false; // true = atirando, false = pausando
    private long inicioFase = 0;
    private long duracaoTiro = 4000; // 4 segundos atirando
    private long duracaoPausa = 3000; // 3 segundos de pausa
    private long ultimoTiro = 0;
    private long intervaloTiro = 400; // Intervalo entre tiros durante período ativo
    
    //tamanho aumentado
    private double raio = 25; // Era 18
    
    public BossFase1(int vida) {
        //posicionar as naves em cima
        this.nave1X = 80; //mais afastado da borda
        this.nave1Y = 100; //mais baixo
        this.nave2X = GameLib.WIDTH - 80;
        this.nave2Y = 100;
        this.vida = vida;
        
        //inicializar sistema de tiro
        this.inicioFase = System.currentTimeMillis();
        this.ultimoTiro = System.currentTimeMillis();
        this.periodoTiro = true; // Começar atirando
    }
    
    @Override
    public void atualizar(long delta, long currentTime) {
        if (destruido) return;
        
        //movimento sincronizado das naves
        atualizarMovimento(delta);
        
        //atualizar sistema de tiro pausado
        atualizarSistemaTiro(currentTime);
    }
    
    private void atualizarMovimento(long delta) {
        //nave 1 - movimento horizontal
        if (nave1MovendoDireita) {
            nave1X += velocidade * delta;
            if (nave1X >= GameLib.WIDTH - 80) {
                nave1MovendoDireita = false;
            }
        } else {
            nave1X -= velocidade * delta;
            if (nave1X <= 80) {
                nave1MovendoDireita = true;
            }
        }
        
        //nave 2 - movimento oposto à nave 1
        if (nave2MovendoDireita) {
            nave2X += velocidade * delta;
            if (nave2X >= GameLib.WIDTH - 80) {
                nave2MovendoDireita = false;
            }
        } else {
            nave2X -= velocidade * delta;
            if (nave2X <= 80) {
                nave2MovendoDireita = true;
            }
        }
    }
    
    private void atualizarSistemaTiro(long currentTime) {
        long tempoDecorrido = currentTime - inicioFase;
        long cicloDuracao = duracaoTiro + duracaoPausa;
        long posicaoNoCiclo = tempoDecorrido % cicloDuracao;
        
        //determinar se está em tiro ou pausa
        periodoTiro = posicaoNoCiclo < duracaoTiro;
    }
    
    @Override
    public void desenhar() {
        if (destruido) return;
        
        desenharNave(nave1X, nave1Y);
        
        desenharNave(nave2X, nave2Y);
        
        //indica que ta no periodo de tiro
        if (periodoTiro) {
            GameLib.setColor(Color.YELLOW);
            GameLib.drawCircle(nave1X, nave1Y - 35, 4); // Maior
            GameLib.drawCircle(nave2X, nave2Y - 35, 4);
        }
    }
    
    private void desenharNave(double x, double y) {
        //corpo principal do boss
        GameLib.setColor(Color.RED);
        GameLib.drawCircle(x, y, raio); // 25 em vez de 18
        
        //núcleo de dentro
        GameLib.setColor(Color.DARK_GRAY);
        GameLib.drawCircle(x, y, raio - 8); // 17 em vez de 12
        
        //centro brilhante
        GameLib.setColor(Color.YELLOW);
        GameLib.drawCircle(x, y, raio - 15); // 10 em vez de 6
        
        //detalhes da nave
        GameLib.setColor(Color.RED);

        //asa esquerda
        GameLib.drawLine(x - 35, y - 12, x - raio, y);
        GameLib.drawLine(x - 35, y + 12, x - raio, y);

        //asa direita
        GameLib.drawLine(x + 35, y - 12, x + raio, y);
        GameLib.drawLine(x + 35, y + 12, x + raio, y);

        //tiro
        GameLib.setColor(Color.GRAY);
        GameLib.drawLine(x - 12, y + raio, x - 12, y + raio + 10);
        GameLib.drawLine(x + 12, y + raio, x + 12, y + raio + 10);
    }
    
    @Override
    public List<ProjetilInimigo> atirar(long currentTime, double playerY) {
        List<ProjetilInimigo> projeteis = new ArrayList<>();
        
        //só atira durante o período de tiro
        if (periodoTiro && currentTime - ultimoTiro >= intervaloTiro) {
            //tiro da nave 1
            projeteis.add(new ProjetilInimigo(nave1X - 12, nave1Y + raio + 10, 0, 0.45)); // Mais rápido
            projeteis.add(new ProjetilInimigo(nave1X + 12, nave1Y + raio + 10, 0, 0.45));
            
            //tiro da nave 2
            projeteis.add(new ProjetilInimigo(nave2X - 12, nave2Y + raio + 10, 0, 0.45));
            projeteis.add(new ProjetilInimigo(nave2X + 12, nave2Y + raio + 10, 0, 0.45));
            
            ultimoTiro = currentTime;
        }
        
        return projeteis;
    }
    
    @Override
    public boolean colideCom(Entidade outra) {
        if (destruido || outra == null || outra.getEstado() != Estado.ATIVO) {
            return false;
        }
        
        //verificar colisão com nave 1
        double dx1 = nave1X - outra.getX();
        double dy1 = nave1Y - outra.getY();
        double dist1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        
        //verificar colisão com nave 2
        double dx2 = nave2X - outra.getX();
        double dy2 = nave2Y - outra.getY();
        double dist2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
        
        double raioColisao = raio + outra.getRaio();
        return dist1 < raioColisao * 0.8 || dist2 < raioColisao * 0.8;
    }
    
    @Override
    public void aplicarPowerupLentidao(boolean ativo) {
        if (ativo && !powerupLentidaoAtivo) {
            velocidade = velocidadeBase * 0.4; //reduz velocidade
            powerupLentidaoAtivo = true;
            System.out.println("Zack e Cody afetado por lentidão");
        } else if (!ativo && powerupLentidaoAtivo) {
            velocidade = velocidadeBase; //volta pra velocidade original
            powerupLentidaoAtivo = false;
            System.out.println("Zack e Cody recuperou velocidade normal");
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
            System.out.println("Zack e Cody recebeu dano. Vida restante: " + vida);
            if (vida <= 0) {
                destruido = true;
                System.out.println("Você destruiu o Zack e Cody!");
            }
        }
    }
    
    @Override
    public int getVida() {
        return Math.max(0, vida);
    }
    
    @Override
    public double getX() {
        //eetorna o centro entre as duas naves
        return (nave1X + nave2X) / 2;
    }
    
    @Override
    public double getY() {
        return (nave1Y + nave2Y) / 2;
    }
    
    @Override
    public double getRaio() {
        return raio;
    }
    
    //métodos auxiliares para debug
    public boolean isAtirando() {
        return periodoTiro;
    }
    
    public double getNave1X() { return nave1X; }
    public double getNave2X() { return nave2X; }
}
