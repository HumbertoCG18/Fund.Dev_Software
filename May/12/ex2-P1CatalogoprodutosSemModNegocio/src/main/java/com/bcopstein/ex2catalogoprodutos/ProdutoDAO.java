package com.bcopstein.ex2catalogoprodutos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProdutoDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ProdutoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Produto> getProdutosDisponiveis() {
        List<Produto> resp = this.jdbcTemplate.query("SELECT * from produtos where qtdadeEstoque > 0",
                (rs, rowNum) -> new Produto(rs.getLong("codigo"),
                                            rs.getString("descricao"), 
                                            rs.getDouble("precoUnitario"),
                                            rs.getInt("qtdadeEstoque")));
        return resp;
    }

    public Produto buscaPorCodig(long codigo){
        List<Produto> resp = this.jdbcTemplate.query("SELECT * from produtos where codigo="+codigo,
                (rs, rowNum) -> new Produto(rs.getLong("codigo"),
                                            rs.getString("descricao"), 
                                            rs.getDouble("precoUnitario"),
                                            rs.getInt("qtdadeEstoque")));
        if (resp.size() == 0){
            return null;
        }else{
            return resp.get(0);
        }
    }

    public boolean atualizaQuantidade(long codigo,int novaQuantidade){
        this.jdbcTemplate.update(
            "UPDATE produtos SET qtdadeEstoque = "+novaQuantidade+" where codigo = "+codigo+";");
        return true;
    }

    public List<Produto> comprasNecessarias(){
        List<Produto> resp = this.jdbcTemplate.query("SELECT * from produtos where qtdadeEstoque < 2",
        (rs, rowNum) -> new Produto(rs.getLong("codigo"),
                                    rs.getString("descricao"), 
                                    rs.getDouble("precoUnitario"),
                                    rs.getInt("qtdadeEstoque")));       
        return resp; 
    }
/* 
    // Melhorar as consultas que seguem !!
    // Colocar as queries em JDBC
    @Override
    public List<String> getTitulos() {
        return getAll()
                .stream()
                .map(livro -> livro.getTitulo())
                .toList();
    }

    @Override
    public List<String> getAutores() {
        return getAll()
                .stream()
                .map(livro -> livro.getAutor())
                .toList();
    }

    @Override
    public List<Livro> getLivrosDoAutor(String autor) {
        return getAll()
                .stream()
                .filter(livro -> livro.getAutor().equals(autor))
                .toList();
    }

    @Override
    public Livro getLivroTitulo(String titulo) {
        return getAll()
                .stream()
                .filter(livro -> livro.getTitulo().equals(titulo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean removeLivro(long codigo) {
        String sql = "DELETE FROM livros WHERE id = " + codigo;
        this.jdbcTemplate.batchUpdate(sql);
        return true;
    }

    @Override
    public boolean cadastraLivroNovo(Livro livro) {
        this.jdbcTemplate.update(
                "INSERT INTO livros(codigo,titulo,autor,ano) VALUES (?,?,?,?)",
                livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getAno());
        return true;
    }
*/
}