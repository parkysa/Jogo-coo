package org.example;

import org.example.entidades.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.*;
import java.util.Scanner;

public class Main {

    public static void busyWait(long time) {
        while (System.currentTimeMillis() < time) Thread.yield();
    }

    //verifica colisão entre player e powerup
    private static boolean verificarColisao(Player player, Powerup powerup) {
        try {
            double dx = player.getX() - powerup.getX();
            double dy = player.getY() - powerup.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            return dist < (player.getRaio() + powerup.getRaio()) * 0.8;
        } catch (Exception e) {
            return false;
        }
    }
    
    //desenha filtro roxo quando controles estão invertidos
    private static void desenharFiltroInversao(Player player, long currentTime) {
        if (player.temControlesInvertidos()) {
            //calcula intensidade do filtro baseado no tempo restante
            long tempoRestante = player.getFimInversao() - currentTime;
            long duracaoTotal = player.getFimInversao() - player.getInicioInversao();
            
            if (duracaoTotal > 0) {
                double intensidade = Math.max(0.1, (double) tempoRestante / duracaoTotal);
                int alpha = (int) (80 * intensidade); // Opacidade entre 8 e 80
                
                //desenha filtro roxo
                try {
                    Color filtro = new Color(128, 0, 128, alpha);
                    GameLib.setColor(filtro);
                    
                    //desenha retângulos para simular filtro
                    for (int i = 0; i < GameLib.HEIGHT; i += 20) {
                        GameLib.fillRect(GameLib.WIDTH / 2.0, i + 10, GameLib.WIDTH, 20);
                    }
                } catch (Exception e) {
                    GameLib.setColor(new Color(128, 0, 128));
                    for (int i = 0; i < 5; i++) {
                        GameLib.drawLine(0, i * GameLib.HEIGHT / 5, GameLib.WIDTH, i * GameLib.HEIGHT / 5);
                    }
                }
            }
        }
    }
    
    //desenha filtro azul quando power-up de lentidão ta ativo
    private static void desenharFiltroLentidao(Player player) {
        if (player.hasPowerupLentidao()) {
            try {
                Color filtro = new Color(0, 150, 255, 60); // Azul claro com baixa opacidade
                GameLib.setColor(filtro);
                
                //desenha retângulos para simular filtro
                for (int i = 0; i < GameLib.HEIGHT; i += 25) {
                    GameLib.fillRect(GameLib.WIDTH / 2.0, i + 12, GameLib.WIDTH, 25);
                }
            } catch (Exception e) {
                GameLib.setColor(new Color(0, 150, 255));
                for (int i = 0; i < 4; i++) {
                    GameLib.drawLine(0, i * GameLib.HEIGHT / 4, GameLib.WIDTH, i * GameLib.HEIGHT / 4);
                }
            }
        }
    }
    
    //metodo para desenhar filtro amarelo quando power-up de velocidade está ativo
    private static void desenharFiltroVelocidade(Player player) {
        if (player.hasPowerupVelocidade()) {
            try {
                Color filtro = new Color(255, 255, 0, 50); // Amarelo claro com baixa opacidade
                GameLib.setColor(filtro);
                
                //desenha retângulos para simular filtro
                for (int i = 0; i < GameLib.HEIGHT; i += 30) {
                    GameLib.fillRect(GameLib.WIDTH / 2.0, i + 15, GameLib.WIDTH, 30);
                }
            } catch (Exception e) {
                GameLib.setColor(new Color(255, 255, 0));
                for (int i = 0; i < 3; i++) {
                    GameLib.drawLine(0, i * GameLib.HEIGHT / 3, GameLib.WIDTH, i * GameLib.HEIGHT / 3);
                }
            }
        }
    }

    public static void main(String[] args) {

        boolean running = true;
        long currentTime = System.currentTimeMillis();
        long delta;

        //sistema de fases
        Fase faseAtual = Fase.FASE_1;
        boolean bossAtivo = false;
        Boss bossAtual = null;
        int inimigosDestruidos = 0;
        int inimigosNecessarios = 24; // AUMENTADO: 24 inimigos para ambas as fases

        //sistema de vitória
        boolean jogoFinalizado = false;
        long tempoFinalizacao = 0;
        long delayFechamento = 3000; // 3 segundos

        //entidades do jogo
        int playerVida = 0;
        List<Projetil> projeteisPlayer = new ArrayList<>();
        List<ProjetilInimigo> projeteisInimigos = new ArrayList<>();
        List<Inimigo> inimigos = new ArrayList<>();
        List<Powerup> powerups = new ArrayList<>();
        List<Powerup> powerupsAtivos = new ArrayList<>();
        List<Spawn> fases[] = new ArrayList[1];

        //arquivos de configuração
        String configPath = "config/Config.txt";
        try{
            File configFile = new File(configPath);
            Scanner configScanner = new Scanner(configFile);
            playerVida = configScanner.nextInt();
            int numFases = configScanner.nextInt();
            fases = new ArrayList[numFases];
            for(int i = 0; i<numFases; i++){
                String fasePath = configScanner.next();
                File faseFile = new File(fasePath);
                Scanner faseScanner = new Scanner(faseFile);
                while(faseScanner.hasNextLine()){
                    fases[i] = new ArrayList<Spawn>();
                    String spawn = faseScanner.next();
                    int tipo = faseScanner.nextInt();
                    int vida = -1;
                    if(spawn=="CHEFE") vida = faseScanner.nextInt();
                    long tempo = faseScanner.nextInt();
                    double x = faseScanner.nextDouble();
                    double y = faseScanner.nextDouble();
                    fases[i].add(new Spawn(spawn, tipo, vida, tempo, x, y));
                }
            }
        }catch(Exception e){
        }

        //player
        Player player = new Player(playerVida);
      
        //inimigos
        long proximoInimigo1 = currentTime + 2000;
        long proximoInimigo2 = currentTime + 7000;
        int contagemInimigo2 = 0;
        double spawnXInimigo2 = GameLib.WIDTH * 0.20;

        //powerups
        long proximoPowerup = currentTime + 8000;

        //background - fase 1
        double[][] background1 = new double[15][2];
        double[][] background2 = new double[30][2];
        double background1_speed = 0.050;
        double background2_speed = 0.030;
        double background1_count = 0.0;
        double background2_count = 0.0;

        //inicializar background da fase 1
        for (int i = 0; i < background1.length; i++) {
            background1[i][0] = Math.random() * GameLib.WIDTH;
            background1[i][1] = Math.random() * GameLib.HEIGHT;
        }
        for (int i = 0; i < background2.length; i++) {
            background2[i][0] = Math.random() * GameLib.WIDTH;
            background2[i][1] = Math.random() * GameLib.HEIGHT;
        }

        GameLib.initGraphics();

        while (running) {
            try {
                delta = System.currentTimeMillis() - currentTime;
                currentTime = System.currentTimeMillis();

                //verifica se deve fechar o jogo
                if (jogoFinalizado && currentTime - tempoFinalizacao >= delayFechamento && player.getVida() == 0) {
                    running = false;
                    break;
                }

                //player (sempre pode se mover)
                player.atualizar(delta, currentTime);
                Projetil novoTiro = player.atirar(currentTime);
                if (novoTiro != null) {
                    novoTiro.aplicarPowerupVelocidade(player.hasPowerupVelocidade());
                    projeteisPlayer.add(novoTiro);
                }

                //powerups
                Iterator<Powerup> powerupIterator = powerups.iterator();
                while (powerupIterator.hasNext()) {
                    Powerup powerup = powerupIterator.next();
                    powerup.atualizarMovimento(delta, currentTime);
                    if (powerup.getEstado() == Estado.INATIVO) {
                        powerupIterator.remove();
                    }
                }

                //gerenciar powerups ativos
                Iterator<Powerup> ativosIterator = powerupsAtivos.iterator();
                while (ativosIterator.hasNext()) {
                    Powerup powerup = ativosIterator.next();
                    powerup.atualizar(currentTime);
                    if (!powerup.isAtivo()) {
                        powerup.remover(player);
                        ativosIterator.remove();
                    }
                }

                //boss
                if (bossAtivo && bossAtual != null) {
                    //aplica power-up de lentidão no boss
                    bossAtual.aplicarPowerupLentidao(player.hasPowerupLentidao());
                    
                    bossAtual.atualizar(delta, currentTime);
                    List<ProjetilInimigo> tirosBoss = bossAtual.atirar(currentTime, player.getY());
                    if (tirosBoss != null && !tirosBoss.isEmpty()) {
                        for (ProjetilInimigo proj : tirosBoss) {
                            if (proj != null) {
                                proj.aplicarPowerupLentidao(player.hasPowerupLentidao());
                            }
                        }
                        projeteisInimigos.addAll(tirosBoss);
                    }

                    if (bossAtual.isDestruido()) {
                        bossAtivo = false;
                        bossAtual = null;
                        
                        if (faseAtual == Fase.FASE_1) {
                            // Transição imediata para Fase 2
                            faseAtual = Fase.FASE_2;
                            inimigosDestruidos = 0;
                            inimigosNecessarios = 24;
                            
                            //altera background para fase 2
                            background1 = new double[25][2];
                            background2 = new double[60][2];
                            background1_speed = 0.120;
                            background2_speed = 0.080;
                            
                            //reinicializar background da fase 2
                            for (int i = 0; i < background1.length; i++) {
                                background1[i][0] = Math.random() * GameLib.WIDTH;
                                background1[i][1] = Math.random() * GameLib.HEIGHT;
                            }
                            for (int i = 0; i < background2.length; i++) {
                                background2[i][0] = Math.random() * GameLib.WIDTH;
                                background2[i][1] = Math.random() * GameLib.HEIGHT;
                            }
                            
                        } else if (faseAtual == Fase.FASE_2) {
                            //inicia período de vitória com delay
                            faseAtual = Fase.CONCLUIDA;
                            jogoFinalizado = true;
                            tempoFinalizacao = currentTime;
                            System.out.println("Jogo concluído! Fechando em 3 segundos...");
                        }
                    }
                }

                //inimigos, se o jogo não foi finalizado
                if (!jogoFinalizado) {
                    Iterator<Inimigo> inimigoIterator = inimigos.iterator();
                    while (inimigoIterator.hasNext()) {
                        Inimigo inimigo = inimigoIterator.next();
                        
                        //aplica powerups baseado no estado atual do player
                        if (inimigo instanceof InimigoTipo1) {
                            ((InimigoTipo1) inimigo).aplicarPowerupLentidao(player.hasPowerupLentidao());
                        } else if (inimigo instanceof InimigoTipo2) {
                            ((InimigoTipo2) inimigo).aplicarPowerupLentidao(player.hasPowerupLentidao());
                        }
                        
                        //atualiza movimento
                        inimigo.atualizar(delta, currentTime);

                        //tiros dos inimigos
                        if (inimigo instanceof InimigoTipo1) {
                            ProjetilInimigo tiroInimigo = inimigo.atirar(currentTime, player.getY());
                            if (tiroInimigo != null) {
                                tiroInimigo.aplicarPowerupLentidao(player.hasPowerupLentidao());
                                projeteisInimigos.add(tiroInimigo);
                            }
                        }

                        if (inimigo instanceof InimigoTipo2) {
                            List<ProjetilInimigo> tiros = ((InimigoTipo2) inimigo).atirarEspecial();
                            if (tiros != null) {
                                for (ProjetilInimigo proj : tiros) {
                                    if (proj != null) {
                                        proj.aplicarPowerupLentidao(player.hasPowerupLentidao());
                                    }
                                }
                                projeteisInimigos.addAll(tiros);
                            }
                        }
                        
                        //remove inimigos inativos
                        if (inimigo.getEstado() == Estado.INATIVO) {
                            inimigoIterator.remove();
                        }
                    }
                } else {
                    //jogo finalizado: apenas atualizar inimigos existentes
                    Iterator<Inimigo> inimigoIterator = inimigos.iterator();
                    while (inimigoIterator.hasNext()) {
                        Inimigo inimigo = inimigoIterator.next();
                        inimigo.atualizar(delta, currentTime);
                        if (inimigo.getEstado() == Estado.INATIVO) {
                            inimigoIterator.remove();
                        }
                    }
                }

                //projéteis
                for (Projetil p : projeteisPlayer) {
                    p.atualizar(delta, currentTime);
                }
                for (ProjetilInimigo p : projeteisInimigos) {
                    p.atualizar(delta, currentTime);
                }

                //background
                background1_count += background1_speed * delta;
                background2_count += background2_speed * delta;

                if (!jogoFinalizado) {
                    //projéteis do player com inimigos
                    for (Projetil p : projeteisPlayer) {
                        Iterator<Inimigo> colisaoIterator = inimigos.iterator();
                        while (colisaoIterator.hasNext()) {
                            Inimigo i = colisaoIterator.next();
                            if (p.getEstado() == Estado.ATIVO && i.getEstado() == Estado.ATIVO && p.colideCom(i)) {
                                i.explodir(currentTime);
                                p.setEstado(Estado.INATIVO);
                                inimigosDestruidos++;
                                break;
                            }
                        }
                    }

                    //projéteis do player com boss
                    if (bossAtivo && bossAtual != null && !bossAtual.isDestruido()) {
                        for (Projetil p : projeteisPlayer) {
                            if (p.getEstado() == Estado.ATIVO && bossAtual.colideCom(p)) {
                                bossAtual.receberDano();
                                p.setEstado(Estado.INATIVO);
                            }
                        }
                    }

                    //player com powerups
                    Iterator<Powerup> powerupColisaoIterator = powerups.iterator();
                    while (powerupColisaoIterator.hasNext()) {
                        Powerup powerup = powerupColisaoIterator.next();
                        if (powerup.getEstado() == Estado.ATIVO && player.getEstado() == Estado.ATIVO && verificarColisao(player, powerup)) {
                            powerup.setEstado(Estado.INATIVO);
                            powerup.ativar(currentTime);
                            powerup.aplicar(player);
                            powerupsAtivos.add(powerup);
                            powerupColisaoIterator.remove();
                        }
                    }

                    //player com inimigos
                    if (player.getEstado() == Estado.ATIVO) {
                        for (Inimigo i : inimigos) {
                            if (i.getEstado() == Estado.ATIVO && player.colideCom(i)) {
                                player.explodir(currentTime);
                                break;
                            }
                        }
                    }

                    //player com projéteis inimigos
                    if (player.getEstado() == Estado.ATIVO) {
                        Iterator<ProjetilInimigo> projetilIterator = projeteisInimigos.iterator();
                        while (projetilIterator.hasNext()) {
                            ProjetilInimigo pi = projetilIterator.next();
                            if (pi.getEstado() == Estado.ATIVO && player.colideCom(pi)) {
                                
                                //verificar se é projétil especial do boss
                                if (pi instanceof ProjetilBoss) {
                                    //projétil do boss inverte controles
                                    player.inverterControles(currentTime, 5000); // 5 segundos
                                    System.out.println("O Mystherio te atingiu! Controles invertidos por 5 segundos");
                                } else {
                                    //projéteis normais causam explosão
                                    player.explodir(currentTime);
                                }
                                
                                pi.setEstado(Estado.INATIVO);
                                break;
                            }
                        }
                    }

                    //player com boss
                    if (bossAtivo && bossAtual != null && !bossAtual.isDestruido() && player.getEstado() == Estado.ATIVO) {
                        if (bossAtual.colideCom(player)) {
                            player.explodir(currentTime);
                        }
                    }
                }

                if (!jogoFinalizado) {
                    // insere boss se necessário
                    if (!bossAtivo && inimigosDestruidos >= inimigosNecessarios) {
                        if (faseAtual == Fase.FASE_1) {
                            bossAtual = new BossFase1(45);
                            System.out.println("É o momento de enfrentar os gêmeos Zach & Cody!");
                        } else if (faseAtual == Fase.FASE_2) {
                            bossAtual = new BossFase2();
                            System.out.println("É o momento de enfrentar o Mystherio!");
                        }
                        bossAtivo = true;
                    }

                    // insere inimigos se não há boss ativo (mesma frequência em ambas as fases)
                    if (!bossAtivo) {
                        if (currentTime > proximoInimigo1) {
                            inimigos.add(new InimigoTipo1(Math.random() * (GameLib.WIDTH - 20.0) + 10.0, -10.0));
                            proximoInimigo1 = currentTime + 600; // Mesma velocidade em ambas as fases
                        }

                        if (currentTime > proximoInimigo2) {
                            inimigos.add(new InimigoTipo2(spawnXInimigo2, -10.0));
                            contagemInimigo2++;
                            if (contagemInimigo2 < 10) {
                                proximoInimigo2 = currentTime + 150; // Mesma velocidade em ambas as fases
                            } else {
                                contagemInimigo2 = 0;
                                spawnXInimigo2 = Math.random() > 0.5 ? GameLib.WIDTH * 0.2 : GameLib.WIDTH * 0.8;
                                proximoInimigo2 = (long) (currentTime + 2000 + Math.random() * 2000);
                            }
                        }
                    }

                    //insere powerups
                    if (currentTime > proximoPowerup) {
                        if (Math.random() > 0.5) {
                            powerups.add(new PowerupVelocidade(Math.random() * (GameLib.WIDTH - 40) + 20, -10));
                        } else {
                            powerups.add(new PowerupLentidao(Math.random() * (GameLib.WIDTH - 40) + 20, -10));
                        }
                        proximoPowerup = currentTime + 12000 + (long)(Math.random() * 8000);
                    }
                }

                projeteisPlayer.removeIf(p -> p.getEstado() == Estado.INATIVO);
                projeteisInimigos.removeIf(p -> p.getEstado() == Estado.INATIVO);

                if (GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) running = false;

                //fundo (diferente para cada fase)
                if (faseAtual == Fase.FASE_1) {
                    //fundo fase 1
                    GameLib.setColor(new Color(20, 20, 40)); // Azul escuro
                } else if (faseAtual == Fase.FASE_2) {
                    //fundo fase 2
                    GameLib.setColor(new Color(40, 20, 20)); // Vermelho escuro
                } else {
                    //fundo de vitória
                    GameLib.setColor(new Color(60, 50, 20)); // Dourado escuro
                }
                
                for (double[] star : background2) {
                    GameLib.fillRect(star[0], (star[1] + background2_count) % GameLib.HEIGHT, 2, 2);
                }
                
                if (faseAtual == Fase.FASE_1) {
                    GameLib.setColor(new Color(60, 60, 100)); // Azul claro
                } else if (faseAtual == Fase.FASE_2) {
                    GameLib.setColor(new Color(100, 60, 60)); // Vermelho claro
                } else {
                    GameLib.setColor(new Color(120, 100, 40)); // Dourado claro
                }
                
                for (double[] star : background1) {
                    GameLib.fillRect(star[0], (star[1] + background1_count) % GameLib.HEIGHT, 3, 3);
                }

                //entidades
                player.desenhar();
                projeteisPlayer.forEach(Entidade::desenhar);
                projeteisInimigos.forEach(Entidade::desenhar);
                inimigos.forEach(Entidade::desenhar);
                
                //powerups
                for (Powerup powerup : powerups) {
                    powerup.desenhar();
                }

                //boss
                if (bossAtivo && bossAtual != null) {
                    bossAtual.desenhar();
                }

                //cada filtro aparece baseado no estado individual do power-up
                desenharFiltroInversao(player, currentTime);
                desenharFiltroLentidao(player);
                desenharFiltroVelocidade(player);

                //informações da fase
                GameLib.setColor(Color.WHITE);
                if (faseAtual == Fase.FASE_1) {
                    //desenha fase 1
                    GameLib.drawLine(10, 10, 30, 10);
                    GameLib.drawLine(10, 10, 10, 30);
                    GameLib.drawLine(10, 20, 25, 20);
                    
                    GameLib.drawLine(40, 10, 50, 10);
                    GameLib.drawLine(45, 10, 45, 30);
                } else if (faseAtual == Fase.FASE_2) {
                    //desenha fase 2
                    GameLib.drawLine(10, 10, 30, 10);
                    GameLib.drawLine(10, 10, 10, 30);
                    GameLib.drawLine(10, 20, 25, 20);
                    
                    GameLib.drawLine(40, 10, 60, 10);
                    GameLib.drawLine(40, 20, 60, 20);
                    GameLib.drawLine(40, 30, 60, 30);
                    GameLib.drawLine(40, 20, 40, 30);
                    GameLib.drawLine(60, 10, 60, 20);
                }

                //contador de inimigos derrotados
                if (!jogoFinalizado) {
                    GameLib.setColor(Color.YELLOW);
                    GameLib.drawLine(10, 50, 15, 50);
                    GameLib.drawLine(10, 60, 15, 60);
                }

                //mostra vida do boss
                if (bossAtivo && bossAtual != null) {
                    GameLib.setColor(Color.RED);
                    double maxVida = (faseAtual == Fase.FASE_1 ? 45.0 : 69.0); // Vidas atualizadas
                    double barWidth = (bossAtual.getVida() / maxVida) * 200;
                    GameLib.fillRect(GameLib.WIDTH / 2.0, 40, Math.max(0, barWidth), 8);
                    
                    //borda da barra de vida
                    GameLib.setColor(Color.WHITE);
                    GameLib.drawLine(GameLib.WIDTH / 2.0 - 100, 36, GameLib.WIDTH / 2.0 + 100, 36);
                    GameLib.drawLine(GameLib.WIDTH / 2.0 - 100, 44, GameLib.WIDTH / 2.0 + 100, 44);
                    GameLib.drawLine(GameLib.WIDTH / 2.0 - 100, 36, GameLib.WIDTH / 2.0 - 100, 44);
                    GameLib.drawLine(GameLib.WIDTH / 2.0 + 100, 36, GameLib.WIDTH / 2.0 + 100, 44);
                }

                //indicador de controles invertidos
                if (player.temControlesInvertidos()) {
                    GameLib.setColor(Color.MAGENTA);
                    GameLib.drawCircle(GameLib.WIDTH - 30, 30, 10);
                    GameLib.drawLine(GameLib.WIDTH - 35, 25, GameLib.WIDTH - 25, 35);
                    GameLib.drawLine(GameLib.WIDTH - 25, 25, GameLib.WIDTH - 35, 35);
                    
                    // Texto "INVERTIDO"
                    GameLib.setColor(Color.WHITE);
                    GameLib.drawLine(GameLib.WIDTH - 80, 50, GameLib.WIDTH - 80, 60);
                    GameLib.drawLine(GameLib.WIDTH - 75, 50, GameLib.WIDTH - 75, 60);
                }

                //indicadores de power-ups ativos
                if (player.hasPowerupVelocidade()) {
                    GameLib.setColor(Color.YELLOW);
                    GameLib.drawCircle(GameLib.WIDTH - 60, 70, 8);
                    GameLib.drawLine(GameLib.WIDTH - 65, 70, GameLib.WIDTH - 55, 70);
                }
                
                if (player.hasPowerupLentidao()) {
                    GameLib.setColor(Color.CYAN);
                    GameLib.drawCircle(GameLib.WIDTH - 30, 70, 8);
                    GameLib.drawLine(GameLib.WIDTH - 35, 70, GameLib.WIDTH - 25, 70);
                }

                GameLib.display();
                busyWait(currentTime + 3);
                
            } catch (Exception e) {
                System.err.println("Erro no loop principal: " + e.getMessage());
            }
        }

        System.exit(0);
    }
}
