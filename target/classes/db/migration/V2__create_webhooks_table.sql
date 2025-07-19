CREATE TABLE webhooks (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    login VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    descricao VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP
);

-- Índices para melhor performance
CREATE INDEX idx_webhooks_status ON webhooks(status);
CREATE INDEX idx_webhooks_data_criacao ON webhooks(data_criacao);

-- Comentários para documentação
COMMENT ON TABLE webhooks IS 'Tabela para armazenar webhooks cadastrados';
COMMENT ON COLUMN webhooks.url IS 'URL do webhook para receber notificações';
COMMENT ON COLUMN webhooks.login IS 'Login para autenticação no webhook';
COMMENT ON COLUMN webhooks.senha IS 'Senha para autenticação no webhook';
COMMENT ON COLUMN webhooks.descricao IS 'Descrição opcional do webhook';
COMMENT ON COLUMN webhooks.status IS 'Status do webhook: ATIVO ou INATIVO';
COMMENT ON COLUMN webhooks.data_criacao IS 'Data de criação do webhook';
COMMENT ON COLUMN webhooks.data_atualizacao IS 'Data da última atualização do webhook'; 