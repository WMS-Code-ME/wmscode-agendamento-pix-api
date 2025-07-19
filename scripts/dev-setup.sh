#!/bin/bash

echo "🚀 Configurando ambiente de desenvolvimento para API de Agendamentos PIX"

# Verificar se o Docker está instalado
if ! command docker compose version &> /dev/null; then
    echo "❌ Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

# Verificar se o Docker Compose está instalado
if ! command -v docker compose &> /dev/null; then
    echo "❌ Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
    exit 1
fi

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não está instalado. Por favor, instale o Java 21 primeiro."
    exit 1
fi

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não está instalado. Por favor, instale o Maven primeiro."
    exit 1
fi

echo "✅ Dependências verificadas"

# Iniciar PostgreSQL e pgAdmin
echo "🐘 Iniciando PostgreSQL e pgAdmin..."
docker compose up -d

# Aguardar o PostgreSQL estar pronto
echo "⏳ Aguardando PostgreSQL estar pronto..."
sleep 10

# Verificar se o PostgreSQL está rodando
if docker compose ps | grep -q "Up"; then
    echo "✅ PostgreSQL e pgAdmin iniciados com sucesso"
    echo "📊 pgAdmin disponível em: http://localhost:8081"
    echo "   Email: admin@pixscheduler.com"
    echo "   Senha: admin123"
else
    echo "❌ Erro ao iniciar PostgreSQL"
    exit 1
fi

echo ""
echo "🎯 Próximos passos:"
echo "1. Execute: ./mvnw quarkus:dev"
echo "2. Acesse a API em: http://localhost:8080"
echo "3. Acesse o Swagger em: http://localhost:8080/q/swagger-ui"
echo "4. Execute os testes: ./mvnw test"
echo ""
echo "🔧 Para parar os serviços: docker compose down" 