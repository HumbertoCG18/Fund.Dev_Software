package com.bcopstein.sistvendas.dominio.persistencia;

import java.util.List;

import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;

public interface IProdutoRepositorio {
    List<ProdutoModel> todos();
    ProdutoModel consultaPorId(long codigo);
}
