package org.example.entidades;


import java.io.*;
import java.util.*;

public class CarregadorDeFase {

    public static List<EventoFase> carregarEventos(String caminho) throws IOException {
        List<EventoFase> eventos = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(caminho));
        String linha;

        while ((linha = reader.readLine()) != null) {
            String[] partes = linha.trim().split("\\s+");

            if (partes.length == 0) continue;

            switch (partes[0]) {
                case "INIMIGO":
                    eventos.add(new EventoFase(
                        EventoFase.Tipo.INIMIGO,
                        Integer.parseInt(partes[1]),
                        Long.parseLong(partes[2]),
                        Double.parseDouble(partes[3]),
                        Double.parseDouble(partes[4])
                    ));
                    break;

                case "POWERUP":
                    eventos.add(new EventoFase(
                        EventoFase.Tipo.POWERUP,
                        Integer.parseInt(partes[1]),
                        Long.parseLong(partes[2]),
                        Double.parseDouble(partes[3]),
                        Double.parseDouble(partes[4])
                    ));
                    break;

                case "CHEFE":
                    eventos.add(new EventoFase(
                        EventoFase.Tipo.CHEFE,
                        Integer.parseInt(partes[1]),
                        Integer.parseInt(partes[2]),
                        Long.parseLong(partes[3]),
                        Double.parseDouble(partes[4]),
                        Double.parseDouble(partes[5])
                    ));
                    break;

                default:
                    System.err.println("Linha inválida: " + linha);
                    break;
            }
        }

        reader.close();
        return eventos;
    }
}

