package com.bcopstein.sistvendas.aplicacao.casosDeUso;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcopstein.sistvendas.aplicacao.dtos.OrcamentoDTO;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IOrcamentoRepositorio;

@Component
public class TodosOrcamentosUC {
    private IOrcamentoRepositorio orcamentoRepositorio;

    @Autowired
    public TodosOrcamentosUC(IOrcamentoRepositorio orcamentoRepositorio) {
        this.orcamentoRepositorio = orcamentoRepositorio;
    }

    public List<OrcamentoDTO> run() {
        List<OrcamentoModel> orcamentos = orcamentoRepositorio.todos();
        if (orcamentos == null) {
            return List.of(); // Retorna lista vazia se o repositório retornar nulo
        }
        return orcamentos.stream()
                .map(orc -> OrcamentoDTO.fromModel(orc)) // Usa o fromModel atualizado
                .collect(Collectors.toList());
    }
}