# Contrato da SimulaCred API: v1

> **Fonte única da verdade** entre o projeto **Backend** (`simulacred-api`) e o projeto **Mobile** (`simulacred-app`).
> Se este arquivo divergir de `projeto-backend-simulacred-api.md` ou de `projeto-mobile-simulacred-app.md`, **vale este arquivo**.

---

## 0. Regras para os agentes

1. **Nenhum agente altera este contrato por conta própria.** Se precisar mudar um campo, um endpoint ou um formato, **pare e peça ao usuário**. Ele atualiza este arquivo e repassa a mudança ao outro agente.
2. **Nomes de campos são exatos**, em `camelCase`, sem acento. Não renomeie, não traduza, não abrevie.
3. **Campos não listados aqui não existem.** O backend não envia campos extras; o mobile não depende de campos não documentados.
4. **Backend:** implementar exatamente estas respostas e escrever testes que validem o formato (nomes e tipos) de cada uma.
5. **Mobile:** a `FakeSimulaCredApi` deve devolver **exatamente** os exemplos deste arquivo. Assim o app funciona igual com o backend falso e com o real.

---

## 1. Convenções gerais

| Item | Regra |
|---|---|
| URL base (desenvolvimento) | `http://localhost:8080` no computador · `http://10.0.2.2:8080` no emulador Android · `http://<IP-do-computador>:8080` no celular físico (mesma rede Wi-Fi) |
| Prefixo | `/api/v1` |
| Formato | JSON, UTF-8. Requisições com corpo enviam `Content-Type: application/json` |
| Nomes de campos | `camelCase` |
| **Valores em dinheiro** | **String** com ponto decimal e exatamente 2 casas: `"517.12"`. Nunca número JSON. Evita perda de precisão no `Double` do Kotlin e do JavaScript |
| **Taxas** | **String** com ponto decimal e 4 casas: `"0.0349"` (= 3,49% ao mês) |
| Inteiros | Número JSON: `"prazo": 12` |
| Identificadores | UUID v4 em texto: `"6f1c2a9e-3b7d-4e1a-9c2f-8d5e4b3a2f10"` |
| Datas e horas | ISO-8601 em UTC com `Z`: `"2026-11-04T23:15:42Z"` |
| Campos nulos | Enviados como `null` (não omitidos) quando a tabela disser "pode ser `null`" |
| Listas vazias | `[]`, nunca `null` |
| Autenticação | **MVP: nenhuma.** Extensão: seção 7 |

### Implementação sugerida

- **Backend (Jackson):** `@JsonFormat(shape = JsonFormat.Shape.STRING)` nos campos `BigDecimal` dos DTOs de resposta e `BigDecimal` com `setScale(2, RoundingMode.HALF_EVEN)` antes de serializar. Datas como `Instant`.
- **Mobile (kotlinx.serialization):** receber dinheiro e taxa como `String` no DTO e converter para `BigDecimal` no mapeador para o modelo de domínio.

---

## 2. Enumerações

### `StatusSimulacao`

| Valor | Significado |
|---|---|
| `PRE_APROVADA` | Passou em todas as regras de elegibilidade |
| `RECUSADA` | Falhou em pelo menos uma regra; o motivo está em `motivosRecusa` |

### `MotivoRecusa`

| Valor | Quando ocorre | Texto sugerido para o app |
|---|---|---|
| `VALOR_FORA_DA_FAIXA` | valor < 500,00 ou > 30.000,00 | "O valor precisa ficar entre R$ 500 e R$ 30.000." |
| `PRAZO_NAO_PERMITIDO` | prazo < 6 ou > 36 | "O prazo precisa ficar entre 6 e 36 meses." |
| `COMPROMETIMENTO_ACIMA_DO_LIMITE` | parcela > 30% da renda mensal | "A parcela passaria de 30% da sua renda." |
| `IDADE_LIMITE_EXCEDIDA` | idade + (prazo ÷ 12) > 80 | "O contrato terminaria depois do limite de idade." |

> O texto exibido ao cliente é responsabilidade do **app**. A API só devolve o código. Assim, mudar a frase não exige mudar o backend.

---

## 3. Endpoints

| # | Método | Rota | Resumo | Sucesso |
|---|---|---|---|---|
| 3.1 | `POST` | `/api/v1/simulacoes` | Cria uma simulação | `201` |
| 3.2 | `GET` | `/api/v1/simulacoes/{id}` | Busca uma simulação completa | `200` |
| 3.3 | `GET` | `/api/v1/simulacoes?clienteId=&pagina=&tamanho=` | Histórico paginado | `200` |
| 3.4 | `POST` | `/api/v1/simulacoes/{id}/explicacao` | Explicação em linguagem simples (IA) | `200` |

---

### 3.1 `POST /api/v1/simulacoes`: criar simulação

#### Requisição

| Campo | Tipo | Obrigatório | Validação (falha → `400 DADOS_INVALIDOS`) |
|---|---|---|---|
| `clienteId` | string | sim | 1 a 50 caracteres |
| `valor` | string (dinheiro) | sim | número válido, > 0 e ≤ 1000000.00, no máximo 2 casas |
| `prazo` | inteiro | sim | 1 a 120 |
| `rendaMensal` | string (dinheiro) | sim | número válido, > 0, no máximo 2 casas |
| `idade` | inteiro | sim | 18 a 100 |

> **Validação ≠ elegibilidade.** Um pedido de R$ 40.000 é **válido** (passa na validação) e é **recusado** pela regra `VALOR_FORA_DA_FAIXA` (`201` com `status: RECUSADA`). Só dados malformados ou absurdos geram `400`.

```json
{
  "clienteId": "cli-001",
  "valor": "5000.00",
  "prazo": 12,
  "rendaMensal": "3200.00",
  "idade": 58
}
```

#### Resposta `201 Created`

Cabeçalho `Location: /api/v1/simulacoes/{id}`.

| Campo | Tipo | Pode ser `null`? | Descrição |
|---|---|---|---|
| `id` | UUID | não | Identificador da simulação |
| `clienteId` | string | não | Ecoa a requisição |
| `valor` | dinheiro | não | Ecoa a requisição, com 2 casas |
| `prazo` | inteiro | não | Ecoa a requisição |
| `status` | `StatusSimulacao` | não | |
| `motivosRecusa` | `MotivoRecusa[]` | não | **Todos** os motivos que falharam; `[]` se pré-aprovada |
| `taxaMensal` | taxa | **sim** | `null` quando o prazo está fora da tabela de taxas (6 a 36) |
| `valorParcela` | dinheiro | **sim** | Parcela padrão (1ª à penúltima). `null` junto com `taxaMensal` |
| `totalPago` | dinheiro | **sim** | Soma de todas as parcelas do cronograma. `null` junto com `taxaMensal` |
| `totalJuros` | dinheiro | **sim** | `totalPago − valor`. `null` junto com `taxaMensal` |
| `parcelas` | `Parcela[]` | não | Cronograma completo; `[]` quando `taxaMensal` é `null` |
| `criadaEm` | data-hora | não | Momento da criação, em UTC |

**`Parcela`**

| Campo | Tipo | Descrição |
|---|---|---|
| `numero` | inteiro | 1 até `prazo` |
| `valor` | dinheiro | Valor desta parcela (a última pode diferir alguns centavos) |
| `juros` | dinheiro | `saldo anterior × taxaMensal`, arredondado |
| `amortizacao` | dinheiro | `valor − juros` |
| `saldo` | dinheiro | Saldo devedor após esta parcela; na última, `"0.00"` |

**Regra de cálculo (Tabela Price):**
- `valorParcela = valor × i / (1 − (1 + i)^−n)`, arredondado para 2 casas com `HALF_EVEN`;
- da 1ª à penúltima parcela: `juros = saldo × i` (arredondado), `amortizacao = valorParcela − juros`;
- **última parcela:** `amortizacao = saldo restante`, `valor = amortizacao + juros`, para o saldo fechar em `"0.00"`;
- `totalPago` = soma dos `valor` de todas as parcelas.

**Regra quando o prazo está fora da tabela de taxas (< 6 ou > 36):** `status = RECUSADA`, `motivosRecusa` contém `PRAZO_NAO_PERMITIDO`, os campos de cálculo vêm `null`, `parcelas = []`, e a regra `COMPROMETIMENTO_ACIMA_DO_LIMITE` **não é avaliada** (não há parcela). As demais regras são avaliadas normalmente.

**Tabela de taxas (fictícia):**

| Prazo | `taxaMensal` |
|---|---|
| 6 a 12 | `"0.0349"` |
| 13 a 24 | `"0.0399"` |
| 25 a 36 | `"0.0449"` |

#### Exemplo A: pré-aprovada (caso de teste canônico)

```json
{
  "id": "6f1c2a9e-3b7d-4e1a-9c2f-8d5e4b3a2f10",
  "clienteId": "cli-001",
  "valor": "5000.00",
  "prazo": 12,
  "status": "PRE_APROVADA",
  "motivosRecusa": [],
  "taxaMensal": "0.0349",
  "valorParcela": "517.12",
  "totalPago": "6205.38",
  "totalJuros": "1205.38",
  "parcelas": [
    { "numero": 1,  "valor": "517.12", "juros": "174.50", "amortizacao": "342.62", "saldo": "4657.38" },
    { "numero": 2,  "valor": "517.12", "juros": "162.54", "amortizacao": "354.58", "saldo": "4302.80" },
    "... parcelas 3 a 11 ...",
    { "numero": 12, "valor": "517.06", "juros": "17.44",  "amortizacao": "499.62", "saldo": "0.00" }
  ],
  "criadaEm": "2026-11-04T23:15:42Z"
}
```

> A linha `"... parcelas 3 a 11 ..."` é só abreviação deste documento. A resposta real traz as 12 parcelas como objetos.
> **Os valores das parcelas 1, 2 e 12, do `valorParcela`, do `totalPago` e do `totalJuros` são obrigatórios nos testes dos dois projetos.**

#### Exemplo B: recusada por comprometimento de renda

Requisição igual à do exemplo A, mas com `"rendaMensal": "1500.00"` (30% = R$ 450,00 < R$ 517,12).

```json
{
  "id": "a3d9e7c1-5b2f-4c8a-9e1d-7f6b5a4c3d21",
  "clienteId": "cli-001",
  "valor": "5000.00",
  "prazo": 12,
  "status": "RECUSADA",
  "motivosRecusa": ["COMPROMETIMENTO_ACIMA_DO_LIMITE"],
  "taxaMensal": "0.0349",
  "valorParcela": "517.12",
  "totalPago": "6205.38",
  "totalJuros": "1205.38",
  "parcelas": [ "... 12 parcelas, iguais às do exemplo A ..." ],
  "criadaEm": "2026-11-04T23:16:05Z"
}
```

> Recusada **continua sendo `201`**: a simulação foi criada com sucesso; o resultado dela é que foi negativo.

#### Exemplo C: recusada por prazo fora da tabela

Requisição com `"prazo": 48`, `"valor": "40000.00"`, demais campos iguais ao exemplo A.

```json
{
  "id": "c7e2b4a6-1d3f-4e5a-8b9c-0d1e2f3a4b56",
  "clienteId": "cli-001",
  "valor": "40000.00",
  "prazo": 48,
  "status": "RECUSADA",
  "motivosRecusa": ["VALOR_FORA_DA_FAIXA", "PRAZO_NAO_PERMITIDO"],
  "taxaMensal": null,
  "valorParcela": null,
  "totalPago": null,
  "totalJuros": null,
  "parcelas": [],
  "criadaEm": "2026-11-04T23:17:30Z"
}
```

**Ordem de `motivosRecusa`:** sempre a ordem da tabela da seção 2 (`VALOR_FORA_DA_FAIXA`, `PRAZO_NAO_PERMITIDO`, `COMPROMETIMENTO_ACIMA_DO_LIMITE`, `IDADE_LIMITE_EXCEDIDA`), para os testes serem determinísticos.

---

### 3.2 `GET /api/v1/simulacoes/{id}`: buscar simulação

- **`200 OK`:** mesmo formato da resposta de 3.1.
- **`404 Not Found`:** erro `SIMULACAO_NAO_ENCONTRADA` (seção 4).
- **`400 Bad Request`:** `id` não é um UUID válido → `DADOS_INVALIDOS`.

---

### 3.3 `GET /api/v1/simulacoes`: histórico paginado

| Parâmetro de consulta | Obrigatório | Padrão | Validação |
|---|---|---|---|
| `clienteId` | sim | | 1 a 50 caracteres |
| `pagina` | não | `0` | ≥ 0 (começa em zero) |
| `tamanho` | não | `10` | 1 a 50 |

Ordenação fixa: `criadaEm` **decrescente** (mais recente primeiro).

> **Não devolver o objeto `Page` padrão do Spring.** O formato dele muda entre versões. Mapear para o formato abaixo.

#### Resposta `200 OK`

```json
{
  "conteudo": [
    {
      "id": "6f1c2a9e-3b7d-4e1a-9c2f-8d5e4b3a2f10",
      "valor": "5000.00",
      "prazo": 12,
      "status": "PRE_APROVADA",
      "valorParcela": "517.12",
      "criadaEm": "2026-11-04T23:15:42Z"
    }
  ],
  "pagina": 0,
  "tamanho": 10,
  "totalElementos": 1,
  "totalPaginas": 1
}
```

**Item resumido (`conteudo[]`):** `id`, `valor`, `prazo`, `status`, `valorParcela` (pode ser `null`) e `criadaEm`. **Sem** `parcelas`; para o detalhe, o app chama 3.2.

`clienteId` sem nenhuma simulação → `200` com `"conteudo": []`, `"totalElementos": 0`, `"totalPaginas": 0`. **Não é `404`.**

---

### 3.4 `POST /api/v1/simulacoes/{id}/explicacao`: explicação por IA

Sem corpo na requisição.

#### Resposta `200 OK`

| Campo | Tipo | Descrição |
|---|---|---|
| `simulacaoId` | UUID | |
| `texto` | string | Explicação em português simples, até cerca de 5 frases |
| `geradaPorIa` | booleano | `true` = veio do modelo; `false` = texto de fallback por template |
| `geradaEm` | data-hora | |

```json
{
  "simulacaoId": "6f1c2a9e-3b7d-4e1a-9c2f-8d5e4b3a2f10",
  "texto": "Você pediu R$ 5.000,00 para pagar em 12 meses. Cada parcela fica em R$ 517,12, e a última sai por R$ 517,06. No total, você vai pagar R$ 6.205,38, sendo R$ 1.205,38 de juros. Pelas informações enviadas, a simulação foi pré-aprovada.",
  "geradaPorIa": true,
  "geradaEm": "2026-11-04T23:15:50Z"
}
```

**Comportamento obrigatório:**

| Situação | Resposta |
|---|---|
| IA respondeu | `200`, `geradaPorIa: true` |
| IA fora do ar, com erro ou acima de **20 s** | `200`, `geradaPorIa: false`, texto de fallback. **Nunca `500` por falha da IA** |
| Explicação já gerada para esta simulação | `200` com **o mesmo texto salvo** (não chama a IA de novo) |
| Simulação inexistente | `404 SIMULACAO_NAO_ENCONTRADA` |

**Governança (backend):** o prompt enviado ao modelo contém **somente** `valor`, `prazo`, `taxaMensal`, `valorParcela`, a última parcela, `totalPago`, `totalJuros`, `status` e `motivosRecusa`. **Nunca** `clienteId`, `rendaMensal`, `idade` ou qualquer outro dado pessoal.

**Mobile:** timeout de leitura de **30 s** neste endpoint (o modelo local pode demorar). Exibir o selo "Texto gerado por IA. Confira os valores acima." quando `geradaPorIa` for `true`.

---

## 4. Formato de erro (todos os endpoints)

```json
{
  "status": 400,
  "erro": "DADOS_INVALIDOS",
  "mensagem": "Um ou mais campos são inválidos.",
  "detalhes": [
    "prazo: deve estar entre 1 e 120",
    "rendaMensal: é obrigatório"
  ],
  "timestamp": "2026-11-04T23:18:00Z"
}
```

| Campo | Tipo | Descrição |
|---|---|---|
| `status` | inteiro | Igual ao código HTTP |
| `erro` | string | Código estável (tabela abaixo): **o app decide pelo código, nunca pela mensagem** |
| `mensagem` | string | Texto legível, em português |
| `detalhes` | string[] | `"campo: problema"` para erros de validação; `[]` nos demais |
| `timestamp` | data-hora | |

| HTTP | `erro` | Quando |
|---|---|---|
| 400 | `DADOS_INVALIDOS` | Validação falhou, JSON malformado, UUID inválido, parâmetro inválido |
| 404 | `SIMULACAO_NAO_ENCONTRADA` | `id` inexistente |
| 401 | `NAO_AUTENTICADO` | **Só na extensão com JWT** |
| 500 | `ERRO_INTERNO` | Qualquer erro inesperado. **Não expor stack trace nem mensagem de exceção** |

---

## 5. Comportamento esperado do app por situação

| Situação | O app deve |
|---|---|
| `201` com `PRE_APROVADA` | Ir para a tela Resultado e salvar no Room |
| `201` com `RECUSADA` | Ir para a tela Resultado mostrando os motivos (textos da seção 2) e salvar no Room |
| `400 DADOS_INVALIDOS` | Ficar na tela Simular e mostrar os `detalhes` junto aos campos (a validação local deve evitar chegar aqui) |
| `404` | Mensagem "Simulação não encontrada" e voltar |
| `500` ou erro de rede/timeout no `POST /simulacoes` | Usar a `CalculadoraLocal`, marcar `calculadaNoAparelho = true` e avisar o usuário |
| Explicação com erro de rede | Mensagem amigável + botão "Tentar de novo" |

> A `CalculadoraLocal` do app deve produzir **os mesmos números** do exemplo A (seção 3.1). Use os mesmos valores nos testes unitários dos dois projetos.

---

## 6. Documentação viva

- O backend expõe o OpenAPI em `GET /v3/api-docs` e o Swagger UI em `/swagger-ui.html`.
- Quando o backend estiver pronto, o usuário salva o `openapi.json` gerado na pasta do app (`docs/openapi.json`) como referência.
- **Se o `openapi.json` divergir deste arquivo, isso é um bug do backend**, não uma nova versão do contrato.

---

## 7. Extensão futura: autenticação (não implementar no MVP)

| Endpoint | Requisição | Resposta |
|---|---|---|
| `POST /auth/login` | `{ "usuario": "string", "senha": "string" }` | `200` `{ "token": "string", "expiraEm": "data-hora" }` |

- Rotas `/api/v1/**` passam a exigir `Authorization: Bearer <token>`; sem token → `401 NAO_AUTENTICADO`.
- O `clienteId` passa a vir do token, não do corpo. **Essa mudança será feita como v2 deste contrato**, decidida pelo usuário.

---

## 8. Histórico do contrato

| Versão | Data | Mudança |
|---|---|---|
| 1.0 | 2026-11-04 | Versão inicial |
