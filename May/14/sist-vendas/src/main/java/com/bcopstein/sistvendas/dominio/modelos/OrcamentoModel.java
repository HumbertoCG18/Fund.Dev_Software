package com.bcopstein.sistvendas.dominio.modelos;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Locale; // Import Locale

public class OrcamentoModel {
    private long id;
    private List<ItemPedidoModel> itens;
    private double custoItens; 
    private double imposto;
    private double desconto;
    private double custoConsumidor; 
    private boolean efetivado;
    private String estadoCliente; 
    private double aliquotaImpostoAplicada; // Added to store the rate used

    public OrcamentoModel(long id) {
        this.id = id;
        this.itens = new LinkedList<>();
        this.efetivado = false;
    }

    public OrcamentoModel() {
        this.itens = new LinkedList<>();
        this.efetivado = false;
    }

    // Getter and Setter for estadoCliente
    public String getEstadoCliente() {
        return estadoCliente;
    }

    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }

    // Getter for aliquotaImpostoAplicada
    public double getAliquotaImpostoAplicada() {
        return aliquotaImpostoAplicada;
    }
    // No setter for aliquotaImpostoAplicada as it's internally calculated

    public void addItensPedido(PedidoModel pedido) {
        if (pedido != null && pedido.getItens() != null) {
            for (ItemPedidoModel itemPedido : pedido.getItens()) {
                if (itemPedido != null && itemPedido.getProduto() != null) {
                    this.itens.add(itemPedido);
                }
            }
        }
    }

    public List<ItemPedidoModel> getItens() {
        return this.itens; 
    }

    public boolean removeItemPorProdutoId(long produtoId) {
        if (this.isEfetivado()) {
            return false; 
        }
        int tamanhoOriginal = this.itens.size();
        this.itens = this.itens.stream()
                           .filter(item -> item.getProduto() != null && item.getProduto().getId() != produtoId)
                           .collect(Collectors.toList()); 
        
        boolean removido = this.itens.size() < tamanhoOriginal;
        if (removido) {
            recalculaTotais(); 
        }
        return removido;
    }
    
    // This method is now public to be potentially used by OrcamentoDTO if needed,
    // but it's better if OrcamentoModel stores the applied rate.
    public double calculaAliquotaPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return 0.10; 
        }
        switch (estado.trim().toUpperCase()) {
            case "RS":
                return 0.12; 
            case "SC":
                return 0.07; 
            case "PR":
                return 0.11; 
            case "SP":
                return 0.18; 
            default:
                return 0.10; 
        }
    }

    public void recalculaTotais() {
        if (this.isEfetivado()) { 
            return;
        }

        this.custoItens = 0;
        if (this.itens != null) {
             this.custoItens = this.itens.stream()
                .filter(it -> it.getProduto() != null && it.getProduto().getPrecoUnitario() >= 0 && it.getQuantidade() >= 0)
                .mapToDouble(it -> it.getProduto().getPrecoUnitario() * it.getQuantidade())
                .sum();
        }

        // Calculate and store the applicable tax rate
        this.aliquotaImpostoAplicada = calculaAliquotaPorEstado(this.estadoCliente);
        this.imposto = this.custoItens * this.aliquotaImpostoAplicada;

        if (this.itens != null && this.itens.size() > 5) { 
            this.desconto = this.custoItens * 0.05; 
        } else {
            this.desconto = 0.0;
        }
        this.custoConsumidor = this.custoItens + this.imposto - this.desconto;
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getCustoItens() { return custoItens; }
    public void setCustoItens(double custoItens) { this.custoItens = custoItens; }

    public double getImposto() { return imposto; } // This still returns the double value
    public void setImposto(double imposto) { this.imposto = imposto; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }

    public double getCustoConsumidor() { return custoConsumidor; }
    public void setCustoConsumidor(double custoConsumidor) { this.custoConsumidor = custoConsumidor; }

    public boolean isEfetivado() { return efetivado; }
    public void efetiva() { 
        if (!this.efetivado) { 
            this.efetivado = true; 
        }
    }

    @Override
    public String toString() {
        return "OrcamentoModel{" +
                "id=" + id +
                ", itens=" + (itens != null ? itens.size() : 0) + " itens" +
                ", estadoCliente='" + estadoCliente + '\'' +
                ", custoItens=" + custoItens +
                ", imposto=" + String.format(Locale.US, "%.2f (%.0f%%)", imposto, aliquotaImpostoAplicada * 100) + // Show formatted in toString too
                ", aliquotaImpostoAplicada=" + aliquotaImpostoAplicada +
                ", desconto=" + desconto +
                ", custoConsumidor=" + custoConsumidor +
                ", efetivado=" + efetivado +
                '}';
    }
}