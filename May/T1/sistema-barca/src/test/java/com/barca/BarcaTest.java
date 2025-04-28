package com.barca;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class BarcaTest {
    private Barca barca;
    private MockRelogio relogio;
    private final double PRECO_BASE = 100.0;

    @BeforeEach
    void setUp() {
        relogio = new MockRelogio();
        relogio.setHorario(10, 0); // Horário comercial padrão
        barca = new Barca(relogio, PRECO_BASE);
    }

    // 1. TESTES DE FORMATO E LIMITES DE ASSENTOS

    @Test
    @DisplayName("Teste de assentos com formato inválido")
    void testAssentoFormatoInvalido() {
        assertEquals(-1.0, barca.defineAssento("F1A1"), "Formato inválido (faltam dígitos)");
        assertEquals(-1.0, barca.defineAssento("F01A1"), "Formato inválido (falta um dígito no assento)");
        assertEquals(-1.0, barca.defineAssento("F01A001"), "Formato inválido (dígitos a mais)");
        assertEquals(-1.0, barca.defineAssento("X01A01"), "Formato inválido (letra errada)");
        assertEquals(-1.0, barca.defineAssento(null), "Formato inválido (null)");
        assertEquals(-1.0, barca.defineAssento(""), "Formato inválido (vazio)");
    }

    @Test
    @DisplayName("Teste de assentos fora dos limites")
    void testAssentoForaDosLimites() {
        assertEquals(-1.0, barca.defineAssento("F00A01"), "Fileira 0 não existe");
        assertEquals(-1.0, barca.defineAssento("F61A01"), "Fileira 61 não existe");
        assertEquals(-1.0, barca.defineAssento("F01A00"), "Assento 0 não existe");
        assertEquals(-1.0, barca.defineAssento("F01A21"), "Assento 21 não existe");
    }

    // 2. TESTES DE ASSENTOS OCUPADOS

    @Test
    @DisplayName("Teste de ocupação de assento")
    void testOcupacaoAssento() {
        double preco = barca.defineAssento("F01A01");
        assertTrue(preco > 0, "Primeira marcação deve retornar preço > 0");
        assertEquals(-2.0, barca.defineAssento("F01A01"), "Assento já ocupado deve retornar erro -2.0");
    }

    @Test
    @DisplayName("Teste de ocupação arbitrária")
    void testOcupacaoArbitraria() {
        barca.ocupacaoArbitraria("F05A10");
        assertEquals(-2.0, barca.defineAssento("F05A10"), "Assento ocupado arbitrariamente deve estar indisponível");
    }

    // 3. TESTES DE REGRAS DE DISTRIBUIÇÃO DE PESO

    @Test
    @DisplayName("Teste de distribuição de peso para primeiros 100 passageiros")
    void testDistribuicaoPesoPrimeiros100() {
        // Teste para fileira válida (1-20)
        assertTrue(barca.defineAssento("F01A01") > 0, "Primeiro passageiro deve conseguir assento na fileira 1");
        
        // Teste para fileira inválida (21-60)
        assertEquals(-3.0, barca.defineAssento("F21A01"), "Primeiro passageiro não deve sentar na fileira 21");
    }

    @Test
    @DisplayName("Teste de distribuição de peso para próximos 100 passageiros")
    void testDistribuicaoPesoProximos100() {
        // Ocupa os primeiros 99 assentos
        for (int i = 1; i <= 99; i++) {
            String fileira = String.format("%02d", (i % 20 == 0) ? 20 : i % 20);
            String assento = String.format("%02d", ((i - 1) / 20) + 1);
            barca.ocupacaoArbitraria("F" + fileira + "A" + assento);
        }
        
        // Teste para 100º passageiro na fileira válida (1-20)
        assertTrue(barca.defineAssento("F01A06") > 0, "100º passageiro deve conseguir assento na fileira 1-20");
        
        // Teste para 101º passageiro na fileira válida (40-60)
        assertTrue(barca.defineAssento("F40A01") > 0, "101º passageiro deve conseguir assento na fileira 40-60");
        
        // Teste para 101º passageiro na fileira inválida (1-39)
        assertEquals(-3.0, barca.defineAssento("F21A01"), "101º passageiro não deve sentar na fileira 21-39");
    }

    @Test
    @DisplayName("Teste de distribuição de peso para demais passageiros")
    void testDistribuicaoPesoDemaisPassageiros() {
        // Ocupa os primeiros 200 assentos arbitrariamente
        for (int i = 1; i <= 200; i++) {
            String fileira, assento;
            if (i <= 100) {
                fileira = String.format("%02d", (i % 20 == 0) ? 20 : i % 20);
                assento = String.format("%02d", ((i - 1) / 20) + 1);
            } else {
                fileira = String.format("%02d", 40 + ((i - 101) % 21));
                assento = String.format("%02d", ((i - 101) / 21) + 1);
            }
            barca.ocupacaoArbitraria("F" + fileira + "A" + assento);
        }

        // Teste para 201º passageiro em qualquer fileira
        assertTrue(barca.defineAssento("F21A01") > 0, "O 201º passageiro deve poder sentar em qualquer fileira");
        assertTrue(barca.defineAssento("F30A01") > 0, "O 202º passageiro deve poder sentar em qualquer fileira");
    }

    // 4. TESTES DE CÁLCULO DE PREÇO EM DIFERENTES HORÁRIOS

    @ParameterizedTest
    @CsvSource({
        "8, 0, 100.0",  // 8:00 - horário comercial
        "12, 0, 100.0", // 12:00 - horário comercial
        "14, 0, 100.0", // 14:00 - horário comercial
        "18, 0, 100.0"  // 18:00 - horário comercial
    })
    @DisplayName("Teste de preço em horário comercial padrão")
    void testPrecoHorarioComercialPadrao(int hora, int minuto, double precoEsperado) {
        relogio.setHorario(hora, minuto);
        assertEquals(precoEsperado, barca.defineAssento("F01A10"), 0.01);
    }

    @ParameterizedTest
    @CsvSource({
        "12, 1, 110.0", // 12:01 - horário de almoço
        "13, 59, 110.0", // 13:59 - horário de almoço
        "18, 1, 110.0", // 18:01 - início da noite
        "19, 59, 110.0" // 19:59 - início da noite
    })
    @DisplayName("Teste de preço em horário de almoço/início da noite (+10%)")
    void testPrecoHorarioAlmocoNoite(int hora, int minuto, double precoEsperado) {
        relogio.setHorario(hora, minuto);
        assertEquals(precoEsperado, barca.defineAssento("F01A11"), 0.01);
    }

    @ParameterizedTest
    @CsvSource({
        "20, 0, 120.0", // 20:00 - horário noturno
        "22, 30, 120.0", // 22:30 - horário noturno
        "23, 59, 120.0" // 23:59 - horário noturno
    })
    @DisplayName("Teste de preço em horário noturno (+20%)")
    void testPrecoHorarioNoturno(int hora, int minuto, double precoEsperado) {
        relogio.setHorario(hora, minuto);
        assertEquals(precoEsperado, barca.defineAssento("F01A12"), 0.01);
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0, 150.0", // 00:00 - madrugada
        "3, 30, 150.0", // 03:30 - madrugada
        "7, 59, 150.0" // 07:59 - madrugada
    })
    @DisplayName("Teste de preço em horário de madrugada (+50%)")
    void testPrecoHorarioMadrugada(int hora, int minuto, double precoEsperado) {
        relogio.setHorario(hora, minuto);
        assertEquals(precoEsperado, barca.defineAssento("F01A13"), 0.01);
    }

    // 5. TESTES DE VALORES LIMITE PARA FAIXAS DE HORÁRIO

    @Test
    @DisplayName("Teste de valores limite entre faixas de horário")
    void testValoresLimiteHorario() {
        // Madrugada → Comercial
        relogio.setHorario(7, 59);
        assertEquals(PRECO_BASE * 1.5, barca.defineAssento("F02A01"), 0.01);
        
        relogio.setHorario(8, 0);
        assertEquals(PRECO_BASE, barca.defineAssento("F02A02"), 0.01);

        // Comercial → Almoço
        relogio.setHorario(12, 0);
        assertEquals(PRECO_BASE, barca.defineAssento("F02A03"), 0.01);
        
        relogio.setHorario(12, 1);
        assertEquals(PRECO_BASE * 1.1, barca.defineAssento("F02A04"), 0.01);

        // Almoço → Comercial
        relogio.setHorario(13, 59);
        assertEquals(PRECO_BASE * 1.1, barca.defineAssento("F02A05"), 0.01);
        
        relogio.setHorario(14, 0);
        assertEquals(PRECO_BASE, barca.defineAssento("F02A06"), 0.01);

        // Comercial → Noite
        relogio.setHorario(18, 0);
        assertEquals(PRECO_BASE, barca.defineAssento("F02A07"), 0.01);
        
        relogio.setHorario(18, 1);
        assertEquals(PRECO_BASE * 1.1, barca.defineAssento("F02A08"), 0.01);

        // Noite → Noturno
        relogio.setHorario(19, 59);
        assertEquals(PRECO_BASE * 1.1, barca.defineAssento("F02A09"), 0.01);
        
        relogio.setHorario(20, 0);
        assertEquals(PRECO_BASE * 1.2, barca.defineAssento("F02A10"), 0.01);

        // Noturno → Madrugada
        relogio.setHorario(23, 59);
        assertEquals(PRECO_BASE * 1.2, barca.defineAssento("F02A11"), 0.01);
        
        relogio.setHorario(0, 0);
        assertEquals(PRECO_BASE * 1.5, barca.defineAssento("F02A12"), 0.01);
    }
}