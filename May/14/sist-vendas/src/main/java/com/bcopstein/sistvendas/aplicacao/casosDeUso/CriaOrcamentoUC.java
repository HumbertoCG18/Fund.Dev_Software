package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeVendas;

import com.bcopstein.sistvendas.aplicacao.dtos.ItemPedidoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.servicos.ServicoDeEstoque;
@Component
public class CriaOrcamentoUC {
    private ServicoDeVendas servicoDeVendas;
    private ServicoDeEstoque servicoDeEstoque;
    
    @Autowired
    public CriaOrcamentoUC(ServicoDeVendas servicoDeVendas,ServicoDeEstoque servicoDeEstoque){
        this.servicoDeVendas = servicoDeVendas;
        this.servicoDeEstoque = servicoDeEstoque;
    }

public OrcamentoDTO run(List<ItemPedidoDTO> itens){
    System.out.println("Recebendo itens para orçamento: " + itens);
    PedidoModel pedido = new PedidoModel(0);
    
    for(ItemPedidoDTO item:itens){
        System.out.println("Buscando produto com ID: " + item.getIdProduto());
        ProdutoModel produto = servicoDeEstoque.produtoPorCodigo(item.getIdProduto());
        System.out.println("Produto encontrado: " + (produto != null ? produto.getId() + " - " + produto.getDescricao() : "NULL"));
        
        if (produto == null) {
            throw new RuntimeException("Produto com ID " + item.getIdProduto() + " não encontrado");
        }
        
        ItemPedidoModel itemPedido = new ItemPedidoModel(produto, item.getQtdade());
        pedido.addItem(itemPedido);
    }
    
    System.out.println("Pedido criado com " + pedido.getItens().size() + " item(ns)");
    OrcamentoModel orcamento = servicoDeVendas.criaOrcamento(pedido);
    return OrcamentoDTO.fromModel(orcamento);
}
}
