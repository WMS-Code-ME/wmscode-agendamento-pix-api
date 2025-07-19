# API de Webhooks - Documentação

## Visão Geral

A API de Webhooks permite cadastrar endpoints que receberão notificações automáticas quando pagamentos PIX agendados forem processados pelo sistema. O BatchService executa diariamente às 8h e envia os pagamentos para todos os webhooks ativos.

## Endpoints

### 1. Criar Webhook

**POST** `/api/v1/webhooks`

Cria um novo webhook para receber notificações de pagamentos.

**Request Body:**
```json
{
    "url": "https://api.exemplo.com/webhook",
    "login": "usuario",
    "senha": "senha123",
    "descricao": "Webhook para receber pagamentos PIX"
}
```

**Response (201):**
```json
{
    "id": 1,
    "url": "https://api.exemplo.com/webhook",
    "login": "usuario",
    "descricao": "Webhook para receber pagamentos PIX",
    "status": "ATIVO",
    "dataCriacao": "2025-07-19T11:30:00",
    "dataAtualizacao": null
}
```

### 2. Listar Todos os Webhooks

**GET** `/api/v1/webhooks`

Retorna todos os webhooks cadastrados.

**Response (200):**
```json
[
    {
        "id": 1,
        "url": "https://api.exemplo.com/webhook",
        "login": "usuario",
        "descricao": "Webhook para receber pagamentos PIX",
        "status": "ATIVO",
        "dataCriacao": "2025-07-19T11:30:00",
        "dataAtualizacao": null
    }
]
```

### 3. Listar Webhooks Ativos

**GET** `/api/v1/webhooks/ativos`

Retorna apenas os webhooks com status ATIVO.

### 4. Buscar Webhook por ID

**GET** `/api/v1/webhooks/{id}`

Retorna um webhook específico.

**Response (200):**
```json
{
    "id": 1,
    "url": "https://api.exemplo.com/webhook",
    "login": "usuario",
    "descricao": "Webhook para receber pagamentos PIX",
    "status": "ATIVO",
    "dataCriacao": "2025-07-19T11:30:00",
    "dataAtualizacao": null
}
```

### 5. Atualizar Webhook

**PUT** `/api/v1/webhooks/{id}`

Atualiza um webhook existente.

**Request Body:**
```json
{
    "url": "https://api.novo.com/webhook",
    "login": "novo_usuario",
    "senha": "nova_senha",
    "descricao": "Webhook atualizado"
}
```

### 6. Deletar Webhook

**DELETE** `/api/v1/webhooks/{id}`

Remove um webhook do sistema.

**Response (204):** Sem conteúdo

### 7. Ativar Webhook

**PUT** `/api/v1/webhooks/{id}/ativar`

Ativa um webhook que estava inativo.

### 8. Inativar Webhook

**PUT** `/api/v1/webhooks/{id}/inativar`

Inativa um webhook (não receberá mais notificações).

## Validações

- **URL**: Deve começar com `http://` ou `https://`
- **Login**: Campo obrigatório
- **Senha**: Campo obrigatório
- **Descrição**: Campo opcional

## Payload de Notificação

Quando um pagamento PIX agendado é processado, o sistema envia um POST para cada webhook ativo com o seguinte payload:

```json
{
    "idAgendamento": 123,
    "codigoTransacao": "PIX20250719123456789",
    "chavePix": "usuario@email.com",
    "nomeBeneficiario": "João Silva",
    "valor": 150.00,
    "descricao": "Pagamento agendado",
    "dataAgendamento": "2025-07-19T10:00:00",
    "dataProcessamento": "2025-07-19T08:00:00",
    "status": "PROCESSADO"
}
```

## Autenticação

O sistema utiliza autenticação HTTP Basic para enviar as notificações:

- **Username**: Login cadastrado no webhook
- **Password**: Senha cadastrada no webhook

## Exemplos de Uso

### Criar Webhook com cURL

```bash
curl -X POST http://localhost:8080/api/v1/webhooks \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://api.exemplo.com/webhook",
    "login": "usuario",
    "senha": "senha123",
    "descricao": "Webhook de teste"
  }'
```

### Listar Webhooks Ativos

```bash
curl -X GET http://localhost:8080/api/v1/webhooks/ativos
```

### Ativar Webhook

```bash
curl -X PUT http://localhost:8080/api/v1/webhooks/1/ativar
```

## BatchService

O BatchService executa automaticamente todos os dias às 8h e:

1. Busca agendamentos PIX vencidos com status AGENDADO
2. Marca cada agendamento como PROCESSANDO
3. Envia notificação para todos os webhooks ativos
4. Atualiza o status do agendamento para PROCESSADO ou ERRO

### Configuração do Scheduler

O scheduler está configurado com a expressão cron: `0 0 8 * * ?`

- **0 0 8**: Executa às 8h00
- **\* \* ?**: Todos os dias da semana, todos os meses, qualquer dia da semana

## Tratamento de Erros

- Se um webhook não responder (timeout ou erro), o sistema continua processando os outros webhooks
- Se nenhum webhook responder com sucesso, o agendamento é marcado como ERRO
- Logs detalhados são gerados para facilitar o debug

## Segurança

- Senhas são armazenadas em texto plano no banco (em produção, considere usar criptografia)
- URLs devem usar HTTPS em produção
- Autenticação HTTP Basic é utilizada para as notificações 