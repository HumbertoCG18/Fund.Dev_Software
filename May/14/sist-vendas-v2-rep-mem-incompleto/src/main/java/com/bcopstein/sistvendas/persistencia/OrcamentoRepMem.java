package com.bcopstein.sistvendas.persistencia;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;

@Repository
public class OrcamentoRepMem implements IOrcamentoRepositorio{
    private static int idCount = 1;
    private List<OrcamentoModel> orcamentos;

    @Autowired
    public OrcamentoRepMem(IProdutoRepositorio produtos){
        this.orcamentos = new LinkedList<>();

        // Cria Orcamento
        OrcamentoModel orcamento = new OrcamentoModel(1);
        PedidoModel pedido = new PedidoModel(1);
        ProdutoModel p = produtos.consultaPorId(10);
        ItemPedidoModel item = new ItemPedidoModel(p, 2);
        pedido.addItem(item);
        p = produtos.consultaPorId(20);
        item = new ItemPedidoModel(p, 3);
        pedido.addItem(item);
        orcamento.addItensPedido(pedido);
        orcamentos.add(orcamento);

        // Cria Orcamento
        orcamento = new OrcamentoModel(2);
        pedido = new PedidoModel(2);
        p = produtos.consultaPorId(40);
        item = new ItemPedidoModel(p,1);
        pedido.addItem(item);
        p = produtos.consultaPorId(50);
        item = new ItemPedidoModel(p, 2);
        pedido.addItem(item);
        p = produtos.consultaPorId(20);
        item = new ItemPedidoModel(p, 1);
        pedido.addItem(item);
        orcamento.addItensPedido(pedido);
        orcamentos.add(orcamento);

        // Ajusta contador de orcamentos
        OrcamentoRepMem.idCount += 2;
    }

    @Override
    public List<OrcamentoModel> todos() {
        return orcamentos;
    }

    @Override
    public OrcamentoModel cadastra(OrcamentoModel orcamento) {
        if (orcamento.getId() == 0){
            orcamento.setId(idCount++);
        }
        orcamentos.add(orcamento);
        return orcamento;
    }

    @Override
    public OrcamentoModel recuperaPorId(long id) {
        return orcamentos.stream()
            .filter(or->or.getId() == id)
            .findAny()
            .orElse(null);
    }

    @Override
    public void marcaComoEfetivado(long id) {
        OrcamentoModel orcamento = recuperaPorId(id);
        if (orcamento == null){
            throw new IllegalArgumentException("Orcamento não encontrado");
        }
        orcamento.efetiva();
    }
}
