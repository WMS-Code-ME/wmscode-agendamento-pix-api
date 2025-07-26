#!/usr/bin/env python3
"""
Script de Teste de Carga para API de Agendamentos PIX
Limite: 50 requests por minuto
"""

import requests
import time
import json
import random
import threading
from datetime import datetime, timedelta
from collections import defaultdict
import statistics

class LoadTest:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.results = defaultdict(list)
        self.request_count = 0
        self.start_time = None
        self.lock = threading.Lock()
        
        # Limite de 50 requests por minuto
        self.max_requests_per_minute = 50
        self.request_times = []
        
    def rate_limit(self):
        """Controle de taxa: máximo 50 requests por minuto"""
        current_time = time.time()
        
        with self.lock:
            # Remove timestamps mais antigos que 1 minuto
            self.request_times = [t for t in self.request_times if current_time - t < 60]
            
            if len(self.request_times) >= self.max_requests_per_minute:
                # Aguarda até que possamos fazer mais uma requisição
                sleep_time = 60 - (current_time - self.request_times[0])
                if sleep_time > 0:
                    print(f"Rate limit atingido. Aguardando {sleep_time:.2f} segundos...")
                    time.sleep(sleep_time)
            
            self.request_times.append(current_time)
    
    def make_request(self, method, endpoint, data=None, headers=None):
        """Faz uma requisição HTTP e registra os resultados"""
        self.rate_limit()
        
        url = f"{self.base_url}{endpoint}"
        start_time = time.time()
        
        try:
            if method.upper() == "GET":
                response = requests.get(url, headers=headers, timeout=30)
            elif method.upper() == "POST":
                response = requests.post(url, json=data, headers=headers, timeout=30)
            elif method.upper() == "PUT":
                response = requests.put(url, json=data, headers=headers, timeout=30)
            elif method.upper() == "DELETE":
                response = requests.delete(url, headers=headers, timeout=30)
            else:
                raise ValueError(f"Método não suportado: {method}")
            
            end_time = time.time()
            duration = (end_time - start_time) * 1000  # em milissegundos
            
            with self.lock:
                self.request_count += 1
                self.results[response.status_code].append(duration)
            
            return response, duration
            
        except Exception as e:
            end_time = time.time()
            duration = (end_time - start_time) * 1000
            
            with self.lock:
                self.request_count += 1
                self.results["ERROR"].append(duration)
            
            print(f"Erro na requisição {method} {endpoint}: {e}")
            return None, duration
    
    def generate_pix_request(self):
        """Gera dados aleatórios para agendamento PIX"""
        chaves_pix = [
            "joao.silva@email.com",
            "maria.santos@email.com", 
            "pedro.oliveira@email.com",
            "ana.costa@email.com",
            "carlos.rodrigues@email.com"
        ]
        
        nomes = [
            "João Silva",
            "Maria Santos",
            "Pedro Oliveira", 
            "Ana Costa",
            "Carlos Rodrigues"
        ]
        
        return {
            "chavePix": random.choice(chaves_pix),
            "nomeBeneficiario": random.choice(nomes),
            "valor": round(random.uniform(10.0, 500.0), 2),
            "descricao": f"Pagamento teste {random.randint(1, 1000)}",
            "dataAgendamento": (datetime.now() + timedelta(days=random.randint(1, 30))).isoformat(),
            "observacao": f"Observação teste {random.randint(1, 100)}"
        }
    
    def generate_webhook_request(self):
        """Gera dados aleatórios para webhook"""
        return {
            "url": f"https://api.teste{random.randint(1, 100)}.com/webhook",
            "login": f"usuario{random.randint(1, 100)}",
            "senha": f"senha{random.randint(1000, 9999)}",
            "descricao": f"Webhook teste {random.randint(1, 100)}",
            "evento": "PAGAMENTO_PIX"
        }
    
    def test_auth_endpoint(self):
        """Testa endpoint de autenticação"""
        print("Testando endpoint de autenticação...")
        
        auth_data = {
            "clientId": "dev-client",
            "clientSecret": "dev-secret-123"
        }
        
        response, duration = self.make_request("POST", "/api/v1/auth/token", auth_data)
        
        if response and response.status_code == 200:
            print(f"✅ Auth OK - {duration:.2f}ms")
            return response.json().get("accessToken")
        else:
            print(f"❌ Auth FAIL - Status: {response.status_code if response else 'ERROR'}")
            return None
    
    def test_pix_endpoints(self, token=None):
        """Testa endpoints de agendamento PIX"""
        print("Testando endpoints de agendamento PIX...")
        
        headers = {"Authorization": f"Bearer {token}"} if token else {}
        
        # POST - Criar agendamento
        pix_data = self.generate_pix_request()
        response, duration = self.make_request("POST", "/api/v1/pix/agendamentos", pix_data, headers)
        
        if response and response.status_code == 201:
            print(f"✅ POST PIX OK - {duration:.2f}ms")
            agendamento_id = response.json().get("id")
            
            # GET - Buscar por ID
            response, duration = self.make_request("GET", f"/api/v1/pix/agendamentos/{agendamento_id}", headers=headers)
            if response and response.status_code == 200:
                print(f"✅ GET PIX by ID OK - {duration:.2f}ms")
            
            # DELETE - Cancelar agendamento
            response, duration = self.make_request("DELETE", f"/api/v1/pix/agendamentos/{agendamento_id}", headers=headers)
            if response and response.status_code == 204:
                print(f"✅ DELETE PIX OK - {duration:.2f}ms")
        else:
            print(f"❌ POST PIX FAIL - Status: {response.status_code if response else 'ERROR'}")
        
        # GET - Listar todos
        response, duration = self.make_request("GET", "/api/v1/pix/agendamentos", headers=headers)
        if response and response.status_code == 200:
            print(f"✅ GET PIX List OK - {duration:.2f}ms")
        else:
            print(f"❌ GET PIX List FAIL - Status: {response.status_code if response else 'ERROR'}")
    
    def test_webhook_endpoints(self, token=None):
        """Testa endpoints de webhook"""
        print("Testando endpoints de webhook...")
        
        headers = {"Authorization": f"Bearer {token}"} if token else {}
        
        # Primeiro, listar webhooks existentes e deletar se necessário
        response, duration = self.make_request("GET", "/api/v1/webhooks", headers=headers)
        if response and response.status_code == 200:
            webhooks = response.json()
            if webhooks:
                # Deletar webhooks existentes para evitar conflito
                for webhook in webhooks:
                    webhook_id = webhook.get("id")
                    if webhook_id:
                        delete_response, _ = self.make_request("DELETE", f"/api/v1/webhooks/{webhook_id}", headers=headers)
                        if delete_response and delete_response.status_code == 204:
                            print(f"🗑️  Webhook {webhook_id} deletado para evitar conflito")
        
        # POST - Criar webhook
        webhook_data = self.generate_webhook_request()
        response, duration = self.make_request("POST", "/api/v1/webhooks", webhook_data, headers)
        
        if response and response.status_code == 201:
            print(f"✅ POST Webhook OK - {duration:.2f}ms")
            webhook_id = response.json().get("id")
            
            # GET - Buscar por ID
            response, duration = self.make_request("GET", f"/api/v1/webhooks/{webhook_id}", headers=headers)
            if response and response.status_code == 200:
                print(f"✅ GET Webhook by ID OK - {duration:.2f}ms")
            
            # PUT - Inativar webhook
            response, duration = self.make_request("PUT", f"/api/v1/webhooks/{webhook_id}/inativar", headers=headers)
            if response and response.status_code == 200:
                print(f"✅ PUT Webhook Inativar OK - {duration:.2f}ms")
            
            # PUT - Ativar webhook
            response, duration = self.make_request("PUT", f"/api/v1/webhooks/{webhook_id}/ativar", headers=headers)
            if response and response.status_code == 200:
                print(f"✅ PUT Webhook Ativar OK - {duration:.2f}ms")
            
            # DELETE - Deletar webhook no final
            response, duration = self.make_request("DELETE", f"/api/v1/webhooks/{webhook_id}", headers=headers)
            if response and response.status_code == 204:
                print(f"✅ DELETE Webhook OK - {duration:.2f}ms")
        elif response and response.status_code == 409:
            print(f"⚠️  POST Webhook CONFLICT - Já existe webhook ativo para este evento - {duration:.2f}ms")
        else:
            print(f"❌ POST Webhook FAIL - Status: {response.status_code if response else 'ERROR'}")
        
        # GET - Listar todos
        response, duration = self.make_request("GET", "/api/v1/webhooks", headers=headers)
        if response and response.status_code == 200:
            print(f"✅ GET Webhook List OK - {duration:.2f}ms")
        else:
            print(f"❌ GET Webhook List FAIL - Status: {response.status_code if response else 'ERROR'}")
    
    def run_load_test(self, duration_minutes=5):
        """Executa o teste de carga por um período determinado"""
        print(f"🚀 Iniciando teste de carga por {duration_minutes} minutos...")
        print(f"📊 Limite: {self.max_requests_per_minute} requests por minuto")
        print(f"🌐 Base URL: {self.base_url}")
        print("-" * 60)
        
        self.start_time = time.time()
        end_time = self.start_time + (duration_minutes * 60)
        
        # Primeiro, obtém um token de autenticação
        token = self.test_auth_endpoint()
        
        while time.time() < end_time:
            # Testa endpoints de PIX
            self.test_pix_endpoints(token)
            
            # Testa endpoints de webhook
            self.test_webhook_endpoints(token)
            
            # Pequena pausa entre ciclos
            time.sleep(2)
        
        self.print_results()
    
    def print_results(self):
        """Imprime os resultados do teste de carga"""
        print("\n" + "=" * 60)
        print("📈 RESULTADOS DO TESTE DE CARGA")
        print("=" * 60)
        
        total_time = time.time() - self.start_time
        total_requests = self.request_count
        
        print(f"⏱️  Tempo total: {total_time:.2f} segundos")
        print(f"📊 Total de requests: {total_requests}")
        print(f"🚀 Requests por segundo: {total_requests / total_time:.2f}")
        print(f"⏳ Requests por minuto: {(total_requests / total_time) * 60:.2f}")
        
        print("\n📋 Distribuição por Status Code:")
        for status_code, durations in self.results.items():
            count = len(durations)
            percentage = (count / total_requests) * 100
            avg_duration = statistics.mean(durations) if durations else 0
            min_duration = min(durations) if durations else 0
            max_duration = max(durations) if durations else 0
            
            print(f"  {status_code}: {count} requests ({percentage:.1f}%)")
            print(f"    Média: {avg_duration:.2f}ms | Min: {min_duration:.2f}ms | Max: {max_duration:.2f}ms")
        
        # Estatísticas gerais de tempo de resposta
        all_durations = []
        for durations in self.results.values():
            all_durations.extend(durations)
        
        if all_durations:
            print(f"\n📊 Estatísticas Gerais:")
            print(f"  Tempo médio de resposta: {statistics.mean(all_durations):.2f}ms")
            print(f"  Tempo mediano de resposta: {statistics.median(all_durations):.2f}ms")
            print(f"  Tempo mínimo: {min(all_durations):.2f}ms")
            print(f"  Tempo máximo: {max(all_durations):.2f}ms")
            print(f"  Desvio padrão: {statistics.stdev(all_durations):.2f}ms")
        
        print("\n✅ Teste de carga concluído!")

def main():
    """Função principal"""
    import argparse
    
    parser = argparse.ArgumentParser(description="Teste de Carga para API de Agendamentos PIX")
    parser.add_argument("--url", default="http://localhost:8080", help="URL base da API")
    parser.add_argument("--duration", type=int, default=5, help="Duração do teste em minutos")
    
    args = parser.parse_args()
    
    # Verifica se a API está rodando
    try:
        response = requests.get(f"{args.url}/api/v1/pix/agendamentos", timeout=5)
        if response.status_code in [200, 401, 403]:  # 401/403 significa que a API está rodando mas precisa de auth
            print("✅ API está respondendo")
        else:
            print(f"❌ API não está respondendo corretamente - Status: {response.status_code}")
            return
    except Exception as e:
        print(f"❌ Não foi possível conectar com a API: {e}")
        print(f"   Verifique se a API está rodando em: {args.url}")
        return
    
    # Executa o teste de carga
    load_test = LoadTest(args.url)
    load_test.run_load_test(args.duration)

if __name__ == "__main__":
    main() 