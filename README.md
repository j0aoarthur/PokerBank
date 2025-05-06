<h1 align="center">🃏 PokerBank</h1>

<p align="center">Aplicação Spring Boot para gerenciamento de jogos de poker, jogadores, fichas e suas interações.</p>

---

## ✨ Funcionalidades

- ✅ Gerencie fichas de poker com diferentes valores e cores
- ✅ Adicione e gerencie jogadores
- ✅ Crie e acompanhe jogos de poker
- ✅ Associe jogadores a jogos com suas respectivas fichas e saldos
- ✅ Carregue dados automaticamente para testes

---

## 🛠️ Tecnologias Utilizadas

* ⚙️ **Java (Spring Boot)**
* 📦 **Maven** – Gerenciamento de dependências
* 🗃️ **SQL** – Banco de dados relacional
* 🔒 **Jakarta Validation** – Validação de dados de entrada
* 🧠 **Spring Data JPA** – Integração com o banco de dados
* 🌐 **Spring Web** – APIs RESTful

---

## 🧩 Entidades do Banco de Dados

### 🟡 1. `Chip` (Ficha)

Representa uma ficha de poker com valor e cor específicos.

* `id` (Long) – Identificador único
* `color` (String) – Cor da ficha
* `value` (BigDecimal) – Valor monetário da ficha

### 👤 2. `Player` (Jogador)

Representa um jogador de poker.

* `id` (Long) – Identificador único
* `name` (String) – Nome do jogador

### 🃏 3. `Game` (Jogo)

Representa um jogo de poker.

* `id` (Long) – Identificador único
* `date` (LocalDate) – Data do jogo

### 🤝 4. `GamePlayer` (Jogador no Jogo)

Relaciona um jogador a um jogo, incluindo seu saldo e fichas.

* `gameId` (Long) – ID do jogo
* `playerId` (Long) – ID do jogador
* `balance` (BigDecimal) – Saldo do jogador no jogo
* `chips` (Lista de `ChipCount`) – Fichas atribuídas ao jogador

---

## 🔗 Endpoints da API

### 🎨 **Endpoints de Fichas (`/chips`)**

* **POST** `/chips`
  Criar nova ficha:

  ```json
  {
    "color": "string",
    "value": "decimal"
  }
  ```

* **GET** `/chips`
  Listar todas as fichas cadastradas.

---

### 👥 **Endpoints de Jogadores (`/players`)**

* **POST** `/players`
  Criar novo jogador:

  ```json
  {
    "name": "string"
  }
  ```

* **GET** `/players`
  Listar todos os jogadores.

* **GET** `/players/not-in-game/{gameId}`
  Listar jogadores que **não** participam do jogo com ID fornecido.

---

### 🃏 **Endpoints de Jogos (`/games`)**

* **POST** `/games`
  Criar novo jogo:

  ```json
  {
    "date": "yyyy-MM-dd"
  }
  ```

* **GET** `/games`
  Listar todos os jogos.

---

### 🔄 **Endpoints de Participação em Jogos (`/game-players`)**

* **POST** `/game-players`
  Adicionar jogador a um jogo, com fichas e saldo:

  ```json
  {
    "gameId": "long",
    "playerId": "long",
    "balance": "decimal",
    "chips": [
      {
        "chipId": "long",
        "quantity": "integer"
      }
    ]
  }
  ```

---

## 🚀 Como Executar o Projeto

1. Clone o repositório:

   ```bash
   git clone https://github.com/j0aoarthur/pokerbank.git
   cd pokerbank
   ```

2. Compile o projeto:

   ```bash
   mvn clean install
   ```

3. Rode a aplicação:

   ```bash
   mvn spring-boot:run
   ```

4. Acesse no navegador:
   [http://localhost:8080](http://localhost:8080)

---

## 🧪 Testes com Dados de Exemplo

A aplicação possui um carregador automático de dados (`DataLoader`) para testes no perfil `test`.

1. Ative o perfil de teste no `application.properties`:

   ```properties
   spring.profiles.active=test
   ```

2. Execute a aplicação. Serão carregados:

    * Fichas com cores e valores diversos
    * Jogadores com nomes pré-definidos
    * Um jogo com jogadores, fichas e saldos atribuídos


