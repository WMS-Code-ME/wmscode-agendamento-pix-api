package br.com.wmscode.process.service;

import br.com.wmscode.system.entity.AuthToken;
import br.com.wmscode.system.repository.AuthTokenRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AuthServiceTest {
    
    @Inject
    AuthService authService;
    
    @Inject
    AuthTokenRepository authTokenRepository;
    
    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar dados antes de cada teste
        authTokenRepository.deleteAll();
    }
    
    @Test
    @Transactional
    public void testAuthenticateWithDefaultClient() {
        // Teste com cliente padrão configurado
        String clientId = "dev-client";
        String clientSecret = "dev-secret-123";
        
        String token = authService.authenticateAndGenerateToken(clientId, clientSecret);
        
        assertNotNull(token);
        assertTrue(token.contains(".")); // Verifica se é um JWT válido (tem pontos)
        
        // Verificar se o token foi salvo no banco
        assertTrue(authService.isValidToken(token));
    }
    
    @Test
    @Transactional
    public void testAuthenticateWithInvalidCredentials() {
        String clientId = "invalid-client";
        String clientSecret = "invalid-secret";
        
        assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticateAndGenerateToken(clientId, clientSecret);
        });
    }
    
    @Test
    @Transactional
    public void testTokenValidation() {
        // Gerar token válido
        String clientId = "dev-client";
        String clientSecret = "dev-secret-123";
        String token = authService.authenticateAndGenerateToken(clientId, clientSecret);
        
        // Verificar se o token é válido
        assertTrue(authService.isValidToken(token));
        
        // Verificar se token inválido retorna false
        assertFalse(authService.isValidToken("invalid-token"));
        assertFalse(authService.isValidToken(null));
        assertFalse(authService.isValidToken(""));
    }
    
    @Test
    @Transactional
    public void testTokenExpiration() {
        // Gerar token
        String clientId = "dev-client";
        String clientSecret = "dev-secret-123";
        String token = authService.authenticateAndGenerateToken(clientId, clientSecret);
        
        // Verificar se o token foi salvo no banco
        assertTrue(authService.isValidToken(token));
        
        // Buscar o token no banco e verificar se tem data de expiração
        var tokenOpt = authTokenRepository.findByActiveToken(token);
        assertTrue(tokenOpt.isPresent());
        
        AuthToken authToken = tokenOpt.get();
        assertNotNull(authToken.getExpiresAt());
        assertNotNull(authToken.getCreatedAt());
        assertEquals(clientId, authToken.getClientId());
        assertEquals("Cliente Padrão", authToken.getClientName());
        assertEquals(AuthToken.StatusToken.ATIVO, authToken.getStatus());
    }
    
    @Test
    @Transactional
    public void testTokenInactivation() {
        // Gerar token
        String clientId = "dev-client";
        String clientSecret = "dev-secret-123";
        String token = authService.authenticateAndGenerateToken(clientId, clientSecret);
        
        // Verificar se o token é válido
        assertTrue(authService.isValidToken(token));
        
        // Buscar o token no banco
        var tokenOpt = authTokenRepository.findByActiveToken(token);
        assertTrue(tokenOpt.isPresent());
        
        // Inativar o token
        authService.inactivateToken(tokenOpt.get());
        
        // Verificar se o token não é mais válido
        assertFalse(authService.isValidToken(token));
    }
} 