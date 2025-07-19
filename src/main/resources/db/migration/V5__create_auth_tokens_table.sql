CREATE TABLE auth_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(1000) NOT NULL UNIQUE,
    client_id VARCHAR(255) NOT NULL,
    client_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT uk_auth_tokens_token UNIQUE (token)
);

-- Índices para melhorar performance
CREATE INDEX idx_auth_tokens_client_id ON auth_tokens(client_id);
CREATE INDEX idx_auth_tokens_status ON auth_tokens(status);
CREATE INDEX idx_auth_tokens_expires_at ON auth_tokens(expires_at);
CREATE INDEX idx_auth_tokens_token_status ON auth_tokens(token, status); 