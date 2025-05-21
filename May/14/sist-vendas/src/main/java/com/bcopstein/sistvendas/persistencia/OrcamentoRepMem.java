package com.bcopstein.sistvendas.persistencia;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;

@Repository
public class OrcamentoRepMem implements IOrcamentoRepositorio {
    private static int idCount = 1; 
    private List<OrcamentoModel> orcamentos;

    @Autowired
    public OrcamentoRepMem(IProdutoRepositorio produtosRepo) {
        this.orcamentos = new LinkedList<>();
        idCount = 1; 

        // Exemplo de orçamento 1 
        OrcamentoModel orc1 = new OrcamentoModel(); 
        PedidoModel ped1 = new PedidoModel(0);
        ProdutoModel p10 = produtosRepo.consultaPorId(10);
        ProdutoModel p20 = produtosRepo.consultaPorId(20);
        if(p10 != null) ped1.addItem(new ItemPedidoModel(p10, 2));
        if(p20 != null) ped1.addItem(new ItemPedidoModel(p20, 3));
        orc1.addItensPedido(ped1);
        orc1.recalculaTotais(); // Calcula totais ao criar
        this.cadastra(orc1); 

        // Exemplo de orçamento 2
        OrcamentoModel orc2 = new OrcamentoModel();
        PedidoModel ped2 = new PedidoModel(0);
        ProdutoModel p40 = produtosRepo.consultaPorId(40);
        ProdutoModel p50 = produtosRepo.consultaPorId(50);
        if(p40 != null) ped2.addItem(new ItemPedidoModel(p40, 1));
        if(p50 != null) ped2.addItem(new ItemPedidoModel(p50, 2));
        if(p20 != null) ped2.addItem(new ItemPedidoModel(p20, 1)); 
        orc2.addItensPedido(ped2);
        orc2.recalculaTotais(); // Calcula totais ao criar
        this.cadastra(orc2);
    }

    @Override
    public List<OrcamentoModel> todos() {
        return new LinkedList<>(orcamentos);
    }

    @Override
    public OrcamentoModel cadastra(OrcamentoModel orcamento) {
        if (orcamento == null) throw new IllegalArgumentException("Orçamento não pode ser nulo.");
        // Remove se já existe para evitar duplicatas e permitir que cadastra funcione como "salvar ou atualizar"
        orcamentos.removeIf(o -> o.getId() == orcamento.getId() && orcamento.getId() != 0);

        if (orcamento.getId() == 0) { // Se for um novo orçamento (ID 0)
            orcamento.setId(idCount++);
        } else { // Se um ID foi fornecido (potencialmente uma atualização através de cadastra)
            if (orcamento.getId() >= idCount) { // Atualiza o contador se necessário
                idCount = (int) orcamento.getId() + 1;
            }
        }
        orcamentos.add(orcamento);
        // System.out.println("OrcamentoRepMem: Orçamento ID " + orcamento.getId() + " cadastrado/atualizado.");
        return orcamento;
    }
    
    @Override
    public OrcamentoModel atualiza(OrcamentoModel orcamentoAtualizado) {
        if (orcamentoAtualizado == null) {
            throw new IllegalArgumentException("Orçamento para atualização não pode ser nulo.");
        }
        Optional<OrcamentoModel> orcOpt = orcamentos.stream()
            .filter(o -> o.getId() == orcamentoAtualizado.getId())
            .findFirst();
        
        if (orcOpt.isPresent()) {
            orcamentos.remove(orcOpt.get()); // Remove o antigo
            orcamentos.add(orcamentoAtualizado); // Adiciona o novo (atualizado)
            // System.out.println("OrcamentoRepMem: Orçamento ID " + orcamentoAtualizado.getId() + " atualizado.");
            return orcamentoAtualizado;
        }
        // Se não encontrar, pode ser uma política lançar erro ou cadastrar como novo
        throw new IllegalArgumentException("Orçamento com ID " + orcamentoAtualizado.getId() + " não encontrado para atualização.");
    }


    @Override
    public OrcamentoModel recuperaPorId(long id) {
        return orcamentos.stream()
                .filter(or -> or.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void marcaComoEfetivado(long id) {
        OrcamentoModel orcamento = recuperaPorId(id);
        if (orcamento == null) {
            throw new IllegalArgumentException("Orçamento com ID " + id + " não encontrado para marcar como efetivado.");
        }
        if (orcamento.isEfetivado()){
            return;
        }
        orcamento.efetiva(); 
    }

    @Override
    public List<OrcamentoModel> ultimosEfetivados(int n) {
        return orcamentos.stream()
                .filter(OrcamentoModel::isEfetivado) 
                .sorted(Comparator.comparing(OrcamentoModel::getId).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public boolean removePorId(long id) {
        OrcamentoModel orcamento = recuperaPorId(id);
        if (orcamento != null) {
            if (orcamento.isEfetivado()) {
                // System.out.println("OrcamentoRepMem: Aviso - Removendo orçamento EFETIVADO ID " + id);
            }
            return orcamentos.removeIf(o -> o.getId() == id);
        }
        return false;
    }
}