package br.com.wmscode.common.dto;

import br.com.wmscode.system.entity.Webhook;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WebhookResponse {
    private Long id;
    private String url;
    private String login;
    private String descricao;
    private String body;
    private Webhook.TipoEvento evento;
    private Webhook.StatusWebhook status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
} 