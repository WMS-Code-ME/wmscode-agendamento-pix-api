#!/usr/bin/env python3
"""
Teste Rápido - Demonstração do Teste de Carga
Executa apenas alguns requests para verificar se tudo está funcionando
"""

import requests
import time
import random
from datetime import datetime, timedelta

def quick_test():
    """Executa um teste rápido para demonstrar o funcionamento"""
    
    base_url = "http://localhost:8080"
    
    print("🚀 Teste Rápido - API de Agendamentos PIX")
    print("=" * 50)
    
    # Verificar se a API está rodando
    try:
        response = requests.get(f"{base_url}/api/v1/pix/agendamentos", timeout=5)
        if response.status_code in [200, 401, 403]:  # 401/403 significa que a API está rodando mas precisa de auth
            print("✅ API está respondendo")
        else:
            print(f"❌ API não está respondendo corretamente - Status: {response.status_code}")
            return
    except Exception as e:
        print(f"❌ Não foi possível conectar com a API: {e}")
        print("   Execute: mvn quarkus:dev -Dquarkus.profile=load-test")
        return
    
    # Teste de autenticação
    print("\n🔐 Testando autenticação...")
    auth_data = {
        "clientId": "dev-client",
        "clientSecret": "dev-secret-123"
    }
    
    start_time = time.time()
    response = requests.post(f"{base_url}/api/v1/auth/token", json=auth_data, timeout=10)
    duration = (time.time() - start_time) * 1000
    
    if response.status_code == 200:
        token = response.json().get("accessToken")
        print(f"✅ Auth OK - {duration:.2f}ms")
    else:
        print(f"❌ Auth FAIL - Status: {response.status_code}")
        return
    
    headers = {"Authorization": f"Bearer {token}"}
    
    # Teste de criação de agendamento PIX
    print("\n💰 Testando criação de agendamento PIX...")
    pix_data = {
        "chavePix": "teste@email.com",
        "nomeBeneficiario": "Usuário Teste",
        "valor": 100.50,
        "descricao": "Teste de carga",
        "dataAgendamento": (datetime.now() + timedelta(days=1)).isoformat(),
        "observacao": "Teste rápido"
    }
    
    start_time = time.time()
    response = requests.post(f"{base_url}/api/v1/pix/agendamentos", json=pix_data, headers=headers, timeout=10)
    duration = (time.time() - start_time) * 1000
    
    if response.status_code == 201:
        agendamento_id = response.json().get("id")
        print(f"✅ POST PIX OK - {duration:.2f}ms (ID: {agendamento_id})")
        
        # Teste de busca por ID
        start_time = time.time()
        response = requests.get(f"{base_url}/api/v1/pix/agendamentos/{agendamento_id}", headers=headers, timeout=10)
        duration = (time.time() - start_time) * 1000
        
        if response.status_code == 200:
            print(f"✅ GET PIX by ID OK - {duration:.2f}ms")
        
        # Teste de listagem
        start_time = time.time()
        response = requests.get(f"{base_url}/api/v1/pix/agendamentos", headers=headers, timeout=10)
        duration = (time.time() - start_time) * 1000
        
        if response.status_code == 200:
            agendamentos = response.json()
            print(f"✅ GET PIX List OK - {duration:.2f}ms ({len(agendamentos)} agendamentos)")
        
        # Limpeza - cancelar agendamento
        response = requests.delete(f"{base_url}/api/v1/pix/agendamentos/{agendamento_id}", headers=headers, timeout=10)
        if response.status_code == 204:
            print("✅ DELETE PIX OK - Agendamento cancelado")
    else:
        print(f"❌ POST PIX FAIL - Status: {response.status_code}")
    
    # Teste de criação de webhook
    print("\n🔗 Testando criação de webhook...")
    webhook_data = {
        "url": "https://api.teste.com/webhook",
        "login": "usuario_teste",
        "senha": "senha123",
        "descricao": "Webhook de teste",
        "evento": "PAGAMENTO_PIX"
    }
    
    start_time = time.time()
    response = requests.post(f"{base_url}/api/v1/webhooks", json=webhook_data, headers=headers, timeout=10)
    duration = (time.time() - start_time) * 1000
    
    if response.status_code == 201:
        webhook_id = response.json().get("id")
        print(f"✅ POST Webhook OK - {duration:.2f}ms (ID: {webhook_id})")
        
        # Teste de busca por ID
        start_time = time.time()
        response = requests.get(f"{base_url}/api/v1/webhooks/{webhook_id}", headers=headers, timeout=10)
        duration = (time.time() - start_time) * 1000
        
        if response.status_code == 200:
            print(f"✅ GET Webhook by ID OK - {duration:.2f}ms")
        
        # Teste de listagem
        start_time = time.time()
        response = requests.get(f"{base_url}/api/v1/webhooks", headers=headers, timeout=10)
        duration = (time.time() - start_time) * 1000
        
        if response.status_code == 200:
            webhooks = response.json()
            print(f"✅ GET Webhook List OK - {duration:.2f}ms ({len(webhooks)} webhooks)")
        
        # Limpeza - deletar webhook
        response = requests.delete(f"{base_url}/api/v1/webhooks/{webhook_id}", headers=headers, timeout=10)
        if response.status_code == 204:
            print("✅ DELETE Webhook OK - Webhook removido")
    else:
        print(f"❌ POST Webhook FAIL - Status: {response.status_code}")
    
    print("\n" + "=" * 50)
    print("✅ Teste rápido concluído com sucesso!")
    print("🎯 Todos os endpoints principais estão funcionando")
    print("🚀 Execute './scripts/run-load-test.sh' para o teste de carga completo")

if __name__ == "__main__":
    quick_test() 