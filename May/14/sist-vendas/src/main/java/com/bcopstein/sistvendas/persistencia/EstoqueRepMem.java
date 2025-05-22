package com.bcopstein.sistvendas.persistencia;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;

@Repository
public class EstoqueRepMem implements IEstoqueRepositorio {
    private List<ItemDeEstoqueModel> itens;
    private static long nextEstoqueItemIdCounter = 1;

    @Autowired
    public EstoqueRepMem(IProdutoRepositorio produtosRepo) {
        this.itens = new LinkedList<>();
        nextEstoqueItemIdCounter = 1;
        if (produtosRepo != null) { 
            List<ProdutoModel> todosProdutos = produtosRepo.todos();
            if (todosProdutos != null) {
                for (ProdutoModel p : todosProdutos) {
                    if (p == null) continue; 
                    int quantidade = 20; int min = 5; int max = 50;
                    if (p.getId() == 20) { quantidade = 15; }
                    if (p.getId() == 40) { quantidade = 25; min = 10; max = 100; }
                    
                    if (this.itens.stream().noneMatch(item -> item.getProduto() != null && item.getProduto().getId() == p.getId())) {
                         ItemDeEstoqueModel iem = new ItemDeEstoqueModel(nextEstoqueItemIdCounter++, p, quantidade, min, max);
                         // iem.setListado(true); // Default is true in constructor
                         this.itens.add(iem);
                    }
                }
            }
        }
    }

    @Override
    public List<ProdutoModel> todosComEstoque() { // Only listado and quantity > 0
        return itens.stream()
                .filter(it -> it.getProduto() != null && it.getQuantidade() > 0 && it.isListado())
                .map(ItemDeEstoqueModel::getProduto)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDeEstoqueModel> todosOsItensDeEstoque() { // Added
        return new LinkedList<>(this.itens); // Return a copy
    }


    @Override
    public int quantidadeEmEstoque(long idProduto) {
        // This should consider only listado products for sale purposes,
        // or it's a raw check of what's physically there.
        // For sales logic (like in ServicoDeVendas.efetivaOrcamento), 
        // it should probably only count if listado.
        // However, the current logic for efetivaOrcamento fetches product by ID,
        // then checks stock. If a product is delisted, it won't be in an new budget.
        // For existing budgets, this check is fine.
        return itens.stream()
                .filter(it -> it.getProduto() != null && it.getProduto().getId() == idProduto && it.isListado())
                .mapToInt(ItemDeEstoqueModel::getQuantidade)
                .findFirst()
                .orElse(0); 
    }

    @Override
    public void baixaEstoque(long idProduto, int qtdade) {
        ItemDeEstoqueModel item = consultaItemPorProdutoId(idProduto);
        if (item == null || !item.isListado()) { // Check if listado
            throw new IllegalArgumentException("Produto com ID " + idProduto + " não encontrado ou não listado no estoque para dar baixa.");
        }
        if (item.getQuantidade() < qtdade) {
            throw new IllegalArgumentException("Quantidade em estoque ("+item.getQuantidade()+") insuficiente para o produto ID " + idProduto + " (solicitado: "+qtdade+").");
        }
        item.setQuantidade(item.getQuantidade() - qtdade);
        // if (item.getQuantidade() < item.getEstoqueMin() && item.getQuantidade() > 0) {
        //     System.out.println("EstoqueRepMem: Atenção! Produto ID " + idProduto + " atingiu/abaixou do estoque mínimo ("+item.getEstoqueMin()+"). Qtd atual: "+item.getQuantidade());
        // }
        // if (item.getQuantidade() == 0) {
        //    System.out.println("EstoqueRepMem: Produto ID " + idProduto + " zerou o estoque.");
        // }
    }
    
    @Override
    public ItemDeEstoqueModel cadastraItemEstoque(ItemDeEstoqueModel novoItem) {
        if (novoItem == null || novoItem.getProduto() == null) {
            throw new IllegalArgumentException("Item de estoque ou seu produto associado não pode ser nulo.");
        }
        // If an item for this product exists but was delisted, this should ideally "re-list" it or update it.
        // For simplicity, we assume cadastra is for genuinely new stock items for products.
        // An "update" or "relist" operation might be more complex.
        Optional<ItemDeEstoqueModel> existente = itens.stream()
            .filter(it -> it.getProduto() != null && it.getProduto().getId() == novoItem.getProduto().getId())
            .findFirst();

        if (existente.isPresent()) {
            ItemDeEstoqueModel itemExistente = existente.get();
            itemExistente.setQuantidade(novoItem.getQuantidade()); // update quantity
            itemExistente.setEstoqueMin(novoItem.getEstoqueMin());
            itemExistente.setEstoqueMax(novoItem.getEstoqueMax());
            itemExistente.setListado(true); // Ensure it's listado
            // System.out.println("EstoqueRepMem: Item de estoque para produto ID " + novoItem.getProduto().getId() + " atualizado e listado.");
            return itemExistente;
        }

        if (novoItem.getId() == 0 || itens.stream().anyMatch(it -> it.getId() == novoItem.getId())) {
             novoItem.setId(nextEstoqueItemIdCounter++);
        } else if (novoItem.getId() >= nextEstoqueItemIdCounter) {
            nextEstoqueItemIdCounter = novoItem.getId() + 1;
        }
        novoItem.setListado(true); // Ensure new items are listado
        itens.add(novoItem);
        return novoItem;
    }
    
    @Override
    public ItemDeEstoqueModel consultaItemPorProdutoId(long codigoProduto) {
        return itens.stream()
            .filter(it -> it.getProduto() != null && it.getProduto().getId() == codigoProduto)
            .findFirst()
            .orElse(null);
    }

    @Override
    public void atualizaItemEstoque(ItemDeEstoqueModel itemEditado) {
        if(itemEditado == null || itemEditado.getProduto() == null){
             throw new IllegalArgumentException("Item de estoque ou seu produto associado não pode ser nulo para atualização.");
        }
        Optional<ItemDeEstoqueModel> itemOpt = itens.stream()
            .filter(i -> i.getId() == itemEditado.getId())
            .findFirst();
        if (itemOpt.isPresent()) {
            ItemDeEstoqueModel existente = itemOpt.get();
            if(existente.getProduto().getId() != itemEditado.getProduto().getId()){
                throw new IllegalArgumentException("Não é permitido alterar o produto associado a um item de estoque existente.");
            }
            existente.setQuantidade(itemEditado.getQuantidade());
            existente.setEstoqueMin(itemEditado.getEstoqueMin());
            existente.setEstoqueMax(itemEditado.getEstoqueMax());
            existente.setListado(itemEditado.isListado()); // Sync listado status
        } else {
            throw new IllegalArgumentException("Item de estoque com ID " + itemEditado.getId() + " não encontrado para atualização.");
        }
    }

    @Override
    public boolean delistarProdutoDeEstoque(long produtoId) { // Changed
        ItemDeEstoqueModel item = consultaItemPorProdutoId(produtoId);
        if (item != null) {
            item.setListado(false);
            // item.setQuantidade(0); // Optionally zero out stock when delisting
            // System.out.println("EstoqueRepMem: Produto ID " + produtoId + " delistado do estoque.");
            return true;
        }
        // System.out.println("EstoqueRepMem: Produto ID " + produtoId + " não encontrado para delistar.");
        return false;
    }
}