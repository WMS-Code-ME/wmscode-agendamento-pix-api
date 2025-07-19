package br.com.wmscode.experience.controller;

import br.com.wmscode.common.dto.AuthRequest;
import br.com.wmscode.common.dto.AuthResponse;
import br.com.wmscode.process.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {
    
    private static final Logger LOG = Logger.getLogger(AuthController.class);
    
    @Inject
    AuthService authService;
    
    /**
     * Endpoint para autenticação e geração de token JWT
     */
    @POST
    @Path("/token")
    public Response authenticate(@Valid AuthRequest request) {
        LOG.info("Recebendo requisição de autenticação para client_id: " + request.getClientId());
        
        try {
            String token = authService.authenticateAndGenerateToken(request.getClientId(), request.getClientSecret());
            
            AuthResponse response = new AuthResponse();
            response.setAccessToken(token);
            response.setTokenType("Bearer");
            response.setExpiresIn(24L * 3600L); // 24 horas em segundos
            response.setExpiresAt(LocalDateTime.now().plusHours(24));
            response.setClientId(request.getClientId());
            response.setClientName("Cliente Padrão");
            
            LOG.info("Token JWT gerado com sucesso para: " + request.getClientId());
            return Response.ok(response).build();
            
        } catch (IllegalArgumentException e) {
            LOG.warn("Credenciais inválidas: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"Credenciais inválidas\"}")
                .build();
        } catch (Exception e) {
            LOG.error("Erro na autenticação: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Erro interno do servidor\"}")
                .build();
        }
    }
} 