package br.com.wmscode.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuthResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private LocalDateTime expiresAt;
    private String clientId;
    private String clientName;
} 