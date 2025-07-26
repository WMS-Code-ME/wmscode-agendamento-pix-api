# Teste de Carga - API de Agendamentos PIX

Este diretório contém scripts para executar testes de carga na API de Agendamentos PIX.

## 📋 Características

- ✅ **Limite de 50 requests por minuto** (rate limiting)
- ✅ **Banco H2 em memória** (isolamento completo)
- ✅ **Limpeza automática** de dados após teste
- ✅ **Relatórios detalhados** no console
- ✅ **Teste de todos os endpoints** principais
- ✅ **Controle de taxa** automático

## 🚀 Execução Rápida

### Opção 1: Script Automatizado (Recomendado)

```bash
./scripts/run-load-test.sh
```

Este script:
- Verifica dependências
- Inicia a aplicação com H2
- Executa o teste de carga
- Para a aplicação automaticamente
- Mostra resultados no console

### Opção 2: Execução Manual

1. **Iniciar aplicação em modo de teste de carga:**
```bash
mvn quarkus:dev -Dquarkus.profile=load-test
```

2. **Executar teste de carga:**
```bash
python3 scripts/load-test.py --url http://localhost:8081 --duration 5
```

## 📊 Endpoints Testados

### Autenticação
- `POST /api/v1/auth/token` - Geração de token JWT

### Agendamentos PIX
- `POST /api/v1/pix/agendamentos` - Criar agendamento
- `GET /api/v1/pix/agendamentos/{id}` - Buscar por ID
- `GET /api/v1/pix/agendamentos` - Listar todos
- `DELETE /api/v1/pix/agendamentos/{id}` - Cancelar agendamento

### Webhooks
- `POST /api/v1/webhooks` - Criar webhook
- `GET /api/v1/webhooks/{id}` - Buscar por ID
- `GET /api/v1/webhooks` - Listar todos
- `PUT /api/v1/webhooks/{id}/ativar` - Ativar webhook
- `PUT /api/v1/webhooks/{id}/inativar` - Inativar webhook
- `DELETE /api/v1/webhooks/{id}` - Deletar webhook

## ⚙️ Configurações

### Rate Limiting
- **Limite:** 50 requests por minuto
- **Controle:** Automático via script Python
- **Comportamento:** Aguarda quando limite é atingido

### Banco de Dados
- **Tipo:** H2 em memória
- **Schema:** Recriado a cada execução
- **Isolamento:** Completo (não afeta dados de produção)

### Performance
- **Logs:** Reduzidos para melhor performance
- **Scheduler:** Desabilitado durante teste
- **Flyway:** Desabilitado durante teste

## 📈 Resultados

O teste gera relatórios detalhados incluindo:

- **Tempo total** de execução
- **Total de requests** realizados
- **Requests por segundo/minuto**
- **Distribuição por status code**
- **Estatísticas de tempo de resposta:**
  - Média
  - Mediana
  - Mínimo/Máximo
  - Desvio padrão

### Exemplo de Saída

```
📈 RESULTADOS DO TESTE DE CARGA
============================================================
⏱️  Tempo total: 300.45 segundos
📊 Total de requests: 245
🚀 Requests por segundo: 0.82
⏳ Requests por minuto: 49.2

📋 Distribuição por Status Code:
  200: 180 requests (73.5%)
    Média: 45.23ms | Min: 12.45ms | Max: 234.67ms
  201: 45 requests (18.4%)
    Média: 67.89ms | Min: 23.12ms | Max: 345.21ms
  204: 20 requests (8.2%)
    Média: 34.56ms | Min: 8.90ms | Max: 123.45ms

📊 Estatísticas Gerais:
  Tempo médio de resposta: 48.76ms
  Tempo mediano de resposta: 42.34ms
  Tempo mínimo: 8.90ms
  Tempo máximo: 345.21ms
  Desvio padrão: 45.67ms
```

## 🔧 Personalização

### Alterar Duração do Teste

```bash
# Via script automatizado (editar scripts/run-load-test.sh)
DURATION=10

# Via linha de comando
python3 scripts/load-test.py --duration 10
```

### Alterar URL da API

```bash
# Via script automatizado (editar scripts/run-load-test.sh)
API_URL="http://localhost:8082"

# Via linha de comando
python3 scripts/load-test.py --url http://localhost:8082
```

### Alterar Rate Limit

Editar `scripts/load-test.py`:
```python
self.max_requests_per_minute = 100  # Alterar para 100 requests/min
```

## 📋 Pré-requisitos

### Python
```bash
# Ubuntu/Debian
sudo apt-get install python3 python3-pip

# macOS
brew install python3

# Windows
# Baixar de https://python.org
```

### Dependências Python
```bash
pip3 install requests
```

### Java/Maven
- Java 17+
- Maven 3.6+

## 🐛 Troubleshooting

### Erro: "Python 3 não está instalado"
```bash
# Ubuntu/Debian
sudo apt-get install python3 python3-pip

# Verificar instalação
python3 --version
```

### Erro: "Não foi possível conectar com a API"
- Verificar se a aplicação está rodando na porta correta
- Verificar se não há firewall bloqueando
- Verificar logs da aplicação

### Erro: "Rate limit atingido"
- Normal durante o teste
- O script aguarda automaticamente
- Pode aumentar o limite editando `scripts/load-test.py`

### Performance Baixa
- Verificar recursos do sistema (CPU, RAM)
- Reduzir duração do teste
- Verificar logs da aplicação para gargalos

## 📁 Estrutura de Arquivos

```
├── scripts/
│   ├── load-test.py                    # Script principal de teste de carga
│   ├── quick-test.py                   # Script de teste rápido
│   └── run-load-test.sh               # Script automatizado de execução
├── docs/
│   └── README-LOAD-TEST.md            # Este arquivo
└── src/test/resources/
    └── application-load-test.properties  # Configuração H2 para testes
```

## 🔒 Segurança

- ✅ **Isolamento completo** via H2 em memória
- ✅ **Dados temporários** (apagados após teste)
- ✅ **Sem impacto** em dados de produção
- ✅ **Rate limiting** para não sobrecarregar API

## 📞 Suporte

Para dúvidas ou problemas:
1. Verificar logs da aplicação
2. Verificar pré-requisitos
3. Consultar este README
4. Verificar configurações de rede/firewall 