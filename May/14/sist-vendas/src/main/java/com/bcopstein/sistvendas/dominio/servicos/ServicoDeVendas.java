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
    private IOrcamentoRepositorio orcamentosRepo; // Renomeado para clareza
    private IEstoqueRepositorio estoqueRepo;   // Renomeado para clareza

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

    public OrcamentoModel criaOrcamento(PedidoModel pedido) {
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
        novoOrcamento.addItensPedido(pedido);
        novoOrcamento.recalculaTotais(); // Modelo agora recalcula seus próprios totais
        
        return this.orcamentosRepo.cadastra(novoOrcamento);
    }

    public OrcamentoModel efetivaOrcamento(long id) {
        OrcamentoModel orcamento = this.orcamentosRepo.recuperaPorId(id);
        if (orcamento == null) {
            return null;
        }
        if (orcamento.isEfetivado()) {
            return orcamento;
        }
        boolean todosDisponiveis = true;
        if(orcamento.getItens() == null || orcamento.getItens().isEmpty()){
             // Não deveria acontecer se o orçamento foi criado corretamente, mas é uma defesa.
            throw new IllegalStateException("Orçamento ID " + id + " não pode ser efetivado pois não contém itens.");
        }

        for (var item : orcamento.getItens()) {
            if (item.getProduto() == null) { // Checagem de segurança
                 todosDisponiveis = false; break;
            }
            int quantidadeEmEstoque = estoqueRepo.quantidadeEmEstoque(item.getProduto().getId());
            if (quantidadeEmEstoque < item.getQuantidade()) {
                todosDisponiveis = false; break;
            }
        }
        if (todosDisponiveis) {
            for (var item : orcamento.getItens()) {
                estoqueRepo.baixaEstoque(item.getProduto().getId(), item.getQuantidade());
            }
            orcamentosRepo.marcaComoEfetivado(orcamento.getId()); // O repositório chama orcamento.efetiva()
        } else {
            System.out.println("ServicoDeVendas: Orçamento ID " + id + " NÃO efetivado (itens indisponíveis).");
        }
        return orcamento; // Retorna o objeto orcamento (que foi modificado em memória se efetivado)
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
                    // O método removeItemPorProdutoId já chama recalculaTotais() no OrcamentoModel
                    orcamentosRepo.atualiza(orcamento); // Persiste a mudança
                    // System.out.println("ServicoDeVendas: Orçamento ID " + orcamento.getId() + " atualizado após remoção do produto ID " + produtoIdRemovido);
                }
            }
        }
    }

    public boolean removerOrcamento(long orcamentoId) {
        OrcamentoModel orcamentoExistente = orcamentosRepo.recuperaPorId(orcamentoId);
        if (orcamentoExistente == null) {
            return false; 
        }
        // Adicionar aqui validações de regra de negócio se necessário antes de remover
        // Ex: if (orcamentoExistente.isEfetivado()) { throw new IllegalStateException("Não pode remover orçamento efetivado"); }
        return orcamentosRepo.removePorId(orcamentoId);
    }
}