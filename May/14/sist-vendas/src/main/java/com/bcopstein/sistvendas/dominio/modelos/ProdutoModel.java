package com.bcopstein.sistvendas.dominio.modelos;

public class ProdutoModel {
    private long id;
    private String descricao;
    private double precoUnitario;

    public ProdutoModel(long id, String descricao, double precoUnitario) {
        this.id = id;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
    }

    public long getId() {
        return this.id;
    }

    // Setter for ID - useful if ID is assigned after object creation by repository
    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    // Setter for descricao - NEEDED FOR EDIT FUNCTIONALITY
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPrecoUnitario() {
        return this.precoUnitario;
    }

    // Setter for precoUnitario - NEEDED FOR EDIT FUNCTIONALITY
    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    @Override
    public String toString() {
        return "ProdutoModel{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", precoUnitario=" + precoUnitario +
                '}';
    }
}