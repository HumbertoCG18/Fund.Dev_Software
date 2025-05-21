package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;

public interface IEstoqueRepositorio {
    List<ProdutoModel> todosComEstoque();
    int quantidadeEmEstoque(long codigoProduto);
    void baixaEstoque(long codProd, int qtdade);
    ItemDeEstoqueModel cadastraItemEstoque(ItemDeEstoqueModel novoItem); 
    ItemDeEstoqueModel consultaItemPorProdutoId(long codigoProduto); 
    void atualizaItemEstoque(ItemDeEstoqueModel itemEditado); 
    boolean removeItemEstoquePorProdutoId(long produtoId); // Remove item de estoque pelo ID do produto
}