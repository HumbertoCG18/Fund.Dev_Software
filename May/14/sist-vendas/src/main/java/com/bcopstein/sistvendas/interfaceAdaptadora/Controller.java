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

import com.bcopstein.sistvendas.aplicacao.casosDeUso.*; 
import com.bcopstein.sistvendas.aplicacao.dtos.*; 

@RestController
public class Controller {
    private ProdutosDisponiveisUC produtosDisponiveisUC;
    private TodosProdutosStatusUC todosProdutosStatusUC; // Added
    private CriaOrcamentoUC criaOrcamentoUC;
    private EfetivaOrcamentoUC efetivaOrcamentoUC;
    private UltimosOrcamentosEfetivadosUC ultimosOrcamentosEfetivadosUC;
    private TodosOrcamentosUC todosOrcamentosUC;
    private AdicionarProdutoUC adicionarProdutoUC;
    private EditarProdutoUC editarProdutoUC;
    private RemoverOrcamentoUC removerOrcamentoUC;
    private DesativarProdutoUC desativarProdutoUC; // Changed from RemoverProdutoUC

    @Autowired
    public Controller(
            ProdutosDisponiveisUC produtosDisponiveisUC,
            TodosProdutosStatusUC todosProdutosStatusUC, // Added
            CriaOrcamentoUC criaOrcamentoUC,
            EfetivaOrcamentoUC efetivaOrcamentoUC,
            UltimosOrcamentosEfetivadosUC ultimosOrcamentosEfetivadosUC,
            TodosOrcamentosUC todosOrcamentosUC,
            AdicionarProdutoUC adicionarProdutoUC,
            EditarProdutoUC editarProdutoUC,
            RemoverOrcamentoUC removerOrcamentoUC,
            DesativarProdutoUC desativarProdutoUC // Changed
            ) {
        this.produtosDisponiveisUC = produtosDisponiveisUC;
        this.todosProdutosStatusUC = todosProdutosStatusUC; // Added
        this.criaOrcamentoUC = criaOrcamentoUC;
        this.efetivaOrcamentoUC = efetivaOrcamentoUC;
        this.ultimosOrcamentosEfetivadosUC = ultimosOrcamentosEfetivadosUC;
        this.todosOrcamentosUC = todosOrcamentosUC;
        this.adicionarProdutoUC = adicionarProdutoUC;
        this.editarProdutoUC = editarProdutoUC;
        this.removerOrcamentoUC = removerOrcamentoUC;
        this.desativarProdutoUC = desativarProdutoUC; // Changed
    }

    @GetMapping("")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> welcomeMessage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/welcome.html")) // Changed to welcome.html as per files
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
    // Changed to use NovoOrcamentoRequestDTO
    public OrcamentoDTO novoOrcamento(@RequestBody NovoOrcamentoRequestDTO request) { 
        try {
            return criaOrcamentoUC.run(request); // Pass the whole request DTO
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (RuntimeException e) { // Catch specific runtime from UC like "Produto não encontrado"
             System.err.println("Controller Erro: /novoOrcamento -> " + e.getMessage());
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
         catch (Exception e) {
            System.err.println("Controller Erro: /novoOrcamento -> " + e.getMessage());
            e.printStackTrace(); // Good for debugging server-side
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar orçamento.", e);
        }
    }
    
    @GetMapping("/efetivaOrcamento/{id}")
    @CrossOrigin(origins = "*")
    public OrcamentoDTO efetivaOrcamento(@PathVariable(value = "id") long idOrcamento) {
        try {
            OrcamentoDTO orcamento = efetivaOrcamentoUC.run(idOrcamento);
            if (orcamento == null) { // Should mean "not found"
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento ID " + idOrcamento + " não encontrado.");
            }
            // If orcamento.isEfetivado() is false after the call, it means stock was insufficient
            // The client can check this flag. HTTP 200 is okay, body contains outcome.
            return orcamento;
        } catch (IllegalStateException e) { // e.g. orçamento sem itens
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
         catch (Exception e) {
            System.err.println("Controller Erro: /efetivaOrcamento/" + idOrcamento + " -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao efetivar orçamento.", e);
        }
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
    @GetMapping("/produtosDisponiveis") // This lists products for adding to budget (listado and stock > 0)
    @CrossOrigin(origins = "*")
    public List<ProdutoDTO> produtosDisponiveis() { // Returns basic ProdutoDTO
        return produtosDisponiveisUC.run();
    }

    @GetMapping("/todosProdutosStatus") // New endpoint for product management UI
    @CrossOrigin(origins = "*")
    public List<ProdutoEstoqueDTO> todosProdutosStatus() { // Returns enhanced ProdutoEstoqueDTO
        return todosProdutosStatusUC.run();
    }

    @PostMapping("/produtos")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ProdutoDTO> adicionarProduto(@RequestBody NovoProdutoRequestDTO novoProdutoDTO) {
        // Returns basic ProdutoDTO
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
        // Returns basic ProdutoDTO
        try {
            ProdutoDTO produtoEditado = editarProdutoUC.run(id, produtoDTO);
            if (produtoEditado == null) { 
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto ID " + id + " não encontrado.");
            }
            return ResponseEntity.ok(produtoEditado);
        } catch (IllegalArgumentException e) { 
             // Assuming service throws IllegalArgumentException for "not found" or bad data
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                 throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + " (PUT) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao editar produto.", e);
        }
    }

    @DeleteMapping("/produtos/{id}") // This now de-lists the product
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> desativarProduto(@PathVariable long id) {
        try {
            boolean desativado = desativarProdutoUC.run(id); // Changed UC
            if (desativado) {
                return ResponseEntity.noContent().build(); 
            } else {
                 // Could be product not found, or already delisted by some logic.
                 // Service should return false if product ID itself doesn't exist in produtosRepo for consistency.
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto ID " + id + " não encontrado ou falha ao desativar.");
            }
        } catch (IllegalArgumentException e) { // Should not happen if ID is just long
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Controller Erro: /produtos/" + id + " (DELETE) -> " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao desativar produto.", e);
        }
    }
}