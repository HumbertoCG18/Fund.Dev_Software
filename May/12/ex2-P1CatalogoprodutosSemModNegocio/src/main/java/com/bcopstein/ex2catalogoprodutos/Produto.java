package com.bcopstein.ex2catalogoprodutos;

public class Produto {
    private long codigo;
    private String descricao;
    private double precoUnitario;
    private int qtdadeEstoque;
    
    public Produto(long codigo, String descricao, double precoUnitario, int qtdadeEstoque) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.qtdadeEstoque = qtdadeEstoque;
    }

    public long getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public int getQtdadeEstoque() {
        return qtdadeEstoque;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public void setQtdadeEstoque(int qtdadeEstoque) {
        this.qtdadeEstoque = qtdadeEstoque;
    }

    @Override
    public String toString() {
        return "Produto [codigo=" + codigo + ", descricao=" + descricao + ", precoUnitario=" + precoUnitario
                + ", qtdadeEstoque=" + qtdadeEstoque + "]";
    } 
}
