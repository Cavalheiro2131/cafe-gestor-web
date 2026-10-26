# CafeGestorWebApp

Sistema web do **Café Gestor** (colheita e estoque de café): API REST em **Spring Boot** com as páginas HTML/CSS/JS do
front-end, as regras de negócio do núcleo refatorado (Etapa 6) e persistência em **MySQL via JDBC**.

```
Navegador (HTML/CSS/JS)  ──fetch /api/**──►  Controladores REST ──► Serviços do núcleo ──► Repositórios JDBC ──► MySQL
   src/main/resources/static                 br.cafegestor.web      model, service, ...      repository.jdbc
```

## Requisitos

- JDK 17 e Maven (o NetBeans traz o Maven embutido)
- MySQL 8 (não é necessário para o modo de demonstração)

## Como executar

**1. Banco de dados (MySQL)**

```
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/dados_iniciais.sql
```

**2. Conexão.** Por padrão a aplicação usa `localhost:3306`, banco `cafe_gestor`, usuário `root` e senha vazia.
Para mudar, defina as variáveis de ambiente `CAFE_DB_URL`, `CAFE_DB_USER` e `CAFE_DB_PASS` (nada de senha no código).

**3. Subir o sistema**

- No NetBeans: *File > Open Project* na pasta do projeto, depois botão direito > **Run** (ou `F6`).
- Pelo terminal: `mvn spring-boot:run`

Acesse **http://localhost:8080**.

**Modo de demonstração (sem MySQL).** Usa repositórios em memória com dados de exemplo; os dados somem ao parar o servidor:

```
mvn spring-boot:run -Dspring-boot.run.profiles=memoria
```

No NetBeans: Properties > Actions > Run Project, ou defina `spring.profiles.active=memoria` na variável de ambiente.

**Contas** (criadas por `sql/dados_iniciais.sql` e pelo modo de demonstração)

| Login | Senha | Perfil | Pode |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | consultar e alterar |
| `gerente` | `gerente123` | GERENTE | consultar e alterar |
| `operador` | `operador123` | OPERADOR | só consultar |

## Testes

```
mvn test
```

Roda os testes JUnit 5 do núcleo (Etapa 7), os testes dos controladores, do interceptor de segurança e da semente,
e o teste de integração com MockMvc, que sobe o contexto do Spring em modo `memoria` (sem banco).
Não é preciso ter MySQL para rodar os testes.

## API REST

Erros têm sempre o formato `{"mensagem": "..."}`. Sem login, qualquer rota responde 401. O perfil OPERADOR só pode
fazer GET; POST, PUT e PATCH respondem 403 para ele.

| Método e rota | Função |
|---|---|
| `POST /api/sessao/login` | Entra (corpo: `login`, `senha`); 200 com o usuário, ou 401 |
| `GET /api/sessao/atual` | Usuário logado |
| `POST /api/sessao/logout` | Sai (204) |
| `GET /api/resumo` | Totais da tela inicial |
| `GET /api/producao/simulacao?balaios=&balaiosPorSaca=` | Sacas completas e sobra (CalculadoraProducao) |
| `GET /api/funcionarios`, `GET /api/funcionarios/{id}` | Consulta |
| `POST /api/funcionarios`, `PUT /api/funcionarios/{id}` | Cadastra (201) e edita |
| `PATCH /api/funcionarios/{id}/status` | Inativa ou reativa (corpo: `ativo`) |
| `GET /api/tipos-cafe`, `POST /api/tipos-cafe` | Lista e cadastra |
| `GET /api/balaios`, `POST /api/balaios` | Lista e registra (`funcionarioId`, `tipoCafeId`, `quantidade`, `data` aaaa-mm-dd) |
| `GET /api/estoque/saldos`, `GET /api/estoque/historico` | Saldos e histórico |
| `POST /api/estoque/movimentacoes` | Entrada ou saída (`tipo`, `tipoCafeId`, `quantidade`, `descricao`); devolve `novoSaldo` |

Códigos: 200/201/204 sucesso; **400** requisição malformada; **401** sem login; **403** perfil sem permissão;
**422** regra de negócio violada (campo inválido, saldo insuficiente, registro não encontrado); **500** falha no banco.

## Estrutura do projeto

```
src/main/java
  model, exception, util, repository, security, service   núcleo de regras de negócio (Etapa 6), sem dependência do Spring
  br/cafegestor/web
    config       beans por perfil (JDBC ou memória) e semente de demonstração
    controller   controladores REST e tratamento de erros
    dto          formatos de entrada e saída da API
    seguranca    sessão do usuário e interceptor de login e permissões
    servico      EstoqueServiceTransacional (transação sobre o serviço do núcleo)
src/main/resources
  application.properties, application-memoria.properties
  static/        páginas, CSS e JavaScript do front-end (Etapa 8), agora ligados à API
src/test/java    testes JUnit 5
sql/             schema.sql e dados_iniciais.sql
```

## Decisões de projeto

- **O núcleo continua sem Spring.** O Spring só liga as peças (`config`) e expõe os serviços (`controller`),
  então a mesma regra de negócio serve ao Swing, à Web e aos testes.
- **Transação no estoque.** Entrada e saída gravam em duas tabelas (saldo e movimentação). O `EstoqueServiceTransacional`
  usa `@Transactional` para que as duas gravações aconteçam juntas ou nenhuma: resolve o risco registrado no plano de testes.
- **Login por sessão HTTP** (cookie `JSESSIONID`, `HttpOnly`, `SameSite=Lax`) e um interceptor que aplica
  `PerfilUsuario.podeAlterarDados()` do núcleo. Em um sistema real, recomenda-se Spring Security.
- **Senhas** gravadas com PBKDF2 (`CodificadorPbkdf2`), nunca em texto puro.
