package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;
import com.bcopstein.sistvendas.dominio.modelos.OrcamentoModel;

public interface IOrcamentoRepositorio {
    List<OrcamentoModel> todos();

    OrcamentoModel recuperaPorId(long id);

    OrcamentoModel cadastra(OrcamentoModel orcamento);

    void marcaComoEfetivado(long id);

    List<OrcamentoModel> ultimosEfetivados(int n);
}