package br.com.wmscode.system.repository;

import br.com.wmscode.system.entity.AuthToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AuthTokenRepository implements PanacheRepository<AuthToken> {
    
    public Optional<AuthToken> findByTokenAndStatus(String token, AuthToken.StatusToken status) {
        return find("token = ?1 and status = ?2", token, status).firstResultOptional();
    }
    
    public Optional<AuthToken> findByActiveToken(String token) {
        return find("token = ?1 and status = ?2", token, AuthToken.StatusToken.ATIVO).firstResultOptional();
    }
    
    public void inactivateExpiredTokens() {
        update("status = ?1 where expiresAt < ?2 and status = ?3", 
               AuthToken.StatusToken.INATIVO, 
               java.time.LocalDateTime.now(), 
               AuthToken.StatusToken.ATIVO);
    }
} 