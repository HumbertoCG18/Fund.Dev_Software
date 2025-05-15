package com.bcopstein.ex2catalogoprodutos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogicaVenda {
    public double calculaCusto(Produto produto, int quantidade){
        double custo = produto.getPrecoUnitario() * quantidade;
        double desconto = 0;
        if (quantidade > 10){
            desconto = custo * 0.1;
        }
        double imposto = (custo - desconto) * 0.05;
        double valor_a_pagar = custo - desconto + imposto;
        return valor_a_pagar;
    }
}
