# Desafios Fitness

Aplicativo web para criação de desafios de exercícios, participação em desafios e
registro de progresso. Trabalho da disciplina de Aplicações para Internet, desenvolvido
em dupla, usando Java, arquitetura MVC, DAO e Service, com banco de dados MySQL.

> **Este projeto está sendo desenvolvido em etapas.** Este README descreve o estado
> atual do repositório ao final da **primeira etapa**. A segunda etapa (CRUD completo
> de Desafios, participação e registro de Progresso) será desenvolvida pelo segundo
> integrante da dupla, no mesmo repositório.

## Objetivo

Permitir que usuários:

- criem desafios de exercícios;
- visualizem e participem de desafios existentes;
- registrem seu progresso nos desafios.

## Tecnologias

- Java 21
- Jakarta Servlet 6.1 (Tomcat 10.1)
- JSP + JSTL (sem scriptlets, exceto o redirect de `index.jsp`)
- MySQL 8.4 (via `mysql-connector-j`)
- Maven (empacotamento WAR)
- Docker / docker-compose (opcional, para subir MySQL + Tomcat)

## Arquitetura

MVC clássico com as camadas Controller (Servlet) → Service (regra de negócio) → DAO
(acesso ao banco) → Model, e rotas por parâmetro (`?acao=...`), sem `pathInfo`/`switch`
como mecanismo de roteamento.

```
src/main/java/br/com/desafiosfitness/
├── config/      MysqlSingleton (conexão única com o MySQL)
├── controller/  Servlets (BaseServlet + um por entidade)
├── dao/         Acesso ao banco (MysqlDAO base + um DAO por entidade)
├── filter/      Filtros de servlet (ex.: codificação UTF-8)
├── model/       Entidades (Mapeavel, Usuario, Desafio, Progresso)
└── service/     Regras de negócio (um Service por entidade)

src/main/webapp/
├── WEB-INF/jsp/          Views (JSP + JSTL/EL)
├── css/estilo.css        Layout
└── index.jsp             Redireciona para /home
```

### Mapeamento automático (Mapeavel / mapearSimples)

Em vez de cada DAO escrever um método `mapear(ResultSet)` manual e repetitivo, os
Models implementam a interface `Mapeavel`:

```java
public interface Mapeavel {
    void preencher(ResultSet rs) throws SQLException;
}
```

E o `MysqlDAO` (classe base de todos os DAOs) oferece um método genérico que usa
reflection para instanciar o Model e delegar o preenchimento a ele:

```java
protected <T extends Mapeavel> T mapearSimples(ResultSet rs, Class<T> classe) {
    T objeto = classe.getDeclaredConstructor().newInstance();
    objeto.preencher(rs);
    return objeto;
}
```

Assim, `UsuarioDAO` e `DesafioDAO` só escrevem SQL — a conversão `ResultSet -> Model`
fica a cargo do próprio Model.

## Entidades

| Entidade  | Campos                                                                                   |
|-----------|-------------------------------------------------------------------------------------------|
| Usuario   | id, nome, login, senha, perfil                                                            |
| Desafio   | id, nome, descricao, tipo_exercicio, meta, data_inicio, data_fim, criador_id (FK usuario) |
| Progresso | id, usuario_id (FK), desafio_id (FK), data_registro, valor_atingido, observacao           |

## Banco de dados

O script [`init.sql`](init.sql) cria o banco `desafios_fitness`, as três tabelas acima
(com chaves primárias e estrangeiras) e alguns usuários de exemplo:

| login | senha  | perfil          |
|-------|--------|-----------------|
| admin | 123456 | Administrador   |
| joao  | 123456 | Participante    |
| maria | 123456 | Participante    |

A conexão é feita pelo Singleton `MysqlSingleton`
(`src/main/java/br/com/desafiosfitness/config/MysqlSingleton.java`), com a URL, usuário
e senha configurados como constantes no topo da classe:

```java
URL      = jdbc:mysql://mysql:3306/desafios_fitness?...
USER     = desafios_user
PASSWORD = desafios123
```

O host `mysql` é o nome do serviço no `docker-compose.yml`. **Se for rodar o Tomcat
fora do Docker** (apontando para um MySQL local), troque `mysql` por `localhost` nessa
URL antes de compilar.

## Como executar

### Opção 1 — Docker (recomendado)

1. Gere o WAR: `mvn clean package` (o WAR é gerado em `deploy/desafios-fitness.war`,
   pasta já mapeada pelo `docker-compose.yml`).
2. Suba os containers: `docker compose up -d`.
3. Acesse: [http://localhost:8080/desafios-fitness](http://localhost:8080/desafios-fitness)

O MySQL sobe na porta `3306` e o `init.sql` é executado automaticamente na primeira
inicialização do container (criação do banco, tabelas e usuários de exemplo).

### Opção 2 — MySQL e Tomcat instalados localmente

1. Crie o banco rodando o script `init.sql` num MySQL 8.4 local (ex.: `mysql -u root -p < init.sql`).
2. Ajuste a URL em `MysqlSingleton.java` para usar `localhost` no lugar de `mysql`.
3. Gere o WAR: `mvn clean package`.
4. Copie `deploy/desafios-fitness.war` para a pasta `webapps` do Tomcat 10.1.
5. Inicie o Tomcat e acesse `http://localhost:8080/desafios-fitness`.

## Rotas implementadas nesta etapa

| Rota                      | Método | Ação                              |
|---------------------------|--------|-----------------------------------|
| `/home`                   | GET    | Painel inicial                    |
| `/usuarios`               | GET    | Lista os usuários cadastrados     |
| `/usuarios?acao=novo`     | GET    | Formulário de cadastro de usuário |
| `/usuarios`               | POST   | Salva um novo usuário             |
| `/desafios`               | GET    | Lista desafios (estrutura inicial) |

## Funcionalidades já implementadas (1ª etapa)

- Estrutura completa do projeto em camadas MVC/DAO/Service.
- Maven configurado para Java 21 / Tomcat 10.1 / MySQL 8.4, gerando WAR.
- `init.sql` com as três tabelas (`usuarios`, `desafios`, `progresso`) e FKs.
- `MysqlSingleton` para conexão única com o MySQL.
- `Mapeavel` + `mapearSimples` (mapeamento genérico via reflection).
- `Usuario`: Model, DAO e Service completos (incluindo validação de login único,
  tamanho mínimo de senha e os métodos de alteração/exclusão já implementados no
  DAO/Service, prontos para quando o Controller/telas forem estendidos).
- Cadastro e listagem de usuários funcionando de ponta a ponta (Controller → Service →
  DAO → banco → JSP).
- `Desafio`: Model, DAO (listar/buscar) e Service iniciais, com relacionamento
  (`criador_id`) para `Usuario`, prontos para a próxima etapa.
- `Progresso`: Model e tabela no banco, preparados para a próxima etapa.
- Layout base (CSS), navegação (Home / Usuários / Desafios) e página de Desafios
  indicando que a funcionalidade está em construção.
- Filtro de codificação UTF-8 aplicado a toda a aplicação.

## Funcionalidades da próxima etapa (segundo integrante da dupla)

- CRUD completo de Desafios (criar, editar, excluir), usando o `DesafioDAO` e o
  `DesafioService` já criados como ponto de partida.
- Participação de usuários em desafios (tabela de relacionamento, se necessário).
- Registro de progresso: DAO, Service, Controller e telas para a entidade `Progresso`
  (Model e tabela já existem).
- Telas de edição e exclusão de usuários (os métodos `alterar`/`deletar` já existem em
  `UsuarioDAO`/`UsuarioService`, faltando apenas expor as rotas/telas no Controller).
- Regras de negócio mais completas de participação (ex.: impedir duplicidade,
  encerramento automático por data).

## Verificação realizada nesta etapa

- `mvn clean package` executado com sucesso, gerando `deploy/desafios-fitness.war`.
- Conferência manual de que as colunas usadas nas consultas SQL batem com as tabelas
  do `init.sql`.
- **Observação:** não foi possível validar a aplicação rodando de ponta a ponta em um
  container Docker/MySQL real neste ambiente (Docker não disponível). Recomenda-se
  rodar `docker compose up -d` e testar o cadastro/listagem de usuários no navegador
  antes da entrega.
