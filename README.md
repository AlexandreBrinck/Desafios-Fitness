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
- Gson 2.13 (JSON da API REST)
- Docker / docker-compose (MySQL + Tomcat)
- Postman (coleção de testes da API em [`postman/`](postman/))

## Arquitetura

MVC clássico com as camadas Controller (Servlet) → Service (regra de negócio) → DAO
(acesso ao banco) → Model, e rotas por parâmetro (`?acao=...`), sem `pathInfo`/`switch`
como mecanismo de roteamento.

```
src/main/java/br/com/desafiosfitness/
├── config/      MysqlSingleton (conexão única com o MySQL)
├── controller/  Servlets (BaseServlet + um por entidade/funcionalidade)
│   └── api/     DesafioApiServlet (API REST em JSON — Parte 2)
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

postman/Desafios-Fitness-API.postman_collection.json   Coleção de testes da API
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
URL      = jdbc:mysql://mysql:3306/desafios_fitness?...
USER     = desafios_user
PASSWORD = desafios123
```

Por padrão o projeto aponta para o host `mysql`, que é o nome do serviço MySQL no
`docker-compose.yml` (execução via Docker). **Para rodar com MySQL e Tomcat instalados
diretamente no Windows, sem Docker**, troque `mysql` por `localhost` nessa URL antes de
compilar.

O site (Parte 1) e a API REST (Parte 2) usam exatamente a mesma conexão, os mesmos
DAOs e o mesmo banco `desafios_fitness`: um desafio criado pela API aparece no site e
vice-versa.

## Como executar

### Opção 1 — MySQL e Tomcat instalados localmente 

0. Em `MysqlSingleton.java`, troque o host `mysql` por `localhost` na URL.
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

### Opção 2 — Docker (recomendada)

1. Clone o repositório.
2. Entre na pasta do projeto.
3. Execute `docker compose up -d`.
4. Acesse `http://localhost:8080/desafios-fitness/home`.

O projeto já possui o WAR em `deploy/desafios-fitness.war`, portanto não é necessário instalar Java, Maven, Tomcat ou MySQL separadamente para executar a versão Dockerizada.

O `docker-compose.yml` sobe automaticamente:
- MySQL 8.4;
- Tomcat 10.1 com JDK 21;
- banco `desafios_fitness`;
- tabelas e dados iniciais através do `init.sql`.

**Depois de alterar o código-fonte**, gere o WAR de novo para o Tomcat do Docker
carregar a nova versão. Sem Maven instalado, dá para usar a própria imagem do Maven:

```bash
# Linux/macOS/Git Bash
docker run --rm -v "$(pwd)":/app -w /app maven:3.9-eclipse-temurin-21 mvn clean package
# PowerShell
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-21 mvn clean package
```

O WAR sai em `deploy/desafios-fitness.war` (pasta montada no Tomcat), e o Tomcat
reimplanta a aplicação sozinho em alguns segundos.

> O `init.sql` só roda na **primeira** criação do volume do MySQL. Para recriar o
> banco do zero (apaga os dados!), use `docker compose down -v` e depois
> `docker compose up -d`.


## API REST de Desafios (Parte 2)

### Objetivo

Expor o cadastro de Desafios como uma API REST em JSON, permitindo que outros
clientes (Postman, front-ends, apps) listem, consultem, cadastrem, atualizem e
excluam desafios **com as mesmas regras de negócio do site**. A API não tem regras
próprias: o controller converte HTTP/JSON e delega ao mesmo `DesafioService`, que usa
o mesmo `DesafioDAO` e o mesmo banco.

```
Postman / cliente HTTP
   │  JSON
   ▼
DesafioApiServlet (/api/desafios/*)   ← controller/api — valida formato do JSON, escolhe o status HTTP
   ▼
DesafioService                         ← regras de negócio (as mesmas do site)
   ▼
DesafioDAO → MysqlDAO → MysqlSingleton ← SQL no MySQL (banco desafios_fitness)
```

### Endpoints

Base: `http://localhost:8080/desafios-fitness`

| Método | Rota                 | Ação         | Login          | Sucesso |
|--------|----------------------|--------------|----------------|:-------:|
| GET    | `/api/desafios`      | Lista todos  | não            |   200   |
| GET    | `/api/desafios/{id}` | Busca por id | não            |   200   |
| POST   | `/api/desafios`      | Cadastra     | sim            |   201   |
| PUT    | `/api/desafios/{id}` | Atualiza     | sim (criador)  |   200   |
| DELETE | `/api/desafios/{id}` | Exclui       | sim (criador)  |   200   |

**Autenticação**: a API usa a mesma sessão do site. Faça `POST /login` com corpo
`x-www-form-urlencoded` (`login`, `senha`). A resposta é um redirecionamento 302
para `/home` e traz o cookie `JSESSIONID`. O Postman guarda esse cookie e o envia
automaticamente nas requisições seguintes. Sem login, POST/PUT/DELETE respondem `401`.

**Campos do desafio** (JSON):

| Campo           | Tipo                 | Obrigatório | Limite       |
|-----------------|----------------------|:-----------:|--------------|
| `nome`          | texto                | sim         | 150          |
| `descricao`     | texto                | não         | 500          |
| `tipoExercicio` | texto                | sim         | 100          |
| `meta`          | texto                | não         | 100          |
| `dataInicio`    | data `yyyy-MM-dd`    | sim         | —            |
| `dataFim`       | data `yyyy-MM-dd`    | não         | ≥ dataInicio |
| `id`, `criadorId` | número (só resposta) | —         | gerados pelo sistema |

- O `id` é gerado pelo MySQL e devolvido no POST.
- O `criadorId` é sempre o usuário logado e nunca é lido do corpo.
- O PUT substitui todos os campos; opcionais omitidos ficam `null`.

**Formato das respostas**: sucesso traz `sucesso`, `mensagem` (quando houver) e
`dados`; erro traz `sucesso: false`, `status` e `erro`.

| Status | Quando |
|:------:|--------|
| 400 | JSON malformado, campo com tipo errado, data fora do formato, campo obrigatório ausente, texto maior que o limite, data de fim anterior ao início, id inválido na URL |
| 401 | POST/PUT/DELETE sem login |
| 403 | PUT/DELETE de desafio criado por outro usuário |
| 404 | desafio inexistente |
| 405 | método não suportado na rota (ex.: DELETE sem id); traz o cabeçalho `Allow` |
| 415 | corpo sem `Content-Type: application/json` |
| 500 | erro interno (ex.: banco fora do ar) |

### Exemplos de requisições

Com `curl` (o arquivo `cookies.txt` guarda a sessão):

```bash
BASE=http://localhost:8080/desafios-fitness

# Login (joao / 123456)
curl -c cookies.txt -d "login=joao&senha=123456" $BASE/login

# Listar e buscar
curl $BASE/api/desafios
curl $BASE/api/desafios/1

# Cadastrar
curl -b cookies.txt -X POST -H "Content-Type: application/json" \
     -d '{"nome":"Pedalada 100km","tipoExercicio":"Ciclismo","meta":"100 km","dataInicio":"2026-12-01","dataFim":"2026-12-31"}' \
     $BASE/api/desafios

# Atualizar (troque 3 pelo id devolvido no cadastro)
curl -b cookies.txt -X PUT -H "Content-Type: application/json" \
     -d '{"nome":"Pedalada 150km","tipoExercicio":"Ciclismo","meta":"150 km","dataInicio":"2026-12-01","dataFim":"2026-12-31"}' \
     $BASE/api/desafios/3

# Excluir
curl -b cookies.txt -X DELETE $BASE/api/desafios/3
```

`POST /api/desafios` → **201 Created**

```json
{
  "sucesso": true,
  "mensagem": "Desafio cadastrado com sucesso.",
  "dados": {
    "id": 3,
    "nome": "Pedalada 100km",
    "descricao": null,
    "tipoExercicio": "Ciclismo",
    "meta": "100 km",
    "dataInicio": "2026-12-01",
    "dataFim": "2026-12-31",
    "criadorId": 2
  }
}
```

`GET /api/desafios/9999` → **404 Not Found**

```json
{ "sucesso": false, "status": 404, "erro": "Desafio com id 9999 não encontrado." }
```

`PUT /api/desafios/1` logado como `joao` (o desafio 1 é do `admin`) → **403 Forbidden**

```json
{ "sucesso": false, "status": 403, "erro": "Somente o criador pode editar este desafio." }
```

### Coleção do Postman

Arquivo: [`postman/Desafios-Fitness-API.postman_collection.json`](postman/Desafios-Fitness-API.postman_collection.json)
(formato Collection v2.1).

**Importar**: no Postman, clique em **Import** e selecione o arquivo (ou arraste-o para
a janela). A coleção "Desafios Fitness - API REST" aparece na barra lateral.

**Variáveis da coleção** (aba *Variables*):

| Variável                | Padrão                                   | Uso |
|-------------------------|------------------------------------------|-----|
| `baseUrl`               | `http://localhost:8080/desafios-fitness` | endereço da aplicação |
| `login` / `senha`       | `joao` / `123456`                        | usuário do login |
| `desafioId`             | preenchida pelo POST                     | id usado no GET/PUT/DELETE |
| `desafioDeOutroUsuario` | `1`                                      | desafio do `admin`, para o teste 403 |

**Organização**:

1. **0. Autenticacao**: login (guarda o cookie de sessão).
2. **1. CRUD de Desafios**: listar → buscar → cadastrar (salva `desafioId`) → buscar o
   criado → atualizar → excluir → buscar o excluído (404).
3. **2. Validacoes e erros**: 404, id inválido, campo obrigatório, data inválida,
   datas incoerentes, JSON malformado, 415, 403, 405, logout e 401.

Cada requisição tem testes automáticos (aba *Tests*) e as principais têm exemplos de
resposta salvos (*Examples*).

**Executar tudo**: com o projeto rodando, clique com o botão direito na coleção, escolha
**Run collection** e depois **Run**. Rode na ordem, porque as requisições dependem do
login e do `desafioId`. A coleção cria um desafio e o exclui no final, então o banco
volta ao estado anterior.

Pela linha de comando, com o [Newman](https://github.com/postmanlabs/newman):

```bash
newman run postman/Desafios-Fitness-API.postman_collection.json
# ou, sem instalar nada (Docker):
docker run --rm --network host -v "$(pwd)/postman":/etc/newman postman/newman:6-alpine \
       run Desafios-Fitness-API.postman_collection.json
```

> No Docker Desktop (Windows/macOS), `--network host` pode não alcançar o
> `localhost` da máquina. Nesse caso, remova `--network host` e acrescente ao final
> `--env-var baseUrl=http://host.docker.internal:8080/desafios-fitness`.


## Regras de negócio (camada Service)

- **Usuário**: nome, login, senha (mínimo 6 caracteres) e perfil obrigatórios; login
  único; autenticação por login+senha.
- **Desafio**: nome, tipo de exercício e data de início obrigatórios; textos limitados
  ao tamanho das colunas do banco (nome 150, descrição 500, tipo de exercício e meta
  100); data de fim (se informada) não pode ser anterior à data de início; ao
  cadastrar, o id gerado pelo banco é devolvido no próprio objeto; somente o criador pode editar ou
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
4. **API REST (Parte 2)**: testada em MySQL 8.4 + Tomcat 10.1 (Docker, banco recriado
   pelo `init.sql`). A coleção do Postman foi executada com o Newman: 19 requisições e
   66 verificações, sem falhas.
   - O id devolvido pelo POST confere com o gravado no MySQL.
   - Os limites de tamanho do Service funcionam também no formulário do site, que
     antes gerava erro 500.
   - Uma regressão completa do site continuou funcionando: usuários, desafios,
     participação, progresso e cascade.

## Possíveis evoluções futuras (fora do escopo da entrega)

- Hash de senha (ex.: BCrypt) em vez de texto puro — mantido simples por ser o padrão
  já usado desde a primeira etapa e adequado ao escopo da disciplina.
- Paginação nas listagens.
- Edição/exclusão de usuários pela interface (o `UsuarioService`/`UsuarioDAO` já têm os
  métodos `alterar`/`deletar` prontos, faltando apenas expor rotas e telas).
