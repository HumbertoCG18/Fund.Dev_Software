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
        if (produtosRepo != null) { // Adiciona verificação de nulidade
            List<ProdutoModel> todosProdutos = produtosRepo.todos();
            if (todosProdutos != null) {
                for (ProdutoModel p : todosProdutos) {
                    if (p == null) continue; // Pula produtos nulos na lista (segurança)
                    int quantidade = 20; int min = 5; int max = 50;
                    if (p.getId() == 20) { quantidade = 15; }
                    if (p.getId() == 40) { quantidade = 25; min = 10; max = 100; }
                    
                    // Evita adicionar se já existe um item de estoque para o produto
                    if (this.itens.stream().noneMatch(item -> item.getProduto() != null && item.getProduto().getId() == p.getId())) {
                         this.itens.add(new ItemDeEstoqueModel(nextEstoqueItemIdCounter++, p, quantidade, min, max));
                    }
                }
            }
        }
    }

    @Override
    public List<ProdutoModel> todosComEstoque() {
        return itens.stream()
                .filter(it -> it.getProduto() != null && it.getQuantidade() > 0)
                .map(ItemDeEstoqueModel::getProduto)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public int quantidadeEmEstoque(long idProduto) {
        return itens.stream()
                .filter(it -> it.getProduto() != null && it.getProduto().getId() == idProduto)
                .mapToInt(ItemDeEstoqueModel::getQuantidade)
                .findFirst()
                .orElse(0); 
    }

    @Override
    public void baixaEstoque(long idProduto, int qtdade) {
        ItemDeEstoqueModel item = consultaItemPorProdutoId(idProduto);
        if (item == null) {
            throw new IllegalArgumentException("Produto com ID " + idProduto + " não encontrado no estoque para dar baixa.");
        }
        if (item.getQuantidade() < qtdade) {
            throw new IllegalArgumentException("Quantidade em estoque ("+item.getQuantidade()+") insuficiente para o produto ID " + idProduto + " (solicitado: "+qtdade+").");
        }
        item.setQuantidade(item.getQuantidade() - qtdade);
    }
    
    @Override
    public ItemDeEstoqueModel cadastraItemEstoque(ItemDeEstoqueModel novoItem) {
        if (novoItem == null || novoItem.getProduto() == null) {
            throw new IllegalArgumentException("Item de estoque ou seu produto associado não pode ser nulo.");
        }
        if (itens.stream().anyMatch(it -> it.getProduto() != null && it.getProduto().getId() == novoItem.getProduto().getId())) {
            throw new IllegalArgumentException("Já existe um item de estoque para o produto ID " + novoItem.getProduto().getId());
        }
        if (novoItem.getId() == 0 || itens.stream().anyMatch(it -> it.getId() == novoItem.getId())) {
             novoItem.setId(nextEstoqueItemIdCounter++);
        } else if (novoItem.getId() >= nextEstoqueItemIdCounter) {
            nextEstoqueItemIdCounter = novoItem.getId() + 1;
        }
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
            // Valida se o produto do item a ser atualizado corresponde ao produto existente
            // (Normalmente não se muda o produto de um item de estoque, apenas seus atributos)
            if(existente.getProduto().getId() != itemEditado.getProduto().getId()){
                throw new IllegalArgumentException("Não é permitido alterar o produto associado a um item de estoque existente.");
            }
            existente.setQuantidade(itemEditado.getQuantidade());
            existente.setEstoqueMin(itemEditado.getEstoqueMin());
            existente.setEstoqueMax(itemEditado.getEstoqueMax());
        } else {
            throw new IllegalArgumentException("Item de estoque com ID " + itemEditado.getId() + " não encontrado para atualização.");
        }
    }

    @Override
    public boolean removeItemEstoquePorProdutoId(long produtoId) {
        boolean removido = itens.removeIf(item -> item.getProduto() != null && item.getProduto().getId() == produtoId);
        // if (removido) {
        //     System.out.println("EstoqueRepMem: Item(ns) de estoque para produto ID " + produtoId + " removido(s).");
        // }
        return removido;
    }
}