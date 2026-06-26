# 🃏 PokerBank — Como Rodar o Projeto

> Guia completo de ambientes: desenvolvimento local (com PostgreSQL), modo de teste rápido (H2 in-memory) e testes unitários automatizados.

---

## 📐 Estrutura de Ambientes

| Perfil | Banco de dados | Flyway | Quando usar |
|---|---|---|---|
| `dev` | PostgreSQL (local ou Docker) | ✅ habilitado | Desenvolvimento do dia a dia |
| `test` | H2 in-memory | ❌ desabilitado | Rodar a API sem instalar PostgreSQL |
| `prod` | PostgreSQL Neon (SSL) | ✅ habilitado | Fly.io — ambiente de produção |
| *(unit tests)* | H2 in-memory | ❌ desabilitado | `mvn test` — testes automatizados |

---

## 🔧 Pré-requisitos

- **Java 17+** — `java -version`
- **Maven 3.8+** — `mvn -version`
- **Docker + Docker Compose** — apenas para o perfil `dev` com PostgreSQL

---

## 🚀 Ambiente Dev (recomendado para desenvolvimento)

O perfil `dev` usa **PostgreSQL** e **Flyway** (migrations automáticas).

### 1. Configure as variáveis de ambiente

```bash
# Copie o arquivo de exemplo
cp .env.example .env
```

Abra o `.env` e ajuste os valores (o padrão já é compatível com o `docker-compose.yml`):

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pokerbank_dev?ssl=false
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_PROFILES_ACTIVE=dev
```

### 2. Suba o banco de dados com Docker

```bash
docker-compose up -d
```

> O banco `pokerbank_dev` será criado automaticamente. Para parar: `docker-compose down`

### 3. Rode a aplicação

```bash
# Carrega o .env e sobe o servidor
export $(grep -v '^#' .env | xargs) && mvn spring-boot:run
```

Ou, se preferir via `.jar`:

```bash
mvn clean package -DskipTests
export $(grep -v '^#' .env | xargs) && java -jar target/pokerbank.jar
```

### ✅ Resultado esperado

```
Started PokerBankApplication in X.XXX seconds
```

API disponível em: **http://localhost:7070/api**  
Swagger UI: **http://localhost:7070/api/documentation**

---

## 🧪 Ambiente Test — H2 in-memory (sem PostgreSQL)

O perfil `test` usa banco **H2 em memória**. Não precisa de Docker nem de PostgreSQL instalado.

### 1. Configure o perfil no `.env`

```env
SPRING_PROFILES_ACTIVE=test
```

Ou passe diretamente na linha de comando (sem precisar alterar o `.env`):

### 2. Rode a aplicação

```bash
# Via Maven (recomendado)
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Via jar (precisa compilar antes)
mvn clean package -DskipTests
java -jar target/pokerbank.jar --spring.profiles.active=test
```

> ⚠️ O banco H2 é recriado a cada vez que a aplicação sobe. Os dados não persistem entre execuções.

### ✅ Resultado esperado

API disponível em: **http://localhost:8080/api**  
*(porta padrão quando `SERVER_PORT` não está definido no ambiente)*

---

## 🔬 Testes Unitários Automatizados

Os testes em `src/test/` usam H2 automaticamente (configurado em `src/test/resources/application.properties`). **Não precisa de banco rodando.**

### Rodar todos os testes

```bash
mvn test
```

### Rodar apenas uma classe

```bash
mvn test -Dtest=NomeDaClasseTest
```

### Rodar com relatório de cobertura

```bash
mvn verify
```

---

## 🗂️ Referência de Arquivos de Configuração

| Arquivo | Perfil | Banco | Descrição |
|---|---|---|---|
| `src/main/resources/application.properties` | base | — | Configurações comuns a todos os perfis |
| `src/main/resources/application-dev.properties` | `dev` | PostgreSQL | Desenvolvimento local com Flyway |
| `src/main/resources/application-test.properties` | `test` | H2 in-memory | Execução rápida sem Postgres |
| `src/test/resources/application.properties` | unit tests | H2 in-memory | Usado pelo `mvn test` |

---

## 🔌 Variáveis de Ambiente

Todas as variáveis ficam no arquivo `.env` (não versionado). Use `.env.example` como base.

| Variável | Padrão | Descrição |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil ativo (`dev` ou `test`) |
| `SPRING_DATASOURCE_URL` | — | URL de conexão JDBC do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | — | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | — | Senha do banco |
| `SERVER_PORT` | `8080` | Porta do servidor |
| `FRONTEND_BASE_URL` | `http://localhost:5173` | URL do frontend (CORS) |
| `JWT_SECRET_PASSWORD` | — | Segredo para assinar tokens JWT |
| `JWT_EXPIRATION_TIME` | `1440` | Validade do access token (minutos) |
| `JWT_REFRESH_EXPIRATION_TIME` | `7` | Validade do refresh token (dias) |
| `SPRING_MAIL_HOST` | `smtp.gmail.com` | Host SMTP para envio de e-mails |
| `SPRING_MAIL_PORT` | `587` | Porta SMTP |
| `SPRING_MAIL_USERNAME` | — | E-mail remetente |
| `SPRING_MAIL_PASSWORD` | — | App password do Gmail |

---

## 🐳 Docker Compose

O `docker-compose.yml` sobe apenas o **banco de dados PostgreSQL** para desenvolvimento.

```bash
# Subir
docker-compose up -d

# Ver logs
docker-compose logs -f postgres

# Parar (mantém os dados)
docker-compose down

# Parar e apagar os dados
docker-compose down -v
```

**Credenciais padrão do banco via Docker:**

| Campo | Valor |
|---|---|
| Host | `localhost` |
| Porta | `5432` |
| Banco | `pokerbank_dev` |
| Usuário | `postgres` |
| Senha | `postgres` |

---

## 🌐 Ambiente Prod (Fly.io + Neon)

O projeto é hospedado no **Fly.io** (região `gru` — São Paulo) com banco **Neon PostgreSQL** serverless.

### Infraestrutura

| Componente | Serviço |
|---|---|
| Aplicação | [Fly.io](https://fly.io) — `pokerbank-green-water-5229` |
| Banco de dados | [Neon](https://neon.tech) — PostgreSQL serverless com SSL |
| CI/CD | GitHub Actions → deploy automático no push para `main` |

### Deploy automático (CI/CD)

Qualquer push na branch `main` dispara o pipeline:

1. **Testes unitários** rodam automaticamente
2. Se todos passarem → **deploy automático** para o Fly.io

Não é necessário fazer nada manualmente após o push.

### Deploy manual (sem CI/CD)

```bash
# Instalar flyctl (se não tiver)
brew install flyctl

# Login
flyctl auth login

# Deploy
flyctl deploy
```

### Configurar secrets no Fly.io

As variáveis de ambiente de produção **não ficam no `.env.prod`** em produção — ficam no Fly.io como secrets:

```bash
# Configurar todas as variáveis de uma vez
flyctl secrets set \
  SPRING_DATASOURCE_URL="jdbc:postgresql://ep-wild-surf-a4q5w3on-pooler.us-east-1.aws.neon.tech/pokerbank?sslmode=require" \
  SPRING_DATASOURCE_USERNAME="pokerbank_owner" \
  SPRING_DATASOURCE_PASSWORD="sua_senha" \
  JWT_SECRET_PASSWORD="seu_jwt_secret_longo" \
  SPRING_MAIL_USERNAME="seu@email.com" \
  SPRING_MAIL_PASSWORD="app_password" \
  FRONTEND_BASE_URL="https://seu-frontend.vercel.app"
```

> ⚠️ `SPRING_PROFILES_ACTIVE=prod` e `SERVER_PORT=8080` já estão definidos no `fly.toml` — não precisa setar via secrets.

### Configurar o token do GitHub Actions

1. Gere o token: `flyctl auth token`
2. No GitHub: **Settings → Secrets → Actions → New repository secret**
3. Nome: `FLY_API_TOKEN` / Valor: o token gerado

### Verificar a aplicação em produção

```bash
# Ver status
flyctl status

# Ver logs em tempo real
flyctl logs

# Abrir no navegador
flyctl open

# Checar saúde
curl https://pokerbank-green-water-5229.fly.dev/api/actuator/health
```

### Configuração de arquivo por perfil

| Arquivo | Descrição |
|---|---|
| `src/main/resources/application-prod.properties` | Configurações do perfil prod |
| `.env.prod` | Variáveis locais (referência) — **nunca subir para o Git** |
| `fly.toml` | Configuração da máquina, health check e `SPRING_PROFILES_ACTIVE=prod` |
| `.github/workflows/fly-deploy.yml` | Pipeline CI/CD |
