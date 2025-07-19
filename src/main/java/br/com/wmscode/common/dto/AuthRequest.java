package br.com.wmscode.common.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AuthRequest {
    
    @NotBlank(message = "Client ID é obrigatório")
    private String clientId;
    
    @NotBlank(message = "Client Secret é obrigatório")
    private String clientSecret;
} 