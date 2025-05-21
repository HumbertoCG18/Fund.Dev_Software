package com.bcopstein.sistvendas.dominio.modelos;

public class ItemDeEstoqueModel{
    private long id; // ID for the stock item itself
    private ProdutoModel produto;
    private int quantidade;
    private int estoqueMin;
    private int estoqueMax;

    public ItemDeEstoqueModel(long id, ProdutoModel produto, int quantidade, int estoqueMin, int estoqueMax) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
    }

    public long getId() {
        return id;
    }

    // Setter for ID - NEEDED BY EstoqueRepMem
    public void setId(long id) {
        this.id = id;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

    // No setter for produto, as it's fundamental to the stock item. If product changes, it's a new stock item.

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getEstoqueMin() {
        return estoqueMin;
    }

    public void setEstoqueMin(int estoqueMin) {
        this.estoqueMin = estoqueMin;
    }

    public int getEstoqueMax() {
        return estoqueMax;
    }

    public void setEstoqueMax(int estoqueMax) {
        this.estoqueMax = estoqueMax;
    }

    @Override
    public String toString() {
        return "ItemDeEstoqueModel[" +
                "id=" + id +
                ", produto=" + (produto != null ? produto.getId() : "null") +
                ", quantidade=" + quantidade +
                ", estoqueMin=" + estoqueMin +
                ", estoqueMax=" + estoqueMax +
                ']';
    }
}