package com.barca;

import java.time.LocalTime;

public class RelogioImpl implements Relogio {
    @Override
    public int getHora() {
        return LocalTime.now().getHour();
    }

    @Override
    public int getMinuto() {
        return LocalTime.now().getMinute();
    }
}