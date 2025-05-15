package com.bcopstein.sistvendas.persistencia;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.bcopstein.sistvendas.dominio.modelos.ProdutoModel;
import com.bcopstein.sistvendas.dominio.persistencia.IProdutoRepositorio;

@Repository
public class ProdutoRepMem  implements IProdutoRepositorio{
    private List<ProdutoModel> produtos;

    public ProdutoRepMem(){
        produtos = new LinkedList<>();
        produtos.add(new ProdutoModel(10,"Televisor",2000.0));
        produtos.add(new ProdutoModel(20,"Geladeira",3500.0));
        produtos.add(new ProdutoModel(30,"Fogao",1200.0));
        produtos.add(new ProdutoModel(40,"Lava-louça",1800.0));
        produtos.add(new ProdutoModel(50,"lava-roupas",2870.0));
    }

    public List<ProdutoModel> todos(){
        return produtos;
    }

    public ProdutoModel consultaPorId(long id){
        return produtos.stream()
            .filter(p->p.getId() == id)
            .findFirst()
            .orElse(null);
    }
}
