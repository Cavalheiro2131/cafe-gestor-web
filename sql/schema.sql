-- CafeGestor - esquema do banco (MySQL 8)
-- Execute este script uma vez antes de rodar o sistema ou os testes com --mysql.
CREATE DATABASE IF NOT EXISTS cafe_gestor CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE cafe_gestor;

CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,          -- senha codificada (PBKDF2), nunca em texto puro
    tipo ENUM('ADMIN', 'GERENTE', 'OPERADOR') NOT NULL
);

CREATE TABLE IF NOT EXISTS funcionario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    funcao VARCHAR(50),
    contato VARCHAR(50),
    status BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS tipo_cafe (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    variedade VARCHAR(50),
    observacoes TEXT
);

CREATE TABLE IF NOT EXISTS balaio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    funcionario_id INT NOT NULL,
    tipo_cafe_id INT NOT NULL,
    FOREIGN KEY (funcionario_id) REFERENCES funcionario(id),
    FOREIGN KEY (tipo_cafe_id) REFERENCES tipo_cafe(id)
);

CREATE TABLE IF NOT EXISTS estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_cafe_id INT NOT NULL UNIQUE,
    quantidade_sacas INT NOT NULL DEFAULT 0 CHECK (quantidade_sacas >= 0),
    FOREIGN KEY (tipo_cafe_id) REFERENCES tipo_cafe(id)
);

CREATE TABLE IF NOT EXISTS movimentacao_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('ENTRADA', 'SAIDA') NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    data DATE NOT NULL,
    destino VARCHAR(100),                 -- destino da saída ou nota da entrada
    tipo_cafe_id INT NOT NULL,
    FOREIGN KEY (tipo_cafe_id) REFERENCES tipo_cafe(id)
);
