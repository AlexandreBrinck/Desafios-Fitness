CREATE DATABASE IF NOT EXISTS desafios_fitness
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE desafios_fitness;

-- =========================================
-- USUARIO DE APLICACAO (usado pelo MysqlSingleton)
-- =========================================

CREATE USER IF NOT EXISTS 'desafios_user'@'localhost'
IDENTIFIED BY 'desafios123';

GRANT ALL PRIVILEGES ON desafios_fitness.* TO 'desafios_user'@'localhost';

FLUSH PRIVILEGES;

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
-- TABELA DE PARTICIPACAO
-- Relacionamento N:N entre usuarios e desafios.
-- Um usuario nao pode participar duas vezes do mesmo desafio (uk_participacao).
-- Se o desafio for excluido, as participacoes relacionadas saem junto (cascade).
-- =========================================

CREATE TABLE participacoes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    desafio_id BIGINT NOT NULL,
    data_participacao DATE NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_participacao
        UNIQUE (usuario_id, desafio_id),

    CONSTRAINT fk_participacao_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_participacao_desafio
        FOREIGN KEY (desafio_id)
        REFERENCES desafios(id)
        ON DELETE CASCADE
);

-- =========================================
-- TABELA DE PROGRESSO
-- Se o desafio for excluido, os registros de progresso relacionados
-- saem junto (cascade), para nao deixar referencias orfas no banco.
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
        ON DELETE CASCADE
);

-- =========================================
-- DADOS PARA TESTE
-- =========================================

INSERT INTO usuarios (nome, login, senha, perfil)
VALUES
    ('Administrador do Sistema', 'admin', '123456', 'Administrador'),
    ('Joao Silva', 'joao', '123456', 'Participante'),
    ('Maria Souza', 'maria', '123456', 'Participante');

INSERT INTO desafios (nome, descricao, tipo_exercicio, meta, data_inicio, data_fim, criador_id)
VALUES
    ('Corrida de 50km no mes', 'Desafio para acumular 50km correndo ao longo do mes.',
     'Corrida', '50 km', '2026-09-01', '2026-09-30', 1),
    ('30 dias de musculacao', 'Treino de musculacao pelo menos 4 vezes por semana.',
     'Musculacao', '4 treinos/semana', '2026-09-01', '2026-09-30', 2);

INSERT INTO participacoes (usuario_id, desafio_id, data_participacao)
VALUES
    (2, 1, '2026-09-01'),
    (3, 1, '2026-09-02');

INSERT INTO progresso (usuario_id, desafio_id, data_registro, valor_atingido, observacao)
VALUES
    (2, 1, '2026-09-05', '10 km', 'Primeira semana, ritmo confortavel.'),
    (2, 1, '2026-09-12', '22 km', 'Acumulado da segunda semana.');
