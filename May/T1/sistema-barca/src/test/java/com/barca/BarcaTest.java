package com.barca;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("📋 Testes da classe Barca")
class BarcaTest {
    private static final Logger logger = LoggerFactory.getLogger(BarcaTest.class);
    
    private Barca barca;
    private MockRelogio relogio;
    private final double PRECO_BASE = 100.0;

    @BeforeEach
    void setUp() {
        relogio = new MockRelogio();
        relogio.setHorario(10, 0); // Horário comercial padrão
        barca = new Barca(relogio, PRECO_BASE);
        logger.info("Configurado novo teste com hora: {}:{} e preço base: {}", 
                   relogio.getHora(), relogio.getMinuto(), PRECO_BASE);
    }

    // 1. TESTES DE FORMATO E LIMITES DE ASSENTOS

    @Test
    @DisplayName("✓ Deve rejeitar assentos com formato inválido")
    void testAssentoFormatoInvalido() {
        logger.info("Iniciando testes de formato inválido de assentos");
        
        assertEquals(-1.0, barca.defineAssento("F1A1"), 
                    "Formato inválido 'F1A1' deve retornar código -1.0");
        assertEquals(-1.0, barca.defineAssento("F01A1"), 
                    "Formato inválido 'F01A1' deve retornar código -1.0");
        assertEquals(-1.0, barca.defineAssento("F01A001"), 
                    "Formato inválido 'F01A001' deve retornar código -1.0");
        assertEquals(-1.0, barca.defineAssento("X01A01"), 
                    "Formato inválido 'X01A01' deve retornar código -1.0");
        assertEquals(-1.0, barca.defineAssento(null), 
                    "Formato inválido 'null' deve retornar código -1.0");
        assertEquals(-1.0, barca.defineAssento(""), 
                    "Formato inválido '' deve retornar código -1.0");
        
        logger.info("Testes de formato inválido concluídos com sucesso");
    }

    @Test
    @DisplayName("✓ Deve rejeitar assentos fora dos limites permitidos")
    void testAssentoForaDosLimites() {
        logger.info("Iniciando testes de assentos fora dos limites");
        
        assertEquals(-1.0, barca.defineAssento("F00A01"), 
                    "Fileira 0 não existe e deve retornar código -1.0");
        assertEquals(-1.0, barca.defineAssento("F61A01"), 
                    "Fileira 61 não existe e deve retornar código -1.0");
        assertEquals(-1.0, barca.defineAssento("F01A00"), 
                    "Assento 0 não existe e deve retornar código -1.0");
        assertEquals(-1.0, barca.defineAssento("F01A21"), 
                    "Assento 21 não existe e deve retornar código -1.0");
        
        logger.info("Testes de assentos fora dos limites concluídos com sucesso");
    }

    // 2. TESTES DE ASSENTOS OCUPADOS

    @Test
    @DisplayName("✓ Deve rejeitar assentos já ocupados")
    void testOcupacaoAssento() {
        logger.info("Iniciando teste de ocupação de assento");
        
        double preco = barca.defineAssento("F01A01");
        logger.info("Assento F01A01 definido, preço retornado: {}", preco);
        assertTrue(preco > 0, "Primeira marcação deve retornar preço > 0");
        
        double resultado = barca.defineAssento("F01A01");
        logger.info("Tentativa de redefinir assento F01A01, resultado: {}", resultado);
        assertEquals(-2.0, resultado, "Assento já ocupado deve retornar erro -2.0");
        
        logger.info("Teste de ocupação de assento concluído com sucesso");
    }

    @Test
    @DisplayName("✓ Deve rejeitar assentos ocupados arbitrariamente")
    void testOcupacaoArbitraria() {
        logger.info("Iniciando teste de ocupação arbitrária");
        
        barca.ocupacaoArbitraria("F05A10");
        logger.info("Assento F05A10 ocupado arbitrariamente");
        
        assertEquals(-2.0, barca.defineAssento("F05A10"), 
                    "Assento ocupado arbitrariamente deve estar indisponível");
        
        logger.info("Teste de ocupação arbitrária concluído com sucesso");
    }

    // 3. TESTES DE REGRAS DE DISTRIBUIÇÃO DE PESO

    @Test
    @DisplayName("✓ Deve aplicar regras para primeiros 100 passageiros")
    void testDistribuicaoPesoPrimeiros100() {
        logger.info("Iniciando teste de distribuição de peso para primeiros 100 passageiros");
        
        // Teste para fileira válida (1-20)
        double resultado = barca.defineAssento("F01A01");
        logger.info("Primeiro passageiro na fileira 1, resultado: {}", resultado);
        assertTrue(resultado > 0, "Primeiro passageiro deve conseguir assento na fileira 1");
        
        // Teste para fileira inválida (21-60)
        resultado = barca.defineAssento("F21A01");
        logger.info("Tentativa de segundo passageiro na fileira 21, resultado: {}", resultado);
        assertEquals(-3.0, resultado, "Primeiro passageiro não deve sentar na fileira 21");
        
        logger.info("Teste de distribuição para primeiros 100 passageiros concluído");
    }

    @Test
    @DisplayName("✓ Deve aplicar regras para próximos 100 passageiros")
    void testDistribuicaoPesoProximos100() {
        logger.info("Iniciando teste de distribuição de peso para próximos 100 passageiros");
        
        // Ocupa os primeiros 99 assentos
        logger.info("Ocupando primeiros 99 assentos arbitrariamente nas fileiras 1-20");
        for (int i = 1; i <= 99; i++) {
            String fileira = String.format("%02d", (i % 20 == 0) ? 20 : i % 20);
            String assento = String.format("%02d", ((i - 1) / 20) + 1);
            barca.ocupacaoArbitraria("F" + fileira + "A" + assento);
        }
        
        // Teste para 100º passageiro na fileira válida (1-20)
        double resultado = barca.defineAssento("F01A06");
        logger.info("100º passageiro na fileira 1, resultado: {}", resultado);
        assertTrue(resultado > 0, "100º passageiro deve conseguir assento na fileira 1-20");
        
        // Teste para 101º passageiro na fileira válida (40-60)
        resultado = barca.defineAssento("F40A01");
        logger.info("101º passageiro na fileira 40, resultado: {}", resultado);
        assertTrue(resultado > 0, "101º passageiro deve conseguir assento na fileira 40-60");
        
        // Teste para 101º passageiro na fileira inválida (1-39)
        resultado = barca.defineAssento("F21A01");
        logger.info("Tentativa de 102º passageiro na fileira 21, resultado: {}", resultado);
        assertEquals(-3.0, resultado, "102º passageiro não deve sentar na fileira 21-39");
        
        logger.info("Teste de distribuição para próximos 100 passageiros concluído");
    }

    @Test
    @DisplayName("✓ Deve permitir qualquer fileira após 200 passageiros")
    void testDistribuicaoPesoDemaisPassageiros() {
        logger.info("Iniciando teste de distribuição de peso para demais passageiros (após 200)");
        
        // Ocupa os primeiros 200 assentos arbitrariamente
        logger.info("Ocupando primeiros 200 assentos arbitrariamente");
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
        
        logger.info("200 assentos ocupados. Testando regra para demais passageiros");

        // Teste para 201º passageiro em fileira anteriormente bloqueada
        double resultado = barca.defineAssento("F21A01");
        logger.info("201º passageiro na fileira 21, resultado: {}", resultado);
        assertTrue(resultado > 0, "O 201º passageiro deve poder sentar em qualquer fileira");
        
        // Teste para 202º passageiro em outra fileira anteriormente bloqueada
        resultado = barca.defineAssento("F30A01");
        logger.info("202º passageiro na fileira 30, resultado: {}", resultado);
        assertTrue(resultado > 0, "O 202º passageiro deve poder sentar em qualquer fileira");
        
        logger.info("Teste de distribuição para demais passageiros concluído com sucesso");
    }

    // 4. TESTES DE CÁLCULO DE PREÇO EM DIFERENTES HORÁRIOS

    @ParameterizedTest(name = "Horário {0}:{1} → Preço deve ser {2}")
    @CsvSource({
        "8, 0, 100.0",  // 8:00 - horário comercial
        "12, 0, 100.0", // 12:00 - horário comercial
        "14, 0, 100.0", // 14:00 - horário comercial
        "18, 0, 100.0"  // 18:00 - horário comercial
    })
    @DisplayName("📊 Preços em horário comercial padrão (preço base)")
    void testPrecoHorarioComercialPadrao(int hora, int minuto, double precoEsperado) {
        logger.info("Testando preço no horário comercial: {}:{}", hora, minuto);
        relogio.setHorario(hora, minuto);
        
        double preco = barca.defineAssento("F01A10");
        logger.info("Preço calculado: {}, esperado: {}", preco, precoEsperado);
        
        assertEquals(precoEsperado, preco, 0.01, 
                    String.format("Às %d:%02d o preço deve ser %.2f", hora, minuto, precoEsperado));
    }

    @ParameterizedTest(name = "Horário {0}:{1} → Preço deve ser {2}")
    @CsvSource({
        "12, 1, 110.0",  // 12:01 - horário de almoço
        "13, 59, 110.0", // 13:59 - horário de almoço
        "18, 1, 110.0",  // 18:01 - início da noite
        "19, 59, 110.0"  // 19:59 - início da noite
    })
    @DisplayName("📊 Preços em horário de almoço/início da noite (+10%)")
    void testPrecoHorarioAlmocoNoite(int hora, int minuto, double precoEsperado) {
        logger.info("Testando preço no horário almoço/início da noite: {}:{}", hora, minuto);
        relogio.setHorario(hora, minuto);
        
        double preco = barca.defineAssento("F01A11");
        logger.info("Preço calculado: {}, esperado: {}", preco, precoEsperado);
        
        assertEquals(precoEsperado, preco, 0.01, 
                    String.format("Às %d:%02d o preço deve ser %.2f", hora, minuto, precoEsperado));
    }

    @ParameterizedTest(name = "Horário {0}:{1} → Preço deve ser {2}")
    @CsvSource({
        "20, 0, 120.0",  // 20:00 - horário noturno
        "22, 30, 120.0", // 22:30 - horário noturno
        "23, 59, 120.0"  // 23:59 - horário noturno
    })
    @DisplayName("📊 Preços em horário noturno (+20%)")
    void testPrecoHorarioNoturno(int hora, int minuto, double precoEsperado) {
        logger.info("Testando preço no horário noturno: {}:{}", hora, minuto);
        relogio.setHorario(hora, minuto);
        
        double preco = barca.defineAssento("F01A12");
        logger.info("Preço calculado: {}, esperado: {}", preco, precoEsperado);
        
        assertEquals(precoEsperado, preco, 0.01, 
                    String.format("Às %d:%02d o preço deve ser %.2f", hora, minuto, precoEsperado));
    }

    @ParameterizedTest(name = "Horário {0}:{1} → Preço deve ser {2}")
    @CsvSource({
        "0, 0, 150.0",   // 00:00 - madrugada
        "3, 30, 150.0",  // 03:30 - madrugada
        "7, 59, 150.0"   // 07:59 - madrugada
    })
    @DisplayName("📊 Preços em horário de madrugada (+50%)")
    void testPrecoHorarioMadrugada(int hora, int minuto, double precoEsperado) {
        logger.info("Testando preço no horário de madrugada: {}:{}", hora, minuto);
        relogio.setHorario(hora, minuto);
        
        double preco = barca.defineAssento("F01A13");
        logger.info("Preço calculado: {}, esperado: {}", preco, precoEsperado);
        
        assertEquals(precoEsperado, preco, 0.01, 
                    String.format("Às %d:%02d o preço deve ser %.2f", hora, minuto, precoEsperado));
    }

    // 5. TESTES DE VALORES LIMITE PARA FAIXAS DE HORÁRIO

    @Test
    @DisplayName("⚠️ Deve calcular preço corretamente nos limites das faixas de horário")
    void testValoresLimiteHorario() {
        logger.info("Iniciando teste de valores limite entre faixas de horário");
        
        // Madrugada → Comercial
        relogio.setHorario(7, 59);
        assertEquals(PRECO_BASE * 1.5, barca.defineAssento("F02A01"), 0.01,
                    "Às 7:59 o preço deve ter aumento de 50%");
        
        relogio.setHorario(8, 0);
        assertEquals(PRECO_BASE, barca.defineAssento("F02A02"), 0.01,
                    "Às 8:00 o preço deve ser o valor base");

        // Comercial → Almoço
        relogio.setHorario(12, 0);
        assertEquals(PRECO_BASE, barca.defineAssento("F02A03"), 0.01,
                    "Às 12:00 o preço deve ser o valor base");
        
        relogio.setHorario(12, 1);
        assertEquals(PRECO_BASE * 1.1, barca.defineAssento("F02A04"), 0.01,
                    "Às 12:01 o preço deve ter aumento de 10%");

        // Almoço → Comercial
        relogio.setHorario(13, 59);
        assertEquals(PRECO_BASE * 1.1, barca.defineAssento("F02A05"), 0.01,
                    "Às 13:59 o preço deve ter aumento de 10%");
        
        relogio.setHorario(14, 0);
        assertEquals(PRECO_BASE, barca.defineAssento("F02A06"), 0.01,
                    "Às 14:00 o preço deve ser o valor base");

        // Comercial → Noite
        relogio.setHorario(18, 0);
        assertEquals(PRECO_BASE, barca.defineAssento("F02A07"), 0.01,
                    "Às 18:00 o preço deve ser o valor base");
        
        relogio.setHorario(18, 1);
        assertEquals(PRECO_BASE * 1.1, barca.defineAssento("F02A08"), 0.01,
                    "Às 18:01 o preço deve ter aumento de 10%");

        // Noite → Noturno
        relogio.setHorario(19, 59);
        assertEquals(PRECO_BASE * 1.1, barca.defineAssento("F02A09"), 0.01,
                    "Às 19:59 o preço deve ter aumento de 10%");
        
        relogio.setHorario(20, 0);
        assertEquals(PRECO_BASE * 1.2, barca.defineAssento("F02A10"), 0.01,
                    "Às 20:00 o preço deve ter aumento de 20%");

        // Noturno → Madrugada
        relogio.setHorario(23, 59);
        assertEquals(PRECO_BASE * 1.2, barca.defineAssento("F02A11"), 0.01,
                    "Às 23:59 o preço deve ter aumento de 20%");
        
        relogio.setHorario(0, 0);
        assertEquals(PRECO_BASE * 1.5, barca.defineAssento("F02A12"), 0.01,
                    "Às 0:00 o preço deve ter aumento de 50%");
        
        logger.info("Teste de valores limite de horário concluído com sucesso");
    }
}