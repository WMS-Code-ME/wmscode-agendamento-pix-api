# Exemplos de Requisições - API de Agendamentos PIX

## Criar Agendamento PIX

```bash
curl -X POST http://localhost:8080/api/v1/pix/agendamentos \
  -H "Content-Type: application/json" \
  -d '{
    "chavePix": "joao.silva@email.com",
    "nomeBeneficiario": "João Silva",
    "valor": 150.50,
    "descricao": "Pagamento de serviços",
    "dataAgendamento": "2024-01-15T14:30:00",
    "observacao": "Pagamento agendado para segunda-feira"
  }'
```

**Resposta esperada:**
```json
{
  "id": 1,
  "chavePix": "joao.silva@email.com",
  "nomeBeneficiario": "João Silva",
  "valor": 150.50,
  "descricao": "Pagamento de serviços",
  "dataAgendamento": "2024-01-15T14:30:00",
  "observacao": "Pagamento agendado para segunda-feira",
  "status": "AGENDADO",
  "dataCriacao": "2024-01-10T10:00:00",
  "dataProcessamento": null,
  "codigoTransacao": null
}
```

## Listar Todos os Agendamentos

```bash
curl -X GET http://localhost:8080/api/v1/pix/agendamentos
```

## Buscar Agendamento por ID

```bash
curl -X GET http://localhost:8080/api/v1/pix/agendamentos/1
```

## Buscar Agendamentos por Status

```bash
curl -X GET http://localhost:8080/api/v1/pix/agendamentos/status/AGENDADO
```

## Buscar Agendamentos por Chave PIX

```bash
curl -X GET http://localhost:8080/api/v1/pix/agendamentos/chave/joao.silva@email.com
```

## Cancelar Agendamento

```bash
curl -X DELETE http://localhost:8080/api/v1/pix/agendamentos/1
```

## Processar Pagamento PIX Imediato

```bash
curl -X POST http://localhost:8080/api/v1/pix/pagamentos \
  -H "Content-Type: application/json" \
  -d '{
    "chavePix": "maria.santos@email.com",
    "nomeBeneficiario": "Maria Santos",
    "valor": 75.25,
    "descricao": "Pagamento imediato",
    "observacao": "Pagamento de teste"
  }'
```

**Resposta esperada:**
```json
{
  "codigoTransacao": "PIX1704891600000abc12345",
  "chavePix": "maria.santos@email.com",
  "nomeBeneficiario": "Maria Santos",
  "valor": 75.25,
  "descricao": "Pagamento imediato",
  "observacao": "Pagamento de teste",
  "status": "PROCESSADO",
  "dataProcessamento": "2024-01-10T10:00:00",
  "qrCode": "00020126580014br.gov.bcb.pix0136maria.santos@email.com52040000530398654075.255802BR5913Maria Santos6006Pagamento imediato62070503***6304",
  "qrCodeBase64": "MDAwMjAxMjY1ODAwMTRici5nb3YuYmNiLnBpeDAxMzZtYXJpYS5zYW50b3NAZW1haWwuY29tNTIwNDAwMDA1MzAzOTg2NTQwNzUuMjU1ODAyQlI1OTEzTWFyaWEgU2FudG9zNjAwNlBhZ2FtZW50byBpbWVkaWF0bzYyMDcwNTAzKioqNjMwNA=="
}
```

## Exemplos com diferentes tipos de chave PIX

### Chave PIX - Email
```json
{
  "chavePix": "usuario@exemplo.com",
  "nomeBeneficiario": "Usuário Exemplo",
  "valor": 100.00,
  "descricao": "Pagamento via email",
  "dataAgendamento": "2024-01-15T15:00:00"
}
```

### Chave PIX - CPF
```json
{
  "chavePix": "12345678901",
  "nomeBeneficiario": "João da Silva",
  "valor": 250.75,
  "descricao": "Pagamento via CPF",
  "dataAgendamento": "2024-01-16T09:30:00"
}
```

### Chave PIX - Celular
```json
{
  "chavePix": "+5511999999999",
  "nomeBeneficiario": "Maria Oliveira",
  "valor": 89.90,
  "descricao": "Pagamento via celular",
  "dataAgendamento": "2024-01-17T12:00:00"
}
```

## Testando com Postman

1. Importe a coleção do Postman (se disponível)
2. Configure a variável de ambiente `baseUrl` como `http://localhost:8080`
3. Execute os requests na ordem:
   - Criar agendamento
   - Listar agendamentos
   - Buscar por ID
   - Processar pagamento imediato

## Testando com Insomnia

1. Crie um novo projeto
2. Configure a URL base como `http://localhost:8080`
3. Use os exemplos acima para criar as requisições

## Validações

A API valida os seguintes campos:

- **chavePix**: Obrigatório, não pode estar vazio
- **nomeBeneficiario**: Obrigatório, não pode estar vazio
- **valor**: Obrigatório, deve ser maior que zero
- **descricao**: Obrigatório, máximo 140 caracteres
- **dataAgendamento**: Obrigatório, deve ser uma data futura
- **observacao**: Opcional, máximo 500 caracteres 