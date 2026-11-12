# MoveSmart — API (SOA / Web Services)

Global Solution FIAP 2026.2 · CitizenTech · Engenharia de Software (3º ano)

## Integrantes

- Augusto Ferreira — RM 557709
- Heitor Prestes — RM 554823

## Princípio central: separar os tipos de informação

Cada dado devolvido pela API informa a sua `origem`:

| Origem | O que é |
|---|---|
| `SPTRANS_OLHO_VIVO` | Previsão de chegada informada pela API oficial da SPTrans (**não** é estimativa nossa) |
| `RELATO_USUARIO` | Lotação relatada voluntariamente por passageiros (**não** é contagem real de pessoas) |
| `MODELO_ML` | Estimativa de atraso produzida pelo nosso modelo de Machine Learning |

A API Olho Vivo não informa lotação; por isso o indicador de lotação vem de relatos dos usuários.

## Tecnologias

Java 25 · Spring Boot 4 · Spring Data JPA · Spring Security + JWT · MySQL · Flyway · springdoc-openapi (Swagger) · JUnit 5 / Mockito.

## Como executar

Pré-requisitos: JDK 25, MySQL 8 em `localhost:3306` (o mesmo do Workbench) e Maven (ou o `./mvnw` do projeto).

1. Confira o usuário e a senha do MySQL em `src/main/resources/application.properties` (padrão: `root` / `fiap`). O schema `movesmart` é criado sozinho na primeira execução.
2. *(Opcional, para a integração com a SPTrans)* defina o token da API Olho Vivo em uma variável de ambiente — ele é pessoal e **não vai para o GitHub**:
   - Windows (PowerShell): `$env:SPTRANS_TOKEN="seu_token"`
   - No IntelliJ: *Run > Edit Configurations > Environment variables* → `SPTRANS_TOKEN=seu_token`
3. Suba a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Acesse (porta **8085**):
   - Swagger UI: http://localhost:8085/swagger-ui.html
   - OpenAPI (JSON): http://localhost:8085/v3/api-docs
   - Health check: http://localhost:8085/health_check

### Autenticação

Na primeira execução é criado o usuário `admin` / `admin123` (perfil ADMIN). Troque a senha em `PUT /usuarios/senha`.

1. `POST /login` com `{"login":"admin","senha":"admin123"}` → copie o `tokenJWT`.
2. No Swagger, clique em **Authorize** e informe apenas o token.

| Perfil | Pode |
|---|---|
| `ADMIN` | Tudo: cadastrar/atualizar/excluir linhas, sincronizar com a SPTrans, registrar estimativas do ML, gerenciar usuários |
| `USER` | Consultar linhas, previsões, lotação e estimativas; relatar lotação; gerenciar os próprios favoritos |

## Endpoints

| Método | Rota | Perfil | Descrição |
|---|---|---|---|
| POST | `/login` | público | Gera o token JWT |
| GET | `/health_check` | público | Verifica se a API está no ar |
| POST/GET/PUT/DELETE | `/linhas` · `/linhas/{id}` | ADMIN (GET: USER) | CRUD de linhas (`GET /linhas?busca=8000`, paginado; exclusão lógica) |
| POST | `/sptrans/linhas/sincronizar?termo=8000` | ADMIN | Busca linhas na Olho Vivo e salva (atualiza se já existir) |
| POST | `/sptrans/linhas/{id}/previsoes/sincronizar` | ADMIN | Salva as previsões de chegada da Olho Vivo |
| GET | `/linhas/{id}/previsoes` | USER/ADMIN | Últimas previsões guardadas |
| POST | `/linhas/{id}/lotacao` | USER/ADMIN | Relata a lotação (`BAIXA`, `MEDIA`, `ALTA`) |
| GET | `/linhas/{id}/lotacao` | USER/ADMIN | Lotação agregada dos relatos dos últimos 30 min |
| POST | `/linhas/{id}/estimativa-atraso` | ADMIN | O notebook de ML envia a estimativa de atraso |
| GET | `/linhas/{id}/estimativa-atraso` | USER/ADMIN | Estimativa de atraso mais recente |
| GET/POST/DELETE | `/favoritos` · `/favoritos/{idLinha}` | USER/ADMIN | Linhas favoritas do usuário logado |
| POST/GET/PUT/DELETE | `/usuarios` | ADMIN | Gestão de usuários e perfis |

Um roteiro pronto para testar tudo está em [`http/requests.http`](http/requests.http) (cliente HTTP do IntelliJ).

### Códigos de resposta

`200/201/204` sucesso · `400` dados inválidos · `403` sem token ou sem permissão · `404` não encontrado · `422` regra de negócio (ex.: linha duplicada) · `503` SPTrans indisponível ou token não configurado.

## Estrutura

```
src/main/java/br/com/fiap3esa/movesmart
├── controller/   endpoints REST
├── service/      regras de negócio
├── domain/       entidades, DTOs, repositórios e exceções (por assunto)
└── infra/        segurança (JWT), CORS, Swagger, tratamento de erros e cliente da SPTrans
src/main/resources/db/migration   migrações Flyway
http/requests.http                roteiro de testes manuais
```

## Testes automatizados

```bash
./mvnw test
```

| Assunto | Domínio | Service | Controller / Cliente HTTP |
|---|---|---|---|
| Linhas | `LinhaTest` | `LinhaServiceTest` | `LinhaControllerTest` |
| Lotação | `OrigemDosDadosTest` | `LotacaoServiceTest` | `LotacaoControllerTest` |
| Estimativa de atraso (ML) | `OrigemDosDadosTest` | `EstimativaAtrasoServiceTest` | `EstimativaAtrasoControllerTest` |
| Favoritos | – | `FavoritoServiceTest` | `FavoritoControllerTest` |
| SPTrans | – | `SptransServiceTest`, `PrevisaoServiceTest` | `SptransControllerTest`, `SptransClientTest` |
| Usuários | `UsuarioTest` | `UsuarioServiceTest` | `UsuarioControllerTest` |

Esses testes **não precisam de banco nem de internet** (a SPTrans é simulada). Exceção: `MoveSmartApplicationTests` sobe o contexto completo e por isso exige o MySQL ligado — ele também valida as migrações do Flyway.

## Dados e privacidade

- Sem dados pessoais reais: o usuário tem apenas login e senha (guardada com BCrypt).
- Fontes de dados: API Olho Vivo (SPTrans) e dados históricos públicos usados no modelo de ML *(documentar fonte, link e período)*.

## Evidências da aplicação
Algumas prints da aplicação funcionando

## **Tabelas criadas**

<img width="837" height="777" alt="image" src="https://github.com/user-attachments/assets/3bedff15-5912-4189-b59d-d0e0af55a594" />

## **Testes rodando**

<img width="1227" height="625" alt="image" src="https://github.com/user-attachments/assets/8eda4466-fcbd-49c4-bbc3-d47a41a5b587" />

## **Cadastrando Usuario**

<img width="505" height="857" alt="image" src="https://github.com/user-attachments/assets/aa2f3614-c748-427b-934e-60b86069b277" />

## **Cadastrando Linha**

<img width="431" height="878" alt="image" src="https://github.com/user-attachments/assets/75e05307-dd4f-49b3-8a41-3882992b686b" />

## **Detalhes da Linha**

<img width="1080" height="806" alt="image" src="https://github.com/user-attachments/assets/1f5dd6d3-3074-473e-98fe-0a655e8256a9" />

## **Relato de Linha lotada**
<img width="1111" height="787" alt="image" src="https://github.com/user-attachments/assets/37ef1452-635e-4181-ba0b-22c6bacad9d6" />
