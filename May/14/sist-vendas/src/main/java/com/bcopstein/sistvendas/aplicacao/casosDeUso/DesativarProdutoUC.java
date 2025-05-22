package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DesativarProdutoUC { // Renamed from RemoverProdutoUC
    private ServicoDeEstoque servicoDeEstoque;

    @Autowired
    public DesativarProdutoUC(ServicoDeEstoque servicoDeEstoque) {
        this.servicoDeEstoque = servicoDeEstoque;
    }

    public boolean run(long produtoId) {
        return servicoDeEstoque.desativarProduto(produtoId); // Calls new service method
    }
}