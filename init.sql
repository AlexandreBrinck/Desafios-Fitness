CREATE DATABASE IF NOT EXISTS desafios_fitness
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE desafios_fitness;

-- =========================================
-- TABELA DE USUARIOS
-- =========================================

CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    login VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_usuario_login
        UNIQUE (login)
);

-- =========================================
-- TABELA DE DESAFIOS
-- Estrutura preparada para a proxima etapa (CRUD completo e participacao).
-- =========================================

CREATE TABLE desafios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    tipo_exercicio VARCHAR(100) NOT NULL,
    meta VARCHAR(100),
    data_inicio DATE NOT NULL,
    data_fim DATE,
    criador_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_desafio_criador
        FOREIGN KEY (criador_id)
        REFERENCES usuarios(id)
);

-- =========================================
-- TABELA DE PROGRESSO
-- Estrutura preparada para a proxima etapa (registro de progresso).
-- =========================================

CREATE TABLE progresso (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    desafio_id BIGINT NOT NULL,
    data_registro DATE NOT NULL,
    valor_atingido VARCHAR(100),
    observacao VARCHAR(500),

    PRIMARY KEY (id),

    CONSTRAINT fk_progresso_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_progresso_desafio
        FOREIGN KEY (desafio_id)
        REFERENCES desafios(id)
);

-- =========================================
-- DADOS PARA TESTE
-- =========================================

INSERT INTO usuarios (nome, login, senha, perfil)
VALUES
    ('Administrador do Sistema', 'admin', '123456', 'Administrador'),
    ('Joao Silva', 'joao', '123456', 'Participante'),
    ('Maria Souza', 'maria', '123456', 'Participante');
