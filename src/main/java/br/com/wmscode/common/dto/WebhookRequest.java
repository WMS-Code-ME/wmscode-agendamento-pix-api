package br.com.wmscode.common.dto;

import br.com.wmscode.system.entity.Webhook;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WebhookRequest {
    
    @NotBlank(message = "URL é obrigatória")
    private String url;
    
    @NotBlank(message = "Login é obrigatório")
    private String login;
    
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
    
    private String descricao;
    
    private String body;
    
    @NotNull(message = "Evento é obrigatório")
    private Webhook.TipoEvento evento;
} 