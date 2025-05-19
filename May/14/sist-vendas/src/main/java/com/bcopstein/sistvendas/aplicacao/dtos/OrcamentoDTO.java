package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.ArrayList;
import java.util.List;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

public class OrcamentoDTO {
    private long id;
    private List<ItemPedidoDTO> itens;
    private List<Double> custoItens; // Alterado para List<Double>
    private double imposto;
    private double desconto;
    private double custoConsumidor;
    private boolean efetivado;

    // Construtor padrão necessário para desserialização
    public OrcamentoDTO() {
        this.itens = new ArrayList<>();
        this.custoItens = new ArrayList<>();
    }

    // Construtor modificado para aceitar List<Double> como custoItens
    public OrcamentoDTO(long id, List<ItemPedidoDTO> itens, List<Double> custoItens, double imposto,
                       double desconto, double custoConsumidor, boolean efetivado) {
        this.id = id;
        this.itens = itens;
        this.custoItens = custoItens;
        this.imposto = imposto;
        this.desconto = desconto;
        this.custoConsumidor = custoConsumidor;
        this.efetivado = efetivado;
    }

    public long getId() {
        return id;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    // Modificado para retornar List<Double>
    public List<Double> getCustoItens() {
        return custoItens;
    }

    public double getImposto() {
        return imposto;
    }

    public double getDesconto() {
        return desconto;
    }

    public double getCustoConsumidor() {
        return custoConsumidor;
    }

    public boolean isEfetivado() {
        return efetivado;
    }

    public void efetiva() {
        efetivado = true;
    }

    public static OrcamentoDTO fromModel(OrcamentoModel orcamento) {
        List<ItemPedidoDTO> itens = new ArrayList<>(orcamento.getItens().size());
        List<Double> precosUnitarios = new ArrayList<>(orcamento.getItens().size());

        System.out.println("===== DEBUG ORÇAMENTO =====");
        System.out.println("ID do orçamento: " + orcamento.getId());
        System.out.println("Número de itens: " + orcamento.getItens().size());

        for (ItemPedidoModel ip : orcamento.getItens()) {
            // Adicionar o DTO do item
            itens.add(ItemPedidoDTO.fromModel(ip));

            // Log detalhado do item e produto
            ProdutoModel produto = ip.getProduto();
            if (produto != null) {
                System.out.println("Item: ProdutoID=" + produto.getId()
                        + ", Descrição=" + produto.getDescricao()
                        + ", PreçoUnitário=" + produto.getPrecoUnitario()
                        + ", Quantidade=" + ip.getQuantidade());

                // Adicionar o preço unitário à lista
                precosUnitarios.add(produto.getPrecoUnitario());
            } else {
                System.out.println("ERRO: Item com produto NULL!");
                precosUnitarios.add(0.0); // Adiciona zero se não houver produto
            }
        }

        System.out.println("PreçosUnitários: " + precosUnitarios);
        System.out.println("=========================");

        return new OrcamentoDTO(
                orcamento.getId(),
                itens,
                precosUnitarios,
                orcamento.getImposto(),
                orcamento.getDesconto(),
                orcamento.getCustoConsumidor(),
                orcamento.isEfetivado());
    }
}