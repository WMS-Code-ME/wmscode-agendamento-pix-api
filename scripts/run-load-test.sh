#!/bin/bash

# Script para executar testes de carga na API de Agendamentos PIX
# Usa H2 como banco de dados para isolamento

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configurações
API_URL="http://localhost:8081"
DURATION=5
PYTHON_SCRIPT="$(dirname "$0")/load-test.py"

echo -e "${BLUE}🚀 Teste de Carga - API de Agendamentos PIX${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

# Verificar se Python está instalado
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}❌ Python 3 não está instalado${NC}"
    echo "Instale o Python 3 para executar os testes de carga"
    exit 1
fi

# Verificar se o script Python existe
if [ ! -f "$PYTHON_SCRIPT" ]; then
    echo -e "${RED}❌ Script de teste de carga não encontrado: $PYTHON_SCRIPT${NC}"
    exit 1
fi

# Verificar se as dependências Python estão instaladas
echo -e "${YELLOW}📦 Verificando dependências Python...${NC}"
python3 -c "import requests, statistics" 2>/dev/null || {
    echo -e "${YELLOW}📦 Instalando dependências Python...${NC}"
    pip3 install requests
}

# Parar aplicação se estiver rodando
echo -e "${YELLOW}🛑 Parando aplicação se estiver rodando...${NC}"
pkill -f "quarkus" || true
sleep 2

# Iniciar aplicação em modo de teste de carga
echo -e "${YELLOW}🚀 Iniciando aplicação em modo de teste de carga...${NC}"
echo -e "${BLUE}   Usando H2 como banco de dados${NC}"
echo -e "${BLUE}   Porta: 8081${NC}"
echo ""

# Executar em background
mvn quarkus:dev -Dquarkus.profile=load-test > load-test-app.log 2>&1 &
APP_PID=$!

echo -e "${YELLOW}⏳ Aguardando aplicação inicializar...${NC}"

# Aguardar aplicação estar pronta
for i in {1..30}; do
    if curl -s "$API_URL/q/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Aplicação iniciada com sucesso!${NC}"
        break
    fi
    
    if [ $i -eq 30 ]; then
        echo -e "${RED}❌ Timeout: Aplicação não iniciou em 30 segundos${NC}"
        echo -e "${YELLOW}📋 Logs da aplicação:${NC}"
        tail -20 load-test-app.log
        kill $APP_PID 2>/dev/null || true
        exit 1
    fi
    
    echo -n "."
    sleep 1
done

echo ""
echo -e "${GREEN}🎯 Executando teste de carga...${NC}"
echo -e "${BLUE}   URL: $API_URL${NC}"
echo -e "${BLUE}   Duração: $DURATION minutos${NC}"
echo -e "${BLUE}   Limite: 50 requests por minuto${NC}"
echo ""

# Executar teste de carga
python3 "$PYTHON_SCRIPT" --url "$API_URL" --duration "$DURATION"

echo ""
echo -e "${YELLOW}🛑 Parando aplicação...${NC}"
kill $APP_PID 2>/dev/null || true

echo ""
echo -e "${GREEN}✅ Teste de carga concluído!${NC}"
echo -e "${BLUE}📋 Logs da aplicação salvos em: load-test-app.log${NC}"

# Limpar arquivos temporários
rm -f load-test-app.log 2>/dev/null || true 