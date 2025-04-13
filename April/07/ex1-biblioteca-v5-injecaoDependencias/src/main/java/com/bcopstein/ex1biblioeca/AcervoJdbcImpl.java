package com.bcopstein.ex1biblioeca;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Primary
public class AcervoJdbcImpl implements IAcervoRepository {
    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Livro> livroRowMapper = (rs, rowNum) -> 
        new Livro(
            rs.getLong("codigo"),
            rs.getString("titulo"),
            rs.getString("autor"),
            rs.getInt("ano")
        );

    @Autowired
    public AcervoJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Livro> getAll() {
        return jdbcTemplate.query("SELECT * FROM livros", livroRowMapper);
    }

    @Override
    public List<String> getTitulos() {
        return jdbcTemplate.queryForList("SELECT titulo FROM livros", String.class);
    }

    @Override
    public List<String> getAutores() {
        return jdbcTemplate.queryForList("SELECT DISTINCT autor FROM livros", String.class);
    }

    @Override
    public List<Livro> getLivrosDoAutor(String autor) {
        return jdbcTemplate.query(
            "SELECT * FROM livros WHERE autor = ?", 
            livroRowMapper, 
            autor
        );
    }

    @Override
    public Livro getLivroTitulo(String titulo) {
        List<Livro> livros = jdbcTemplate.query(
            "SELECT * FROM livros WHERE titulo = ?", 
            livroRowMapper, 
            titulo
        );
        return livros.isEmpty() ? null : livros.get(0);
    }

    @Override
    @Transactional
    public boolean cadastraLivroNovo(Livro livro) {
        try {
            jdbcTemplate.update(
                "INSERT INTO livros (codigo, titulo, autor, ano) VALUES (?, ?, ?, ?)",
                livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getAno()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeLivro(long codigo) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM livros WHERE codigo = ?", codigo);
        return rowsAffected > 0;
    }

    // Additional CRUD operations
    @Transactional
    public Livro getLivroPorId(long id) {
        List<Livro> livros = jdbcTemplate.query(
            "SELECT * FROM livros WHERE codigo = ?", 
            livroRowMapper, 
            id
        );
        return livros.isEmpty() ? null : livros.get(0);
    }

    @Transactional
    public boolean atualizaLivro(Livro livro) {
        int rowsAffected = jdbcTemplate.update(
            "UPDATE livros SET titulo = ?, autor = ?, ano = ? WHERE codigo = ?",
            livro.getTitulo(), livro.getAutor(), livro.getAno(), livro.getId()
        );
        return rowsAffected > 0;
    }

    @Transactional
    public List<Livro> getLivrosPorAno(int ano) {
        return jdbcTemplate.query(
            "SELECT * FROM livros WHERE ano = ?", 
            livroRowMapper, 
            ano
        );
    }
}