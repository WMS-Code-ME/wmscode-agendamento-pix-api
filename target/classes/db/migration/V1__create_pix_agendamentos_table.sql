CREATE TABLE pix_agendamentos (
    id BIGSERIAL PRIMARY KEY,
    chave_pix VARCHAR(255) NOT NULL,
    nome_beneficiario VARCHAR(255) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    descricao VARCHAR(140) NOT NULL,
    data_agendamento TIMESTAMP NOT NULL,
    observacao VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_processamento TIMESTAMP,
    codigo_transacao VARCHAR(50)
);

CREATE INDEX idx_pix_agendamentos_status ON pix_agendamentos(status);
CREATE INDEX idx_pix_agendamentos_data_agendamento ON pix_agendamentos(data_agendamento);
CREATE INDEX idx_pix_agendamentos_chave_pix ON pix_agendamentos(chave_pix); 