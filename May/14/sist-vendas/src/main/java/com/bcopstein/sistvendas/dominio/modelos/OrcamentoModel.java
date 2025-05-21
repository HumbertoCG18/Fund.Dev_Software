package com.bcopstein.sistvendas.dominio.modelos;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class OrcamentoModel {
    private long id;
    private List<ItemPedidoModel> itens;
    private double custoItens; // Subtotal dos itens
    private double imposto;
    private double desconto;
    private double custoConsumidor; // Custo final para o consumidor
    private boolean efetivado;

    public OrcamentoModel(long id) {
        this.id = id;
        this.itens = new LinkedList<>(); // Usar LinkedList para remoção eficiente de itens
        this.efetivado = false;
    }

    public OrcamentoModel() {
        this.itens = new LinkedList<>();
        this.efetivado = false;
    }

    public void addItensPedido(PedidoModel pedido) {
        if (pedido != null && pedido.getItens() != null) {
            for (ItemPedidoModel itemPedido : pedido.getItens()) {
                if (itemPedido != null && itemPedido.getProduto() != null) {
                    // Evitar adicionar o mesmo produto múltiplas vezes como itens separados,
                    // a menos que a intenção seja ter linhas distintas para o mesmo produto (raro em orçamentos).
                    // Para simplificar, apenas adicionamos. A lógica de agrupar seria mais complexa.
                    this.itens.add(itemPedido);
                }
            }
        }
    }

    public List<ItemPedidoModel> getItens() {
        return this.itens; // Retorna a lista original para permitir modificações internas (como remoção)
    }

    public boolean removeItemPorProdutoId(long produtoId) {
        if (this.isEfetivado()) {
            // System.out.println("OrcamentoModel: Não é possível remover itens de um orçamento efetivado ID: " + this.id);
            return false; // Regra de negócio: não alterar orçamentos efetivados
        }
        int tamanhoOriginal = this.itens.size();
        this.itens = this.itens.stream()
                           .filter(item -> item.getProduto() != null && item.getProduto().getId() != produtoId)
                           .collect(Collectors.toList()); // Recria a lista sem o item
        
        boolean removido = this.itens.size() < tamanhoOriginal;
        if (removido) {
            // System.out.println("OrcamentoModel: Item com produto ID " + produtoId + " removido do orçamento ID: " + this.id);
            recalculaTotais(); // Importante recalcular após a remoção
        }
        return removido;
    }

    public void recalculaTotais() {
        if (this.isEfetivado()) {
            // System.out.println("OrcamentoModel: Não é possível recalcular totais de um orçamento efetivado ID: " + this.id);
            return; 
        }

        this.custoItens = 0;
        if (this.itens != null) {
             this.custoItens = this.itens.stream()
                .filter(it -> it.getProduto() != null && it.getProduto().getPrecoUnitario() >= 0 && it.getQuantidade() >= 0)
                .mapToDouble(it -> it.getProduto().getPrecoUnitario() * it.getQuantidade())
                .sum();
        }

        this.imposto = this.custoItens * 0.1; // Exemplo: 10% de imposto

        if (this.itens != null && this.itens.size() > 5) {
            this.desconto = this.custoItens * 0.05; // Exemplo: 5% de desconto para mais de 5 tipos de itens
        } else {
            this.desconto = 0.0;
        }
        this.custoConsumidor = this.custoItens + this.imposto - this.desconto;
        // System.out.println("OrcamentoModel ID " + this.id + " totais recalculados: Subtotal=" + this.custoItens + ", Imposto=" + this.imposto + ", Desconto=" + this.desconto + ", Total=" + this.custoConsumidor);
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getCustoItens() { return custoItens; }
    public void setCustoItens(double custoItens) { this.custoItens = custoItens; }

    public double getImposto() { return imposto; }
    public void setImposto(double imposto) { this.imposto = imposto; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }

    public double getCustoConsumidor() { return custoConsumidor; }
    public void setCustoConsumidor(double custoConsumidor) { this.custoConsumidor = custoConsumidor; }

    public boolean isEfetivado() { return efetivado; }
    public void efetiva() { this.efetivado = true; }

    @Override
    public String toString() {
        return "OrcamentoModel{" +
                "id=" + id +
                ", itens=" + (itens != null ? itens.size() : 0) + " itens" +
                ", custoItens=" + custoItens +
                ", imposto=" + imposto +
                ", desconto=" + desconto +
                ", custoConsumidor=" + custoConsumidor +
                ", efetivado=" + efetivado +
                '}';
    }
}