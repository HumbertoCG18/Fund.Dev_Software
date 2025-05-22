package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.modelos.PedidoModel;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemPedidoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;

@Service
public class ServicoDeVendas {
    private IOrcamentoRepositorio orcamentosRepo;
    private IEstoqueRepositorio estoqueRepo;
    // private String estadoClienteParaNovoOrcamento; // To be used with Improvement 2

    @Autowired
    public ServicoDeVendas(IOrcamentoRepositorio orcamentos, IEstoqueRepositorio estoque) {
        this.orcamentosRepo = orcamentos;
        this.estoqueRepo = estoque;
    }

    public List<ProdutoModel> produtosDisponiveis() {
        return estoqueRepo.todosComEstoque();
    }

    public OrcamentoModel recuperaOrcamentoPorId(long id) {
        return this.orcamentosRepo.recuperaPorId(id);
    }

    // Modified for Improvement 2: Tax by Region
    public OrcamentoModel criaOrcamento(PedidoModel pedido, String estadoCliente) {
        if (pedido == null || pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido inválido: não pode ser nulo ou vazio.");
        }
        for(ItemPedidoModel item : pedido.getItens()){
            if(item.getProduto() == null){
                throw new IllegalArgumentException("Item de pedido inválido: produto não pode ser nulo.");
            }
            if(item.getQuantidade() <=0){
                throw new IllegalArgumentException("Item de pedido inválido: quantidade deve ser positiva.");
            }
        }

        var novoOrcamento = new OrcamentoModel();
        novoOrcamento.setEstadoCliente(estadoCliente); // For Improvement 2
        novoOrcamento.addItensPedido(pedido);
        novoOrcamento.recalculaTotais(); 
        
        return this.orcamentosRepo.cadastra(novoOrcamento);
    }

    public OrcamentoModel efetivaOrcamento(long id) {
        OrcamentoModel orcamento = this.orcamentosRepo.recuperaPorId(id);
        if (orcamento == null) {
            // System.err.println("ServicoDeVendas: Orçamento ID " + id + " não encontrado para efetivação.");
            return null;
        }
        if (orcamento.isEfetivado()) {
            // System.out.println("ServicoDeVendas: Orçamento ID " + id + " já está efetivado.");
            return orcamento;
        }
        
        if(orcamento.getItens() == null || orcamento.getItens().isEmpty()){
            throw new IllegalStateException("Orçamento ID " + id + " não pode ser efetivado pois não contém itens.");
        }

        boolean todosDisponiveis = true;
        for (var item : orcamento.getItens()) {
            if (item.getProduto() == null) { 
                 todosDisponiveis = false; 
                 // System.err.println("ServicoDeVendas: Item no orçamento ID " + id + " com produto nulo.");
                 break;
            }
            int quantidadeEmEstoque = estoqueRepo.quantidadeEmEstoque(item.getProduto().getId());
            if (quantidadeEmEstoque < item.getQuantidade()) {
                // System.out.println("ServicoDeVendas: Produto ID " + item.getProduto().getId() + " com estoque insuficiente ("+quantidadeEmEstoque+") para o orçamento ID " + id + " (solicitado: "+item.getQuantidade()+").");
                todosDisponiveis = false; 
                break;
            }
        }

        if (todosDisponiveis) {
            for (var item : orcamento.getItens()) {
                estoqueRepo.baixaEstoque(item.getProduto().getId(), item.getQuantidade());
            }
            orcamentosRepo.marcaComoEfetivado(orcamento.getId()); 
            // System.out.println("ServicoDeVendas: Orçamento ID " + id + " efetivado com sucesso.");
        } else {
            System.out.println("ServicoDeVendas: Orçamento ID " + id + " NÃO efetivado (itens indisponíveis ou erro).");
        }
        // o método recuperaPorId no OrcamentoRepMem já retorna o objeto com o estado atualizado (efetivado ou não)
        // Se não foi efetivado, o DTO converterá orcamento.isEfetivado() para false.
        // Se foi efetivado, orcamento.isEfetivado() será true.
        return orcamentosRepo.recuperaPorId(id); // Re-fetch to ensure the latest state, especially if `marcaComoEfetivado` modifies and saves.
                                                 // Or ensure `orcamento.efetiva()` updates the instance correctly and it's the same instance.
                                                 // Given RepMem, orcamento instance is modified directly.
    }

    public List<OrcamentoModel> ultimosOrcamentosEfetivados(int n) {
        return this.orcamentosRepo.ultimosEfetivados(n);
    }

    public void atualizarOrcamentosAposRemocaoProduto(long produtoIdRemovido) {
        List<OrcamentoModel> todosOrcamentos = orcamentosRepo.todos();
        for (OrcamentoModel orcamento : todosOrcamentos) {
            if (!orcamento.isEfetivado()) {
                boolean itemFoiRemovido = orcamento.removeItemPorProdutoId(produtoIdRemovido);
                if (itemFoiRemovido) {
                    orcamentosRepo.atualiza(orcamento);
                }
            }
        }
    }

    public boolean removerOrcamento(long orcamentoId) {
        OrcamentoModel orcamentoExistente = orcamentosRepo.recuperaPorId(orcamentoId);
        if (orcamentoExistente == null) {
            return false; 
        }
        return orcamentosRepo.removePorId(orcamentoId);
    }
}