# API de Agendamentos PIX

API desenvolvida em Quarkus para agendamento e processamento automático de pagamentos PIX.

## Arquitetura

A aplicação segue uma arquitetura em camadas:

- **Experience**: Controllers REST que expõem os endpoints da API
- **Process**: Serviços de negócio que implementam a lógica da aplicação
- **System**: Entidades JPA e repositórios para persistência de dados
- **Common**: DTOs e mappers compartilhados entre as camadas

### Diagramas de Arquitetura

#### Modelo C4
- [**Contexto**](docs/diagramas/arquitetura/Contexto.png) - Visão geral do sistema e suas interações externas
- [**Containers**](docs/diagramas/arquitetura/Conteiners.png) - Componentes de alto nível da aplicação
- [**Componentes**](docs/diagramas/arquitetura/Componentes.png) - Estrutura interna dos containers

#### Diagramas de Sequência
- [**Agendamento PIX**](docs/diagramas/sequencia/Agendamento%20Pix.png) - Fluxo de criação de agendamento
- [**Autenticação**](docs/diagramas/sequencia/Autenticação.png) - Processo de autenticação e autorização
- [**Envio Pagamento**](docs/diagramas/sequencia/Envio%20Pagamento.png) - Fluxo de processamento de pagamento

## Tecnologias Utilizadas

- **Quarkus 3.13.0**: Framework Java nativo para cloud
- **Hibernate ORM + Panache**: Persistência de dados
- **PostgreSQL**: Banco de dados
- **Flyway**: Controle de migrações
- **Lombok**: Redução de boilerplate
- **MapStruct**: Mapeamento de objetos
- **JUnit 5**: Testes unitários
- **REST Assured**: Testes de integração

## Endpoints da API

### Agendamentos PIX

- `POST /api/v1/pix/agendamentos` - Criar novo agendamento
- `GET /api/v1/pix/agendamentos` - Listar todos os agendamentos
- `GET /api/v1/pix/agendamentos/{id}` - Buscar agendamento por ID
- `GET /api/v1/pix/agendamentos/status/{status}` - Buscar por status
- `GET /api/v1/pix/agendamentos/chave/{chavePix}` - Buscar por chave PIX
- `DELETE /api/v1/pix/agendamentos/{id}` - Cancelar agendamento

### Pagamentos PIX

- `POST /api/v1/pix/pagamentos` - Processar pagamento PIX imediato

## Configuração do Banco de Dados

1. Instale o PostgreSQL
2. Crie o banco de dados:
```sql
CREATE DATABASE pix_scheduler;
CREATE USER pix_user WITH PASSWORD 'pix_password';
GRANT ALL PRIVILEGES ON DATABASE pix_scheduler TO pix_user;
```

3. Configure as credenciais no `application.properties`

## Executando a Aplicação

### Desenvolvimento
```bash
./mvnw quarkus:dev
```

### Produção
```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## Executando os Testes

```bash
./mvnw test
```

## Teste de Carga

Para executar testes de carga na API:

### Teste Rápido (Verificação)
```bash
python3 scripts/quick-test.py
```

### Teste de Carga Completo
```bash
./scripts/run-load-test.sh
```

**Características:**
- ✅ Limite de 50 requests por minuto
- ✅ Banco H2 em memória (isolamento completo)
- ✅ Teste de todos os endpoints principais
- ✅ Relatórios detalhados no console

**Para mais detalhes, consulte:** [Teste de Carga](docs/README-LOAD-TEST.md)

## Funcionalidades

### Agendamento de PIX
- Criação de agendamentos com data e hora específicas
- Validação de dados de entrada
- Controle de status (AGENDADO, PROCESSANDO, PROCESSADO, ERRO, CANCELADO)

### Processamento Automático
- Scheduler que executa a cada minuto
- Processamento automático de agendamentos vencidos
- Geração de códigos de transação únicos
- Geração de QR Code PIX

### Pagamento Imediato
- Processamento de pagamentos PIX em tempo real
- Geração de QR Code para pagamento
- Retorno de dados da transação

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/wmscode/
│   │   ├── common/
│   │   │   ├── dto/           # DTOs de requisição e resposta
│   │   │   └── mapper/        # Mappers MapStruct
│   │   ├── experience/
│   │   │   └── controller/    # Controllers REST
│   │   ├── process/
│   │   │   └── service/       # Serviços de negócio
│   │   └── system/
│   │       ├── entity/        # Entidades JPA
│   │       └── repository/    # Repositórios
│   └── resources/
│       ├── application.properties
│       └── db/migration/      # Migrações Flyway
└── test/
    └── java/br/com/wmscode/
        ├── process/service/    # Testes unitários
        └── experience/controller/ # Testes de integração
```

## Documentação da API

Acesse a documentação Swagger em: http://localhost:8080/q/swagger-ui

## Status dos Agendamentos

- **AGENDADO**: Agendamento criado, aguardando processamento
- **PROCESSANDO**: Agendamento sendo processado
- **PROCESSADO**: Pagamento realizado com sucesso
- **ERRO**: Erro durante o processamento
- **CANCELADO**: Agendamento cancelado pelo usuário

