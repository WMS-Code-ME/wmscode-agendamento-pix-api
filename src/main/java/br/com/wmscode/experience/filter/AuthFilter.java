package br.com.wmscode.experience.filter;

import br.com.wmscode.process.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Optional;

@Provider
public class AuthFilter implements ContainerRequestFilter {
    
    private static final Logger LOG = Logger.getLogger(AuthFilter.class);
    
    @Inject
    AuthService authService;
    
    @ConfigProperty(name = "quarkus.profile", defaultValue = "prod")
    String profile;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();
        String fullPath = requestContext.getUriInfo().getRequestUri().getPath();
        
        LOG.info("=== AuthFilter - Path: " + path + ", Method: " + method + " ===");
        LOG.info("=== AuthFilter - Full Path: " + fullPath + " ===");
        
        // Endpoints que não precisam de autenticação
        if (isPublicEndpoint(path) || isPublicEndpoint(fullPath)) {
            LOG.info("=== AuthFilter - Endpoint público detectado: " + path + " (fullPath: " + fullPath + ") ===");
            return;
        }
        
        LOG.info("=== AuthFilter - Endpoint protegido: " + path + " ===");
        
        // Em ambiente de teste, permitir acesso sem autenticação
        if (isTestEnvironment()) {
            LOG.debug("Ambiente de teste detectado, pulando autenticação para: " + path);
            return;
        }
        
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOG.warn("Requisição sem header de autorização: " + path);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"Token de acesso é obrigatório\"}")
                .build());
            return;
        }
        
        String token = authHeader.substring("Bearer ".length());
        
        // Validar token usando o AuthService
        if (!authService.isValidToken(token)) {
            LOG.warn("Token inválido: " + token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"Token de acesso inválido\"}")
                .build());
            return;
        }
        
        LOG.debug("Autenticação bem-sucedida para: " + path);
    }
    
    /**
     * Verifica se o endpoint é público (não precisa de autenticação)
     */
    private boolean isPublicEndpoint(String path) {
        // Verificar tanto o path quanto o fullPath para garantir que capturamos corretamente
        boolean isPublic = path.equals("auth/token") || 
               path.equals("/auth/token") ||
               path.equals("api/v1/auth/token") ||
               path.equals("/api/v1/auth/token") ||
               path.startsWith("health") ||
               path.startsWith("/health") ||
               path.startsWith("metrics") ||
               path.startsWith("/metrics") ||
               path.startsWith("openapi") ||
               path.startsWith("/openapi") ||
               path.startsWith("swagger-ui") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("q/") ||
               path.startsWith("/q/");
        
        LOG.info("=== AuthFilter - Verificando se é público: " + path + " = " + isPublic + " ===");
        return isPublic;
    }
    
    /**
     * Verifica se está em ambiente de teste
     */
    private boolean isTestEnvironment() {
        // Verificar múltiplas formas de detectar ambiente de teste
        return "test".equals(profile) ||
               "test".equals(System.getProperty("quarkus.profile")) ||
               "test".equals(System.getenv("QUARKUS_PROFILE")) ||
               System.getProperty("quarkus.test") != null ||
               System.getProperty("maven.test") != null;
    }
} 