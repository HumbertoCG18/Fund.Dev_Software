# Relatório Detalhado do Projeto

Este relatório apresenta todo o processo de desenvolvimento e teste do Sistema Barca, seguindo os passos especificados.

## 1. Análise de Requisitos

Nesta fase, analisamos detalhadamente os requisitos do sistema:

- Controle de assentos por identificador (formato específico)
- Verificação da distribuição de peso na embarcação
- Cálculo de preços com base no horário
- Regras específicas de ocupação de assentos

## 2. Projeto das Classes

A partir dos requisitos, projetamos as seguintes classes:

- `Barca`: Classe principal responsável pelo gerenciamento da embarcação
- `Assento`: Representa um assento da barca com suas propriedades
- `Passageiro`: Contém informações sobre o passageiro

## 3. Projeto dos Casos de Teste

Utilizamos diversas técnicas para projetar os casos de teste:

- **Particionamento de Equivalência**: Dividimos os dados de entrada em classes válidas e inválidas
- **Análise de Valor Limite**: Testamos valores nos limites das condições
- **Testes de Causa-Efeito**: Exploramos combinações de diferentes condições

### Casos de Teste Projetados

| ID | Descrição | Técnica | Entrada | Saída Esperada |
|----|-----------|---------|---------|----------------|
| CT01 | Teste de formato de assento válido | Particionamento | "A01" | true |
| CT02 | Teste de formato de assento inválido | Particionamento | "Z99" | false |
| CT03 | Teste de limite de peso | Valor Limite | 550kg | false |
| CT04 | Teste de preço em horário de pico | Causa-Efeito | 18:00, "A01" | R$ 17,00 |

## 4. Implementação das Classes

Implementamos as classes conforme o projeto:

```java
public class Barca {
    private Map<String, Assento> assentos;
    private double pesoTotal;
    
    public Barca() {
        this.assentos = new HashMap<>();
        this.pesoTotal = 0;
        inicializarAssentos();
    }
    
    // Resto da implementação...
}