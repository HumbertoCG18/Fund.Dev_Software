package com.barca;

public class MockRelogio implements Relogio {
    private int hora = 10;
    private int minuto = 0;

    public void setHora(int hora) {
        this.hora = hora;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
    
    public void setHorario(int hora, int minuto) {
        this.hora = hora;
        this.minuto = minuto;
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