-- Adicionar campos body e evento à tabela webhooks
ALTER TABLE webhooks ADD COLUMN body TEXT;
ALTER TABLE webhooks ADD COLUMN evento VARCHAR(50) NOT NULL DEFAULT 'PAGAMENTO_PIX';

-- Criar índice para busca por evento
CREATE INDEX idx_webhooks_evento ON webhooks(evento);

-- Criar índice composto para busca por status e evento
CREATE INDEX idx_webhooks_status_evento ON webhooks(status, evento); 