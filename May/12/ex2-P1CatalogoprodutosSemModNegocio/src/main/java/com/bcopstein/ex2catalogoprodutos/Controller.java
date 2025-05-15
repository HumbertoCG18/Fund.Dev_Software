package com.bcopstein.ex2catalogoprodutos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private ProdutoDAO produtoDAO;
    private LogicaVenda logicaVenda;

    @Autowired
    public Controller(ProdutoDAO produtoDAO, LogicaVenda logicaVenda) {
        this.produtoDAO = produtoDAO;
        this.logicaVenda = logicaVenda;
    }

    @GetMapping("")
    @CrossOrigin(origins = "*")
    public String welcomeMessage() {
        return ("Bem vindo as lojas ACME");
    }

    @GetMapping("produtosDisponiveis")
    @CrossOrigin(origins = "*")
    public List<Produto> produtosDisponiveis() {
        return produtoDAO.getProdutosDisponiveis();
    }

    @GetMapping("venda/codigo/{codigo}/quantidade/{quantidade}")
    @CrossOrigin(origins = "*")
    public double venda(@PathVariable(value = "codigo") int codigo,
                        @PathVariable(value = "quantidade") int quantidade) {
        // Recupera o produto
        Produto produto = produtoDAO.buscaPorCodig(codigo);
        // Verifica se o produto existe
        if (produto == null) {
            return -1;
        }
        // Verifica se tem quantidade suficiente
        int novaQuantidade = produto.getQtdadeEstoque() - quantidade;
        if (novaQuantidade <= 0) {
            return -1;
        }
        // Atualiza a quantidade no estoque
        produtoDAO.atualizaQuantidade(codigo, novaQuantidade);
        // Calcula o valor da venda
        double valor = logicaVenda.calculaCusto(produto, quantidade);
        return valor;
    }

    @GetMapping("entradaNoEstoque/codigo/{codigo}/quantidade/{quantidade}")
    @CrossOrigin(origins = "*")
    public void entradaNoEstoque(@PathVariable(value = "codigo") int codigo,
                                 @PathVariable(value = "quantidade") int quantidade) {
        // Recupera o produto
        Produto produto = produtoDAO.buscaPorCodig(codigo);
        // Verifica se o produto existe
        if (produto == null) {
            return;
        }
        // Calcula a nova quantidade e atualiza o estoque
        int novaQuantidade = produto.getQtdadeEstoque() + quantidade;
        produtoDAO.atualizaQuantidade(codigo, novaQuantidade);
    }

    @GetMapping("comprasNecessarias")
    @CrossOrigin(origins = "*")
    public List<Produto> comprasNecessarias() {
        return produtoDAO.comprasNecessarias();
    }
}
