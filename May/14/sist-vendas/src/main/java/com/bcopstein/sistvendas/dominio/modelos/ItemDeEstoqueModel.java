package com.bcopstein.sistvendas.dominio.modelos;

public class ItemDeEstoqueModel{
    private long id; 
    private ProdutoModel produto;
    private int quantidade;
    private int estoqueMin;
    private int estoqueMax;
    private boolean listado = true; // Added for soft delete/listing control

    public ItemDeEstoqueModel(long id, ProdutoModel produto, int quantidade, int estoqueMin, int estoqueMax) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMin = estoqueMin;
        this.estoqueMax = estoqueMax;
        this.listado = true; // Default to true
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProdutoModel getProduto() {
        return produto;
    }

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

    public boolean isListado() { // Added getter
        return listado;
    }

    public void setListado(boolean listado) { // Added setter
        this.listado = listado;
    }

    @Override
    public String toString() {
        return "ItemDeEstoqueModel[" +
                "id=" + id +
                ", produto=" + (produto != null ? produto.getId() : "null") +
                ", quantidade=" + quantidade +
                ", estoqueMin=" + estoqueMin +
                ", estoqueMax=" + estoqueMax +
                ", listado=" + listado + // Added to toString
                ']';
    }
}