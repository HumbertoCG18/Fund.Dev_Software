package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

@Service
public class ServicoDeEstoque {
    private IEstoqueRepositorio estoque;
    private IProdutoRepositorio produtos;

    @Autowired
    public ServicoDeEstoque(IProdutoRepositorio produtos, IEstoqueRepositorio estoque) {
        this.produtos = produtos;
        this.estoque = estoque;
    }

    public List<ProdutoModel> produtosDisponiveis() {
        return estoque.todosComEstoque();
    }

    public ProdutoModel produtoPorCodigo(long id) {
        ProdutoModel produto = this.produtos.consultaPorId(id);
        if (produto == null) {
            throw new IllegalArgumentException("Produto com ID " + id + " não encontrado");
        }
        return produto;
    }

    public int qtdadeEmEstoque(long id) {
        return estoque.quantidadeEmEstoque(id);
    }

    public void baixaEstoque(long id, int qtdade) {
        estoque.baixaEstoque(id, qtdade);
    }
}
