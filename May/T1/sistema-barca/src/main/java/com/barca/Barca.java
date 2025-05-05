package com.barca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Barca {
    private static final Logger logger = LoggerFactory.getLogger(Barca.class);
    
    private Relogio relogio;
    private double precoBase;
    private boolean[][] assentos; // true = ocupado, false = livre
    private int passageirosEmbarcados;

    // Constantes para códigos de erro
    public static final double ERRO_ASSENTO_INVALIDO = -1.0;
    public static final double ERRO_ASSENTO_OCUPADO = -2.0;
    public static final double ERRO_ASSENTO_BLOQUEADO = -3.0;

    public Barca(Relogio relogio, double precoBase) {
        this.relogio = relogio;
        this.precoBase = precoBase;
        this.assentos = new boolean[60][20]; // 60 fileiras x 20 assentos
        this.passageirosEmbarcados = 0;
        
        logger.info("Barca inicializada com preço base: {}", precoBase);
    }

    public double defineAssento(String assentoInformado) {
        logger.debug("Solicitação para definir assento: {}", assentoInformado);
        
        // Verificar formato do assento
        if (!validarFormatoAssento(assentoInformado)) {
            logger.info("Assento com formato inválido: {}", assentoInformado);
            return ERRO_ASSENTO_INVALIDO;
        }

        // Extrair fileira e assento
        int fileira = Integer.parseInt(assentoInformado.substring(1, 3));
        int assento = Integer.parseInt(assentoInformado.substring(4, 6));

        // Verificar limites de fileira e assento
        if (fileira < 1 || fileira > 60 || assento < 1 || assento > 20) {
            logger.info("Assento fora dos limites permitidos: {}", assentoInformado);
            return ERRO_ASSENTO_INVALIDO;
        }

        // Ajustar para índices baseados em zero
        int fileiraIndex = fileira - 1;
        int assentoIndex = assento - 1;

        // Verificar se o assento já está ocupado
        if (assentos[fileiraIndex][assentoIndex]) {
            logger.info("Assento já está ocupado: {}", assentoInformado);
            return ERRO_ASSENTO_OCUPADO;
        }

        // Verificar regras de distribuição de peso
        if (!verificarRegrasDistribuicaoPeso(fileira)) {
            logger.info("Assento bloqueado por regras de distribuição de peso: {}", assentoInformado);
            return ERRO_ASSENTO_BLOQUEADO;
        }

        // Marcar assento como ocupado
        assentos[fileiraIndex][assentoIndex] = true;
        passageirosEmbarcados++;
        
        // Calcular e retornar o preço da passagem
        double preco = calcularPrecoPassagem();
        logger.info("Assento {} marcado com sucesso. Preço: {}", assentoInformado, preco);
        return preco;
    }

    private boolean validarFormatoAssento(String assento) {
        // Formato esperado: FxxAxx, onde x são dígitos
        if (assento == null || assento.length() != 6) {
            return false;
        }
        return assento.matches("F\\d{2}A\\d{2}");
    }

    private boolean verificarRegrasDistribuicaoPeso(int fileira) {
        // Primeiros 100 passageiros só podem se sentar nas fileiras de 1 a 20
        if (passageirosEmbarcados < 100) {
            return fileira >= 1 && fileira <= 20;
        }
        // Próximos 100 passageiros só podem se sentar nas fileiras de 40 a 60
        else if (passageirosEmbarcados < 200) {
            return fileira >= 40 && fileira <= 60;
        }
        // Demais passageiros podem sentar-se em qualquer lugar livre
        return true;
    }

    private double calcularPrecoPassagem() {
        int hora = relogio.getHora();
        int minuto = relogio.getMinuto();
        double preco = precoBase;

        // Horário da madrugada (00:00 às 7:59) - 50% mais caro
        if (hora >= 0 && hora < 8) {
            preco *= 1.5;
            logger.debug("Aplicado aumento de 50% para horário de madrugada ({}:{})", hora, minuto);
        }
        // Horário do almoço (12:01 às 13:59) - 10% mais caro
        else if ((hora == 12 && minuto > 0) || hora == 13) {
            preco *= 1.1;
            logger.debug("Aplicado aumento de 10% para horário de almoço ({}:{})", hora, minuto);
        }
        // Início da noite (18:01 às 19:59) - 10% mais caro
        else if ((hora == 18 && minuto > 0) || hora == 19) {
            preco *= 1.1;
            logger.debug("Aplicado aumento de 10% para início da noite ({}:{})", hora, minuto);
        }
        // Horário noturno (20:00 às 23:59) - 20% mais caro
        else if (hora >= 20 && hora <= 23) {
            preco *= 1.2;
            logger.debug("Aplicado aumento de 20% para horário noturno ({}:{})", hora, minuto);
        }

        return preco;
    }

    public void ocupacaoArbitraria(String assentoInformado) {
        logger.debug("Ocupação arbitrária para assento: {}", assentoInformado);
        
        if (validarFormatoAssento(assentoInformado)) {
            int fileira = Integer.parseInt(assentoInformado.substring(1, 3));
            int assento = Integer.parseInt(assentoInformado.substring(4, 6));
            
            if (fileira >= 1 && fileira <= 60 && assento >= 1 && assento <= 20) {
                assentos[fileira - 1][assento - 1] = true;
                passageirosEmbarcados++;
                logger.info("Assento {} ocupado arbitrariamente", assentoInformado);
            } else {
                logger.warn("Tentativa de ocupação arbitrária inválida: assento fora dos limites");
            }
        } else {
            logger.warn("Tentativa de ocupação arbitrária inválida: formato incorreto");
        }
    }
    
    // Método para testes: retorna o número de passageiros embarcados
    public int getPassageirosEmbarcados() {
        return passageirosEmbarcados;
    }
}