package com.bcopstein.sistvendas.persistencia;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;

@Repository
public class ProdutoRepMem implements IProdutoRepositorio {
    private List<ProdutoModel> produtos;
    private static long nextProdIdCounter; // Static counter for new product IDs

    public ProdutoRepMem() {
        produtos = new LinkedList<>();
        produtos.add(new ProdutoModel(10, "Televisor", 2000.0));
        produtos.add(new ProdutoModel(20, "Geladeira", 3500.0));
        produtos.add(new ProdutoModel(30, "Fogao", 3600.0));
        produtos.add(new ProdutoModel(40, "Lava-louça", 1800.0));
        produtos.add(new ProdutoModel(50, "Lava-roupas", 2870.0));
        // Initialize counter based on existing max ID
        nextProdIdCounter = produtos.stream().mapToLong(ProdutoModel::getId).max().orElse(0) + 1;
    }

    @Override
    public List<ProdutoModel> todos() {
        return new LinkedList<>(produtos); // Return a copy
    }

    @Override
    public ProdutoModel consultaPorId(long id) {
        return produtos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ProdutoModel cadastra(ProdutoModel novoProduto) {
        if (novoProduto == null) {
            throw new IllegalArgumentException("Produto para cadastro não pode ser nulo.");
        }
        // Assign new ID if not provided or if it conflicts
        if (novoProduto.getId() == 0 || produtos.stream().anyMatch(p -> p.getId() == novoProduto.getId())) {
            novoProduto.setId(nextProdIdCounter++);
        } else if (novoProduto.getId() >= nextProdIdCounter) {
             // Adjust counter if a higher ID is manually set
            nextProdIdCounter = novoProduto.getId() + 1;
        }
        produtos.add(novoProduto);
        System.out.println("ProdutoRepMem: Produto cadastrado: ID=" + novoProduto.getId() + ", Desc=" + novoProduto.getDescricao());
        return novoProduto;
    }

    @Override
    public ProdutoModel atualiza(ProdutoModel produtoEditado) {
        if (produtoEditado == null) {
            throw new IllegalArgumentException("Produto para atualização não pode ser nulo.");
        }
        Optional<ProdutoModel> produtoOpt = produtos.stream()
                .filter(p -> p.getId() == produtoEditado.getId())
                .findFirst();
        if (produtoOpt.isPresent()) {
            ProdutoModel produtoExistente = produtoOpt.get();
            produtoExistente.setDescricao(produtoEditado.getDescricao());
            produtoExistente.setPrecoUnitario(produtoEditado.getPrecoUnitario());
            System.out.println("ProdutoRepMem: Produto atualizado: ID=" + produtoExistente.getId());
            return produtoExistente;
        }
        System.out.println("ProdutoRepMem: Produto ID " + produtoEditado.getId() + " não encontrado para atualização.");
        return null; // Or throw exception
    }

    @Override
    public boolean removePorId(long id) {
        // Adicionando a implementação que faltava.
        // CUIDADO: Remover um produto pode ter implicações em estoque, orçamentos existentes, etc.
        // Esta é uma remoção simples da lista.
        boolean removido = produtos.removeIf(p -> p.getId() == id);
        if (removido) {
            System.out.println("ProdutoRepMem: Produto ID " + id + " removido.");
            // Aqui você também precisaria remover o item de estoque associado
            // Isso exigiria injetar IEstoqueRepositorio ou ter um ServicoDeDominio para coordenar.
            // Por ora, a remoção do produto é isolada.
        } else {
            System.out.println("ProdutoRepMem: Produto ID " + id + " não encontrado para remoção.");
        }
        return removido;
    }
}