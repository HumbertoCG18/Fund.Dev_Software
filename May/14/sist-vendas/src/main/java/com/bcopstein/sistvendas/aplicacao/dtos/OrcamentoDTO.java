package com.bcopstein.sistvendas.aplicacao.dtos;

import java.util.ArrayList;
import java.util.List;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

public class OrcamentoDTO {
    private long id;
    private List<ItemPedidoDTO> itens;
    private List<Double> precosUnitariosItens;
    private double subTotal;
    private double imposto;
    private double desconto;
    private double custoConsumidor;
    private boolean efetivado;

    public OrcamentoDTO() {
        this.itens = new ArrayList<>();
        this.precosUnitariosItens = new ArrayList<>();
    }

    public OrcamentoDTO(long id, List<ItemPedidoDTO> itens, List<Double> precosUnitariosItens, double subTotal, double imposto,
                       double desconto, double custoConsumidor, boolean efetivado) {
        this.id = id;
        this.itens = itens;
        this.precosUnitariosItens = precosUnitariosItens;
        this.subTotal = subTotal;
        this.imposto = imposto;
        this.desconto = desconto;
        this.custoConsumidor = custoConsumidor;
        this.efetivado = efetivado;
    }

    public long getId() { return id; }
    public List<ItemPedidoDTO> getItens() { return itens; }
    public List<Double> getPrecosUnitariosItens() { return precosUnitariosItens; }
    public double getSubTotal() { return subTotal; }
    public double getImposto() { return imposto; }
    public double getDesconto() { return desconto; }
    public double getCustoConsumidor() { return custoConsumidor; }
    public boolean isEfetivado() { return efetivado; }

    public static OrcamentoDTO fromModel(OrcamentoModel orcamento) {
        if (orcamento == null) {
            System.err.println("OrcamentoDTO.fromModel: Tentativa de converter OrcamentoModel nulo.");
            return new OrcamentoDTO(0, new ArrayList<>(), new ArrayList<>(), 0,0,0,0, false);
        }

        List<ItemPedidoDTO> itensDTO = new ArrayList<>();
        List<Double> precosUnitarios = new ArrayList<>();

        if (orcamento.getItens() != null) {
            for (ItemPedidoModel ip : orcamento.getItens()) {
                itensDTO.add(ItemPedidoDTO.fromModel(ip)); 
                ProdutoModel produto = ip.getProduto();
                if (produto != null) {
                    precosUnitarios.add(produto.getPrecoUnitario());
                } else {
                    precosUnitarios.add(0.0); 
                    System.err.println("OrcamentoDTO.fromModel: ItemPedidoModel com ProdutoModel nulo no orçamento ID: " + orcamento.getId());
                }
            }
        }

        // System.out.println("OrcamentoDTO.fromModel: Convertendo OrcamentoModel ID: " + orcamento.getId());
        // System.out.println("  Model CustoItens (para SubTotal DTO): " + orcamento.getCustoItens());

        return new OrcamentoDTO(
                orcamento.getId(),
                itensDTO,
                precosUnitarios,
                orcamento.getCustoItens(),
                orcamento.getImposto(),
                orcamento.getDesconto(),
                orcamento.getCustoConsumidor(),
                orcamento.isEfetivado());
    }
}