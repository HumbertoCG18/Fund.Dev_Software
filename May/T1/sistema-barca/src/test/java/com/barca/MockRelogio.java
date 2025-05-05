package com.barca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockRelogio implements Relogio {
    private static final Logger logger = LoggerFactory.getLogger(MockRelogio.class);
    
    private int hora = 10;
    private int minuto = 0;

    public void setHora(int hora) {
        this.hora = hora;
        logger.debug("Hora do relógio alterada para: {}", hora);
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
        logger.debug("Minuto do relógio alterado para: {}", minuto);
    }
    
    public void setHorario(int hora, int minuto) {
        this.hora = hora;
        this.minuto = minuto;
        logger.debug("Horário do relógio alterado para: {}:{}", hora, minuto);
    }

    @Override
    public int getHora() {
        return hora;
    }

    @Override
    public int getMinuto() {
        return minuto;
    }
}