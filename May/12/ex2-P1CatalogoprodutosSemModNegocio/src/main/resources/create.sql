DROP TABLE produtos IF EXISTS;
CREATE TABLE produtos (codigo long,descricao VARCHAR(255),precoUnitario float,qtdadeEstoque int,PRIMARY KEY(codigo));
