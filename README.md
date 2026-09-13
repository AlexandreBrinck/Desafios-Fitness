# Desafios Fitness

Aplicativo web para criação de desafios de exercícios, participação em desafios e
registro/acompanhamento de progresso. Trabalho da disciplina de Aplicações para
Internet, desenvolvido em dupla, usando Java, arquitetura MVC, DAO e Service, com
banco de dados MySQL.

> **Este projeto foi desenvolvido em duas etapas, por dois integrantes da dupla, no
> mesmo repositório.** A primeira etapa entregou a base do projeto (estrutura MVC,
> conexão com o banco, cadastro/listagem de usuários e a estrutura inicial de
> Desafios). A segunda etapa — descrita neste README — completou o sistema: CRUD
> completo de Desafios, participação de usuários em desafios, login simples e o
> sistema de Progresso (registrar, listar, editar, excluir e consultar por desafio).

## Objetivo

Permitir que usuários:

- criem desafios de exercícios;
- visualizem desafios existentes;
- participem de desafios;
- registrem seus progressos;
- acompanhem seus progressos.

## Tecnologias

- Java 21
- Jakarta Servlet 6.1 (Tomcat 10.1)
- JSP + JSTL/EL (sem scriptlets, exceto o redirect de `index.jsp`)
- MySQL 8.4 (via `mysql-connector-j`)
- Maven (empacotamento WAR)
- HTML/CSS + um pouco de JavaScript (`confirm()` nos links de exclusão)
- Docker / docker-compose (opcional, para subir MySQL + Tomcat)

## Arquitetura

MVC clássico com as camadas Controller (Servlet) → Service (regra de negócio) → DAO
(acesso ao banco) → Model, e rotas por parâmetro (`?acao=...`), sem `pathInfo`/`switch`
como mecanismo de roteamento.

```
src/main/java/br/com/desafiosfitness/
├── config/      MysqlSingleton (conexão única com o MySQL)
├── controller/  Servlets (BaseServlet + um por entidade/funcionalidade)
├── dao/         Acesso ao banco (MysqlDAO base + um DAO por entidade)
├── filter/      Filtros de servlet (codificação UTF-8 e exigência de login)
├── model/       Entidades (Mapeavel, Usuario, Desafio, Participacao, Progresso)
└── service/     Regras de negócio (um Service por entidade)

src/main/webapp/
├── WEB-INF/jsp/
│   ├── home.jsp, login.jsp
│   ├── usuarios/       lista.jsp, form.jsp
│   ├── desafios/       lista.jsp, form.jsp, visualizar.jsp
│   └── progresso/      lista.jsp, form.jsp
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

Assim, nenhum DAO (`UsuarioDAO`, `DesafioDAO`, `ParticipacaoDAO`, `ProgressoDAO`)
precisa de um método `mapear` manual — a conversão `ResultSet -> Model` fica a cargo
do próprio Model, inclusive em consultas com `JOIN` (ex.: `ParticipacaoDAO` reaproveita
`mapearSimples` para montar `Desafio` e `Usuario` a partir de consultas com `INNER JOIN`).

## Login

Um sistema de login simples (baseado em sessão, sem criptografia de senha — adequado
ao escopo da disciplina) identifica "quem" está criando um desafio, participando ou
registrando progresso:

- `LoginServlet`/`LogoutServlet` autenticam via `UsuarioService.autenticar(login, senha)`
  e guardam o usuário logado na sessão (`usuarioLogado`).
- `AuthFilter` exige login apenas para a área de **Progresso** (`/progresso`,
  `/progresso/*`), pois todo registro pertence a um usuário.
- Em **Desafios**, listar e visualizar continuam públicos; criar, editar, excluir e
  participar exigem login (verificado dentro do próprio `DesafioServlet`, já que a
  mesma rota `/desafios` atende ações públicas e privadas).

## Entidades

| Entidade      | Campos                                                                                     |
|---------------|---------------------------------------------------------------------------------------------|
| Usuario       | id, nome, login, senha, perfil                                                              |
| Desafio       | id, nome, descricao, tipo_exercicio, meta, data_inicio, data_fim, criador_id (FK usuario)   |
| Participacao  | id, usuario_id (FK), desafio_id (FK), data_participacao — relacionamento N:N               |
| Progresso     | id, usuario_id (FK), desafio_id (FK), data_registro, valor_atingido, observacao             |

## Banco de dados

O script [`init.sql`](init.sql) cria o banco `desafios_fitness` e as quatro tabelas
acima, com chaves primárias, estrangeiras e restrições:

- `uk_usuario_login`: login de usuário é único;
- `uk_participacao`: um usuário não pode participar duas vezes do mesmo desafio;
- as FKs de `participacoes.desafio_id` e `progresso.desafio_id` usam
  **`ON DELETE CASCADE`** — ao excluir um desafio, suas participações e registros de
  progresso são removidos automaticamente pelo próprio banco, sem deixar referências
  órfãs.

Usuários de exemplo:

| login | senha  | perfil          |
|-------|--------|-----------------|
| admin | 123456 | Administrador   |
| joao  | 123456 | Participante    |
| maria | 123456 | Participante    |

O script também cadastra dois desafios de exemplo (criados por `admin` e `joao`), com
`joao` e `maria` participando do primeiro e alguns registros de progresso, para
facilitar os testes manuais sem precisar cadastrar tudo do zero.

A conexão é feita pelo Singleton `MysqlSingleton`
(`src/main/java/br/com/desafiosfitness/config/MysqlSingleton.java`), com a URL, usuário
e senha configurados como constantes no topo da classe:

```java
URL      = jdbc:mysql://localhost:3306/desafios_fitness?...
USER     = desafios_user
PASSWORD = desafios123
```

Por padrão o projeto aponta para `localhost`, para rodar com um MySQL/MariaDB e um
Tomcat instalados diretamente no Windows, sem Docker. **Se preferir rodar via Docker**
(usando o `docker-compose.yml` incluído no projeto), troque `localhost` por `mysql`
nessa URL antes de compilar — esse é o nome do serviço MySQL no `docker-compose.yml`.

## Como executar

### Opção 1 — MySQL e Tomcat instalados localmente (padrão atual)

1. Crie o banco rodando o script `init.sql` num MySQL/MariaDB local
   (ex.: `mysql -u root < init.sql`).
2. Crie o usuário do banco usado pela aplicação (ou ajuste `MysqlSingleton.java` para
   usar um usuário já existente):
   ```sql
   CREATE USER 'desafios_user'@'localhost' IDENTIFIED BY 'desafios123';
   GRANT ALL PRIVILEGES ON desafios_fitness.* TO 'desafios_user'@'localhost';
   ```
3. Gere o WAR: `mvn clean package`.
4. Copie `deploy/desafios-fitness.war` para a pasta `webapps` do Tomcat 10.1.
5. Inicie o Tomcat e acesse `http://localhost:8080/desafios-fitness`.

### Opção 2 — Docker

1. Ajuste a URL em `MysqlSingleton.java` para usar `mysql` no lugar de `localhost`
   (nome do serviço MySQL no `docker-compose.yml`).
2. Gere o WAR: `mvn clean package` (o WAR é gerado em `deploy/desafios-fitness.war`,
   pasta já mapeada pelo `docker-compose.yml`).
3. Suba os containers: `docker compose up -d`.
4. Acesse: [http://localhost:8080/desafios-fitness](http://localhost:8080/desafios-fitness)

O MySQL sobe na porta `3306` e o `init.sql` é executado automaticamente na primeira
inicialização do container (criação do banco, tabelas e dados de exemplo).

## Rotas

| Rota                                          | Método | Ação                                                          | Login |
|------------------------------------------------|--------|----------------------------------------------------------------|:-----:|
| `/home`                                         | GET    | Painel inicial                                                  |  não  |
| `/login`                                        | GET/POST | Formulário e autenticação                                     |  não  |
| `/logout`                                       | GET    | Encerra a sessão                                                |  sim  |
| `/usuarios`                                     | GET    | Lista os usuários cadastrados                                   |  não  |
| `/usuarios?acao=novo`                           | GET    | Formulário de cadastro de usuário                               |  não  |
| `/usuarios`                                     | POST   | Salva um novo usuário                                           |  não  |
| `/desafios`                                     | GET    | Lista todos os desafios                                         |  não  |
| `/desafios?acao=visualizar&id=`                 | GET    | Detalhes do desafio, participantes e ações disponíveis          |  não  |
| `/desafios?acao=meus`                           | GET    | Desafios dos quais o usuário logado participa                   |  sim  |
| `/desafios?acao=novo`                           | GET    | Formulário de novo desafio                                       |  sim  |
| `/desafios?acao=editar&id=`                     | GET    | Formulário de edição (somente o criador)                        |  sim  |
| `/desafios`                                     | POST   | Salva (cria ou edita) um desafio                                 |  sim  |
| `/desafios?acao=excluir&id=`                    | GET    | Exclui o desafio (somente o criador; cascade no banco)           |  sim  |
| `/desafios?acao=participar&id=`                 | GET    | Usuário logado passa a participar do desafio                     |  sim  |
| `/progresso`                                    | GET    | Lista o progresso do usuário logado                              |  sim  |
| `/progresso?acao=novo&desafioId=`               | GET    | Formulário de registro de progresso                              |  sim  |
| `/progresso?acao=editar&id=`                    | GET    | Formulário de edição (somente quem registrou)                    |  sim  |
| `/progresso`                                    | POST   | Salva (cria ou edita) um registro de progresso                   |  sim  |
| `/progresso?acao=excluir&id=`                   | GET    | Exclui o registro (somente quem registrou)                       |  sim  |
| `/progresso?acao=porDesafio&desafioId=`         | GET    | Lista todos os registros de progresso de um desafio              |  sim  |

## Regras de negócio (camada Service)

- **Usuário**: nome, login, senha (mínimo 6 caracteres) e perfil obrigatórios; login
  único; autenticação por login+senha.
- **Desafio**: nome, tipo de exercício e data de início obrigatórios; data de fim (se
  informada) não pode ser anterior à data de início; somente o criador pode editar ou
  excluir; ao criar, o criador é sempre o usuário logado (nunca um valor vindo do
  formulário).
- **Participação**: usuário e desafio precisam existir; um usuário não pode participar
  duas vezes do mesmo desafio (validado na Service e reforçado por `UNIQUE` no banco).
- **Progresso**: desafio e valor atingido obrigatórios; data assume a data atual quando
  não informada; só é possível registrar progresso em um desafio do qual o usuário
  participa; somente quem registrou pode editar ou excluir.

## Funcionalidades implementadas

**Primeira etapa** — estrutura MVC/DAO/Service, Maven/Docker configurados, conexão com
MySQL, `Mapeavel`/`mapearSimples`, cadastro e listagem de usuários, estrutura inicial
de Desafio, layout base.

**Segunda etapa** (esta entrega) — projeto completo:

- Login/logout simples baseado em sessão, exigido apenas onde é necessário saber
  "quem" está agindo (`AuthFilter` para Progresso; verificação inline no
  `DesafioServlet` para as ações que exigem usuário logado).
- CRUD completo de Desafios: criar, listar, visualizar (com detalhes, criador e
  participantes), editar e excluir — com checagem de que só o criador edita/exclui.
- Participação de usuários em desafios, com tabela `participacoes`, prevenção de
  participação duplicada e listagem "Meus Desafios".
- Sistema de Progresso completo: registrar, listar (o próprio progresso), editar,
  excluir e consultar por desafio (progresso de todos os participantes).
- Exclusão de Desafio tratando corretamente os relacionamentos (participações e
  progresso são removidos via `ON DELETE CASCADE` no banco).
- Validações de negócio na camada Service (campos obrigatórios, datas, duplicidade de
  participação, posse de registros para edição/exclusão).
- Navegação completa (Home, Usuários, Desafios, Meus Desafios, Progresso, Login/Sair)
  presente em todas as páginas.
- Views novas: `login.jsp`, `desafios/form.jsp`, `desafios/visualizar.jsp`,
  `progresso/lista.jsp`, `progresso/form.jsp`; JSPs existentes atualizadas com a nova
  navegação.

## Como foi testado

O projeto foi validado de ponta a ponta em ambiente real (não apenas compilado):

1. `mvn clean package` — build limpo, sem erros.
2. Banco: `init.sql` executado em um MySQL/MariaDB real, com verificação manual de que
   o `ON DELETE CASCADE` remove participações e progresso ao excluir um desafio.
3. Aplicação implantada em um Apache Tomcat 10.1 real e testada via HTTP (`curl`),
   cobrindo: login/logout, cadastro de usuário, listagem pública de desafios,
   redirecionamento de `/progresso` para `/login` quando não autenticado, criação de
   desafio, bloqueio de edição/exclusão por quem não é o criador, participação (com
   bloqueio de participação duplicada), registro/edição/exclusão de progresso com
   checagem de posse, validações de campos obrigatórios e de datas, e exclusão de
   desafio com cascade real confirmado no banco.

## Possíveis evoluções futuras (fora do escopo da entrega)

- Hash de senha (ex.: BCrypt) em vez de texto puro — mantido simples por ser o padrão
  já usado desde a primeira etapa e adequado ao escopo da disciplina.
- Paginação nas listagens.
- Edição/exclusão de usuários pela interface (o `UsuarioService`/`UsuarioDAO` já têm os
  métodos `alterar`/`deletar` prontos, faltando apenas expor rotas e telas).
