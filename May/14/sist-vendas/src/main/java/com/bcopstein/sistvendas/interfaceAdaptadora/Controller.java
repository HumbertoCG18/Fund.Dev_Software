package com.bcopstein.sistvendas.interfaceAdaptadora;

import java.util.List;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bcopstein.sistvendas.aplicacao.casosDeUso.*; // Importa todos os UCs
import com.bcopstein.sistvendas.aplicacao.dtos.*; // Importa todos os DTOs

@RestController
public class Controller {
    private ProdutosDisponiveisUC produtosDisponiveisUC; // Renomeado para clareza
    private CriaOrcamentoUC criaOrcamentoUC;
    private EfetivaOrcamentoUC efetivaOrcamentoUC;
    private UltimosOrcamentosEfetivadosUC ultimosOrcamentosEfetivadosUC;
    private TodosOrcamentosUC todosOrcamentosUC;
    private AdicionarProdutoUC adicionarProdutoUC;
    private EditarProdutoUC editarProdutoUC;
    private RemoverOrcamentoUC removerOrcamentoUC;
    private RemoverProdutoUC removerProdutoUC; // Novo UC

    @Autowired
    public Controller(
            ProdutosDisponiveisUC produtosDisponiveisUC,
            CriaOrcamentoUC criaOrcamentoUC,
            EfetivaOrcamentoUC efetivaOrcamentoUC,
            UltimosOrcamentosEfetivadosUC ultimosOrcamentosEfetivadosUC,
            TodosOrcamentosUC todosOrcamentosUC,
            AdicionarProdutoUC adicionarProdutoUC,
            EditarProdutoUC editarProdutoUC,
            RemoverOrcamentoUC removerOrcamentoUC,
            RemoverProdutoUC removerProdutoUC) { // Injetar novo UC
        this.produtosDisponiveisUC = produtosDisponiveisUC;
        this.criaOrcamentoUC = criaOrcamentoUC;
        this.efetivaOrcamentoUC = efetivaOrcamentoUC;
        this.ultimosOrcamentosEfetivadosUC = ultimosOrcamentosEfetivadosUC;
        this.todosOrcamentosUC = todosOrcamentosUC;
        this.adicionarProdutoUC = adicionarProdutoUC;
        this.editarProdutoUC = editarProdutoUC;
        this.removerOrcamentoUC = removerOrcamentoUC;
        this.removerProdutoUC = removerProdutoUC; // Atribuir
    }

    @GetMapping("")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> welcomeMessage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/index.html"))
                .build();
    }

    // --- Endpoints de Orçamento ---
    @GetMapping("/todosOrcamentos")
    @CrossOrigin(origins = "*")
    public List<OrcamentoDTO> todosOrcamentos() {
        return todosOrcamentosUC.run();
    }

    @PostMapping("/novoOrcamento")
    @CrossOrigin(origins = "*")
    public OrcamentoDTO novoOrcamento(@RequestBody List<ItemPedidoDTO> itens) {
        try {
            return criaOrcamentoUC.run(itens);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /novoOrcamento -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar orçamento.", e);
        }
    }
    
    @GetMapping("/efetivaOrcamento/{id}")
    @CrossOrigin(origins = "*")
    public OrcamentoDTO efetivaOrcamento(@PathVariable(value = "id") long idOrcamento) {
        OrcamentoDTO orcamento = efetivaOrcamentoUC.run(idOrcamento);
        if (orcamento == null) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento ID " + idOrcamento + " não encontrado.");
        }
        // O DTO já reflete se foi efetivado ou não (devido a falta de estoque, por exemplo)
        return orcamento;
    }

    @GetMapping("/orcamentosEfetivados")
    @CrossOrigin(origins = "*")
    public List<OrcamentoDTO> ultimosOrcamentosEfetivados(@RequestParam(defaultValue = "10") int n) {
        return ultimosOrcamentosEfetivadosUC.run(n);
    }
    
    @DeleteMapping("/orcamentos/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> removerOrcamento(@PathVariable long id) {
        try {
            boolean removido = removerOrcamentoUC.run(id);
            if (removido) {
                return ResponseEntity.noContent().build();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento ID " + id + " não encontrado.");
            }
        } catch (IllegalStateException e) { 
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /orcamentos/" + id + " -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao remover orçamento.", e);
        }
    }

    // --- Endpoints de Produto ---
    @GetMapping("/produtosDisponiveis")
    @CrossOrigin(origins = "*")
    public List<ProdutoDTO> produtosDisponiveis() {
        return produtosDisponiveisUC.run();
    }

    @PostMapping("/produtos")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProdutoDTO> adicionarProduto(@RequestBody NovoProdutoRequestDTO novoProdutoDTO) {
        try {
            ProdutoDTO produtoAdicionado = adicionarProdutoUC.run(novoProdutoDTO);
            return ResponseEntity.created(URI.create("/produtos/" + produtoAdicionado.getId()))
                                 .body(produtoAdicionado);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos (POST) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao adicionar produto.", e);
        }
    }

    @PutMapping("/produtos/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProdutoDTO> editarProduto(@PathVariable long id, @RequestBody ProdutoDTO produtoDTO) {
        try {
            // Validação básica para garantir que o ID no DTO (se presente) corresponda ao ID do path
            // ou que o DTO não precise ter o ID se ele já vem do path.
            // Por simplicidade, o UC usará o ID do path.
            ProdutoDTO produtoEditado = editarProdutoUC.run(id, produtoDTO);
            if (produtoEditado == null) { // O UC pode retornar null se o produto não for encontrado pelo serviço
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto ID " + id + " não encontrado.");
            }
            return ResponseEntity.ok(produtoEditado);
        } catch (IllegalArgumentException e) { 
            // Esta exceção pode vir do serviço (ex: produto não encontrado, dados inválidos)
            // Mapear para NOT_FOUND ou BAD_REQUEST conforme o contexto da exceção.
            // Se o serviço lança IllegalArgumentException para "não encontrado", então NOT_FOUND é apropriado.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + " (PUT) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao editar produto.", e);
        }
    }

    @DeleteMapping("/produtos/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> removerProduto(@PathVariable long id) {
        try {
            boolean removido = removerProdutoUC.run(id);
            if (removido) {
                return ResponseEntity.noContent().build(); 
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto ID " + id + " não encontrado para remoção ou falha na operação.");
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + " (DELETE) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao remover produto.", e);
        }
    }
}