package br.com.wmscode.process.service;

import br.com.wmscode.system.entity.AuthToken;
import br.com.wmscode.system.repository.AuthTokenRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AuthService {
    
    private static final Logger LOG = Logger.getLogger(AuthService.class);
    
    private final AuthTokenRepository tokenRepository;

    @Inject
    public AuthService(AuthTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    
    @ConfigProperty(name = "auth.default.client.id")
    Optional<String> defaultClientId;
    
    @ConfigProperty(name = "auth.default.client.secret")
    Optional<String> defaultClientSecret;
    
    @ConfigProperty(name = "auth.token.expiration.hours", defaultValue = "24")
    Long tokenExpirationHours;
    
    @Transactional
    public String authenticateAndGenerateToken(String clientId, String clientSecret) {
        LOG.info("Tentativa de autenticação para client_id: " + clientId);       
        if (!isValidCredentials(clientId, clientSecret)) {
            LOG.warn("Credenciais inválidas para client_id: " + clientId);
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        String token = generateJwtToken(clientId);
        AuthToken authToken = new AuthToken();
        authToken.setToken(token);
        authToken.setClientId(clientId);
        authToken.setClientName("Cliente Padrão");
        authToken.setCreatedAt(LocalDateTime.now());
        authToken.setExpiresAt(LocalDateTime.now().plusHours(tokenExpirationHours));
        authToken.setStatus(AuthToken.StatusToken.ATIVO);
        tokenRepository.persist(authToken);        
        LOG.info("Token JWT gerado e salvo com sucesso para: " + clientId);
        return token;
    }

    public boolean isValidToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            Optional<AuthToken> authTokenOpt = tokenRepository.findByActiveToken(token);
            
            if (authTokenOpt.isEmpty()) {
                LOG.warn("Token não encontrado no banco de dados: " + token);
                return false;
            }
            
            AuthToken authToken = authTokenOpt.get();
            
            if (authToken.isExpired()) {
                LOG.warn("Token expirado: " + token);
                // Marcar como inativo
                inactivateToken(authToken);
                return false;
            }
            
            LOG.debug("Token válido para client_id: " + authToken.getClientId());
            return true;
            
        } catch (Exception e) {
            LOG.error("Erro ao validar token: " + e.getMessage(), e);
            return false;
        }
    }
    
    @Transactional
    public void inactivateToken(AuthToken authToken) {
        authToken.setStatus(AuthToken.StatusToken.INATIVO);
        tokenRepository.persist(authToken);
        LOG.info("Token inativado: " + authToken.getToken());
    }
    

    private String generateJwtToken(String clientId) {
        // Gerar um token único usando UUID
        String tokenId = UUID.randomUUID().toString();
        
        // Criar um token JWT simples (em produção, use uma biblioteca JWT adequada)
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = String.format(
            "{\"sub\":\"%s\",\"iss\":\"wmscode-pix-api\",\"iat\":%d,\"exp\":%d,\"jti\":\"%s\"}",
            clientId,
            System.currentTimeMillis() / 1000,
            (System.currentTimeMillis() / 1000) + (tokenExpirationHours * 3600),
            tokenId
        );
        
        // Codificar header e payload em base64
        String encodedHeader = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        String encodedPayload = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        
        // Gerar assinatura usando Argon2 (simulado)
        String signature = generateArgon2Signature(encodedHeader + "." + encodedPayload);
        
        return encodedHeader + "." + encodedPayload + "." + signature;
    }
    
    /**
     * Gera assinatura usando algoritmo Argon2
     */
    private String generateArgon2Signature(String data) {
        // Em uma implementação real, você usaria uma biblioteca Argon2
        // Por simplicidade, vamos gerar um hash baseado na data e secret
        String secret = defaultClientSecret.orElse("default-secret");
        String input = data + secret;
        
        // Gerar um hash simples (em produção, use Argon2 real)
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            LOG.error("Erro ao gerar assinatura: " + e.getMessage(), e);
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes());
        }
    }
    
    /**
     * Verifica se as credenciais são válidas
     */
    private boolean isValidCredentials(String clientId, String clientSecret) {
        return defaultClientId.isPresent() && 
               defaultClientSecret.isPresent() &&
               defaultClientId.get().equals(clientId) && 
               defaultClientSecret.get().equals(clientSecret);
    }
    
    /**
     * Tarefa agendada para inativar tokens expirados
     */
    @Scheduled(every = "1h")
    @Transactional
    void inactivateExpiredTokens() {
        LOG.info("Executando limpeza de tokens expirados");
        tokenRepository.inactivateExpiredTokens();
        LOG.info("Limpeza de tokens expirados concluída");
    }
} 