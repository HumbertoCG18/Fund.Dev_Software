package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.aplicacao.dtos.NovoProdutoRequestDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO; // Keep for editing payload
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoEstoqueDTO; // New DTO for list

@Service
public class ServicoDeEstoque {
    private IEstoqueRepositorio estoqueRepo;
    private IProdutoRepositorio produtosRepo;
    private ServicoDeVendas servicoDeVendas; 

    @Autowired
    public ServicoDeEstoque(IProdutoRepositorio produtos, 
                            IEstoqueRepositorio estoque, 
                            ServicoDeVendas servicoDeVendas) { 
        this.produtosRepo = produtos;
        this.estoqueRepo = estoque;
        this.servicoDeVendas = servicoDeVendas;
    }

    public List<ProdutoModel> produtosDisponiveis() { // Used for creating budgets
        return estoqueRepo.todosComEstoque();
    }

    // New method to get all products with their full stock status for management UI
    public List<ProdutoEstoqueDTO> getTodosProdutosComStatusEstoque() {
        List<ProdutoModel> todosProdutosCatalogados = produtosRepo.todos();
        return todosProdutosCatalogados.stream()
            .map(produto -> {
                ItemDeEstoqueModel itemEstoque = estoqueRepo.consultaItemPorProdutoId(produto.getId());
                if (itemEstoque == null) {
                    // This case implies a product exists in catalog but not in stock ledger
                    // Create a "default" or "unmanaged" stock item representation for DTO
                    // Or, ensure every product always has an ItemDeEstoqueModel.
                    // For now, let's assume if no ItemDeEstoqueModel, it's effectively unlistado and 0 quantity.
                    // This might happen if a product was added to ProdutoRepMem but not EstoqueRepMem init.
                    // System.err.println("ServicoDeEstoque: Produto ID " + produto.getId() + " não possui ItemDeEstoqueModel correspondente.");
                    // To prevent nulls in DTO construction for such cases:
                    ItemDeEstoqueModel dummyItem = new ItemDeEstoqueModel(0, produto, 0, 0, 0);
                    dummyItem.setListado(false); // Treat as unlistado if no stock record
                    return ProdutoEstoqueDTO.fromModels(produto, dummyItem);

                }
                return ProdutoEstoqueDTO.fromModels(produto, itemEstoque);
            })
            .collect(Collectors.toList());
    }


    public ProdutoModel produtoPorCodigo(long id) {
        ProdutoModel produto = this.produtosRepo.consultaPorId(id);
         if (produto == null) {
             System.err.println("ServicoDeEstoque: Produto com ID " + id + " não encontrado.");
         }
        return produto;
    }

    public int qtdadeEmEstoque(long idProduto) {
        return estoqueRepo.quantidadeEmEstoque(idProduto);
    }

    public void baixaEstoque(long idProduto, int qtdade) {
        estoqueRepo.baixaEstoque(idProduto, qtdade);
    }

    public ProdutoModel adicionarNovoProduto(NovoProdutoRequestDTO novoProdutoInfo) {
        // ... (validations as before)
        ProdutoModel novoProduto = new ProdutoModel(0, 
            novoProdutoInfo.getDescricao(), 
            novoProdutoInfo.getPrecoUnitario());
        ProdutoModel produtoCadastrado = produtosRepo.cadastra(novoProduto);

        ItemDeEstoqueModel novoItemEstoque = new ItemDeEstoqueModel(
            0, 
            produtoCadastrado, 
            novoProdutoInfo.getQuantidadeInicialEstoque(),
            novoProdutoInfo.getEstoqueMin(),
            novoProdutoInfo.getEstoqueMax()
        );
        // novoItemEstoque.setListado(true); // Already default in constructor
        estoqueRepo.cadastraItemEstoque(novoItemEstoque);
        return produtoCadastrado;
    }

    public ProdutoModel editarProduto(long produtoId, ProdutoDTO produtoEditadoInfo) {
        // ... (validations as before)
        // Here, ProdutoDTO is used as a payload for basic product properties.
        // Stock properties (quantity, min, max, listado) are edited via different means if needed
        // or this method could be expanded if ProdutoDTO carried those for editing too.
        // For now, it only edits description and price of ProdutoModel.
        ProdutoModel produtoExistente = produtosRepo.consultaPorId(produtoId);
        if (produtoExistente == null) {
            throw new IllegalArgumentException("Produto com ID " + produtoId + " não encontrado para edição.");
        }
        // Further check: if the product is associated with an ItemDeEstoqueModel that is !listado,
        // should editing basic properties be allowed? For now, yes.
        
        produtoExistente.setDescricao(produtoEditadoInfo.getDescricao());
        produtoExistente.setPrecoUnitario(produtoEditadoInfo.getPrecoUnitario());
        
        ProdutoModel produtoAtualizado = produtosRepo.atualiza(produtoExistente);
        // If price changes, non-efetivado orcamentos might need re-evaluation.
        // This is a complex side effect not explicitly requested to handle here.
        return produtoAtualizado;
    }

    // Renamed from removerProdutoCompleto
    public boolean desativarProduto(long produtoId) {
        ProdutoModel produto = produtosRepo.consultaPorId(produtoId);
        if (produto == null) {
            return false; 
        }
        servicoDeVendas.atualizarOrcamentosAposRemocaoProduto(produtoId);
        
        boolean delistado = estoqueRepo.delistarProdutoDeEstoque(produtoId);
        // The ProdutoModel itself is NOT removed from produtosRepo.
        // It's only marked as not listado in the stock.
        
        // if (delistado) {
        //     System.out.println("ServicoDeEstoque: Produto ID " + produtoId + " desativado (delistado do estoque).");
        // } else {
        //     System.out.println("ServicoDeEstoque: Falha ao desativar produto ID " + produtoId + " (não encontrado no estoque para delistar).");
        // }
        return delistado;
    }
}