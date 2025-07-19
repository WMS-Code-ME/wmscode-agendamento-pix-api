package br.com.wmscode.system.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_tokens")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthToken extends PanacheEntity {
    
    @Column(name = "token", nullable = false, unique = true, length = 1000)
    private String token;
    
    @Column(name = "client_id", nullable = false)
    private String clientId;
    
    @Column(name = "client_name", nullable = false)
    private String clientName;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusToken status = StatusToken.ATIVO;
    
    public enum StatusToken {
        ATIVO, INATIVO
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isActive() {
        return status == StatusToken.ATIVO && !isExpired();
    }
} 