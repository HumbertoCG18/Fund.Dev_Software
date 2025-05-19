package com.bcopstein.sistvendas.interfaceAdaptadora;

import java.util.List;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.sistvendas.aplicacao.casosDeUso.CriaOrcamentoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.EfetivaOrcamentoUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.ProdutosDisponiveisUC;
import com.bcopstein.sistvendas.aplicacao.casosDeUso.UltimosOrcamentosEfetivadosUC;
import com.bcopstein.sistvendas.aplicacao.dtos.ItemPedidoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.aplicacao.dtos.ProdutoDTO;

@RestController
public class Controller {
    private ProdutosDisponiveisUC produtosDisponiveis;
    private CriaOrcamentoUC criaOrcamento;
    private EfetivaOrcamentoUC efetivaOrcamento;
    private UltimosOrcamentosEfetivadosUC ultimosOrcamentosEfetivados;

    @Autowired
    public Controller(ProdutosDisponiveisUC produtosDisponiveis,
            CriaOrcamentoUC criaOrcamento,
            EfetivaOrcamentoUC efetivaOrcamento,
            UltimosOrcamentosEfetivadosUC ultimosOrcamentosEfetivados) {
        this.produtosDisponiveis = produtosDisponiveis;
        this.criaOrcamento = criaOrcamento;
        this.efetivaOrcamento = efetivaOrcamento;
        this.ultimosOrcamentosEfetivados = ultimosOrcamentosEfetivados;
    }

    @GetMapping("")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> welcomeMessage() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/index.html"))
                .build();
    }

    @GetMapping("/produtosDisponiveis")
    @CrossOrigin(origins = "*")
    public List<ProdutoDTO> produtosDisponiveis() {
        return produtosDisponiveis.run();
    }

    @PostMapping("novoOrcamento")
    @CrossOrigin(origins = "*")
    public OrcamentoDTO novoOrcamento(@RequestBody List<ItemPedidoDTO> itens) {
        return criaOrcamento.run(itens);
    }
    

    @GetMapping("efetivaOrcamento/{id}")
    @CrossOrigin(origins = "*")
    public OrcamentoDTO efetivaOrcamento(@PathVariable(value = "id") long idOrcamento) {
        return efetivaOrcamento.run(idOrcamento);
    }

    @GetMapping("orcamentosEfetivados")
    @CrossOrigin(origins = "*")
    public List<OrcamentoDTO> ultimosOrcamentosEfetivados(@RequestParam(defaultValue = "10") int n) {
        return ultimosOrcamentosEfetivados.run(n);
    }
}