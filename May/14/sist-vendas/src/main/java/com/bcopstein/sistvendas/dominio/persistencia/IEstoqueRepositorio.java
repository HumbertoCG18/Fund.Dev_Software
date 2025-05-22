package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;

public interface IEstoqueRepositorio {
    List<ProdutoModel> todosComEstoque(); // Returns products that are listado and quantity > 0
    List<ItemDeEstoqueModel> todosOsItensDeEstoque(); // Added to get all items for status view
    int quantidadeEmEstoque(long codigoProduto);
    void baixaEstoque(long codProd, int qtdade);
    ItemDeEstoqueModel cadastraItemEstoque(ItemDeEstoqueModel novoItem); 
    ItemDeEstoqueModel consultaItemPorProdutoId(long codigoProduto); 
    void atualizaItemEstoque(ItemDeEstoqueModel itemEditado); 
    boolean delistarProdutoDeEstoque(long produtoId); // Changed from removeItemEstoquePorProdutoId
}