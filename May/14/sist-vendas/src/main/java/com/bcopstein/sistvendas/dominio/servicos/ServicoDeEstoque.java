package com.bcopstein.sistvendas.dominio.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional; // Para JPA

import com.bcopstein.sistvendas.dominio.persistencia.IEstoqueRepositorio;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;
import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.modelos.ItemDeEstoqueModel;
import com.bcopstein.sistvendas.aplicacao.dtos.NovoProdutoRequestDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;

@Service
public class ServicoDeEstoque {
    private IEstoqueRepositorio estoqueRepo;
    private IProdutoRepositorio produtosRepo;
    private ServicoDeVendas servicoDeVendas; // Injetar para atualizar orçamentos

    @Autowired
    public ServicoDeEstoque(IProdutoRepositorio produtos, 
                            IEstoqueRepositorio estoque, 
                            ServicoDeVendas servicoDeVendas) { // Construtor atualizado
        this.produtosRepo = produtos;
        this.estoqueRepo = estoque;
        this.servicoDeVendas = servicoDeVendas;
    }

    public List<ProdutoModel> produtosDisponiveis() {
        return estoqueRepo.todosComEstoque();
    }

    public ProdutoModel produtoPorCodigo(long id) {
        ProdutoModel produto = this.produtosRepo.consultaPorId(id);
        // if (produto == null) {
        //     System.err.println("ServicoDeEstoque: Produto com ID " + id + " não encontrado.");
        // }
        return produto;
    }

    public int qtdadeEmEstoque(long idProduto) {
        return estoqueRepo.quantidadeEmEstoque(idProduto);
    }

    public void baixaEstoque(long idProduto, int qtdade) {
        estoqueRepo.baixaEstoque(idProduto, qtdade);
    }

    // @Transactional // Se usando JPA
    public ProdutoModel adicionarNovoProduto(NovoProdutoRequestDTO novoProdutoInfo) {
        if (novoProdutoInfo.getDescricao() == null || novoProdutoInfo.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do produto não pode ser vazia.");
        }
        if (novoProdutoInfo.getPrecoUnitario() < 0) {
            throw new IllegalArgumentException("Preço unitário do produto não pode ser negativo.");
        }
        if (novoProdutoInfo.getQuantidadeInicialEstoque() < 0) throw new IllegalArgumentException("Estoque inicial não pode ser negativo.");
        if (novoProdutoInfo.getEstoqueMin() < 0) throw new IllegalArgumentException("Estoque mínimo não pode ser negativo.");
        if (novoProdutoInfo.getEstoqueMax() < 0 || novoProdutoInfo.getEstoqueMax() < novoProdutoInfo.getEstoqueMin()) {
             throw new IllegalArgumentException("Estoque máximo inválido.");
        }

        ProdutoModel novoProduto = new ProdutoModel(0, 
                                                   novoProdutoInfo.getDescricao(), 
                                                   novoProdutoInfo.getPrecoUnitario());
        ProdutoModel produtoCadastrado = produtosRepo.cadastra(novoProduto);

        ItemDeEstoqueModel novoItemEstoque = new ItemDeEstoqueModel(
            0, 
            produtoCadastrado, 
            novoProdutoInfo.getQuantidadeInicialEstoque(),
            novoProdutoInfo.getEstoqueMin(),
            novoProdutoInfo.getEstoqueMax()
        );
        estoqueRepo.cadastraItemEstoque(novoItemEstoque);
        // System.out.println("ServicoDeEstoque: Produto e item de estoque adicionados para ID: " + produtoCadastrado.getId());
        return produtoCadastrado;
    }

    // @Transactional // Se usando JPA
    public ProdutoModel editarProduto(long produtoId, ProdutoDTO produtoEditadoInfo) {
        ProdutoModel produtoExistente = produtosRepo.consultaPorId(produtoId);
        if (produtoExistente == null) {
            throw new IllegalArgumentException("Produto com ID " + produtoId + " não encontrado para edição.");
        }
        if (produtoEditadoInfo.getDescricao() == null || produtoEditadoInfo.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do produto não pode ser vazia na edição.");
        }
        if (produtoEditadoInfo.getPrecoUnitario() < 0) {
            throw new IllegalArgumentException("Preço unitário do produto não pode ser negativo na edição.");
        }

        produtoExistente.setDescricao(produtoEditadoInfo.getDescricao());
        produtoExistente.setPrecoUnitario(produtoEditadoInfo.getPrecoUnitario());
        
        ProdutoModel produtoAtualizado = produtosRepo.atualiza(produtoExistente);
        // System.out.println("ServicoDeEstoque: Produto ID " + produtoId + " editado.");
        return produtoAtualizado;
    }

    // @Transactional // Se usando JPA
    public boolean removerProdutoCompleto(long produtoId) {
        ProdutoModel produto = produtosRepo.consultaPorId(produtoId);
        if (produto == null) {
            // System.out.println("ServicoDeEstoque: Produto ID " + produtoId + " não encontrado para remoção completa.");
            return false; 
        }

        // 1. Atualizar orçamentos ANTES de remover o produto e seu estoque,
        // pois a lógica de atualização de orçamento pode precisar consultar o preço do produto.
        // Se bem que OrcamentoModel.removeItemPorProdutoId já faz a remoção e o recalculo
        // e não depende mais do produto existir no repositório para o recalculo (usa os dados já nos itens restantes).
        servicoDeVendas.atualizarOrcamentosAposRemocaoProduto(produtoId);
        // System.out.println("ServicoDeEstoque: Solicitação de atualização de orçamentos enviada para produto ID " + produtoId + ".");
        
        // 2. Remover item do estoque
        estoqueRepo.removeItemEstoquePorProdutoId(produtoId);
        // System.out.println("ServicoDeEstoque: Item de estoque para produto ID " + produtoId + " removido (se existia).");

        // 3. Remover o produto do catálogo
        boolean produtoRemovidoFisicamente = produtosRepo.removePorId(produtoId);
        // if (!produtoRemovidoFisicamente) {
        //     System.err.println("ServicoDeEstoque: Falha ao remover produto ID " + produtoId + " do catálogo.");
        //     // Considerar o que fazer aqui. Se o produto não pôde ser removido, a operação falhou.
        //     // Se as etapas anteriores já ocorreram, pode ser necessário um rollback (complexo sem transações de BD).
        //     return false; 
        // }
        // System.out.println("ServicoDeEstoque: Produto ID " + produtoId + " removido do catálogo.");
            
        return produtoRemovidoFisicamente; // Retorna true se o produto foi removido do catálogo com sucesso
    }
}