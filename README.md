# 💸 API de Agendamentos de PIX

Projeto desenvolvido com **Java 21** e **Quarkus Framework** que oferece uma API robusta e segura para agendamento de transferências via **PIX**, com autenticação, agendamento inteligente, execução automática, integração com webhooks e comunicação com sistemas financeiros externos.

---

## 📌 Visão Geral

A API de Agendamentos de PIX tem como objetivo permitir que usuários agendem pagamentos via PIX para datas futuras. A solução é voltada a instituições financeiras e sistemas bancários que necessitam de um controle automatizado, auditável e escalável para este tipo de operação.

A aplicação foi estruturada em uma arquitetura de microsserviço com camadas bem definidas.

---

## 🧩 Principais Componentes

A aplicação é composta por múltiplos componentes, organizados em camadas de **controle**, **serviço**, **persistência** e **integração externa**:

### 🎯 Controllers
- **Auth Controller**: Recebe requisições de autenticação para uso da API.
- **Pix Scheduler Controller**: Interface HTTP para criação, consulta e cancelamento de agendamentos.
- **Webhook Controller**: Gerencia endpoints de webhooks configurados por clientes.

### 🔧 Services
- **Auth Service**: Processa requisições de autenticação.
- **Pix Scheduler Service**: Processa regras de negócio relacionadas aos agendamentos.
- **Webhook Service**: Processa o cadastro, atualização e envio de notificações via webhooks.
- **Batch Service**: Executa rotinas agendadas para verificar e processar pagamentos com data/hora vencida.

### 🧱 Repositórios
- **Auth Repository**: Responsável por armazenar tokens e acessos (se necessário).
- **Pix Scheduler Repository**: Persistência dos agendamentos.
- **Webhooks Repository**: Persistência dos webhooks cadastrados por clientes.

### 🌐 Integrações
- **Pagamento Client**: Responsável por enviar requisições HTTP para a instituição financeira, efetuando o pagamento agendado.

---

## 🧠 Fluxo de Execução

1. O usuário realiza a **autenticação** via `AuthController`.
2. O cliente agenda um pagamento via `PixSchedulerController`.
3. A requisição é processada e salva no banco de dados PostgreSQL.
4. Um **agendador interno** (`BatchService`) verifica periodicamente os agendamentos pendentes.
5. Quando a data/hora de um agendamento expira, o serviço chama o **PagamentoClient** para executar o PIX.
6. Caso webhooks estejam configurados, eles são **notificados** via `WebhookService`.

---

## 💻 Tecnologias Utilizadas

| Tecnologia           | Finalidade                            |
|----------------------|----------------------------------------|
| **Java 21**          | Linguagem principal                    |
| **Quarkus**          | Framework leve e performático          |
| **PostgreSQL**       | Armazenamento dos agendamentos         |
| **Docker**           | Empacotamento da aplicação             |
| **Kafka (futuro)**   | Mensageria e eventos                   |
| **Auth 2.0**         | Autenticação e autorização             |
| **Swagger/OpenAPI**  | Documentação automática da API         |
| **JUnit/Testcontainers** | Testes automatizados               |

---

## 🧪 Funcionalidades da API

- ✅ Autenticação de usuários
- ✅ Agendamento de pagamentos PIX
- ✅ Consulta e cancelamento de agendamentos
- ✅ Cadastro de webhooks para notificações
- ✅ Execução automática de pagamentos
- ✅ Envio de notificações via webhook
- ✅ Logs e rastreabilidade

---

## 🧱 Arquitetura (Modelo de Componentes)

![Context Diagram](docs/arquitetura-componentes.png)
![Container Diagram](docs/arquitetura-componentes.png)
![Component Diagram](docs/arquitetura-componentes.png)

> O diagrama acima representa os principais componentes da API e suas interações com banco de dados e sistemas externos (como a instituição financeira).

---

## 📂 Estrutura de Pastas (proposta)

