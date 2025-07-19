package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.AuthClientRequest;
import br.com.wmscode.common.dto.AuthClientResponse;
import br.com.wmscode.common.dto.AuthRequest;
import br.com.wmscode.common.dto.AuthResponse;
import br.com.wmscode.system.entity.AuthClient;
import br.com.wmscode.system.repository.AuthRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class AuthServiceTest {
    
    @Inject
    AuthService authService;
    
    @Inject
    AuthRepository authRepository;
    
    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar dados antes de cada teste
        authRepository.deleteAll();
    }
    
    @Test
    @Transactional
    public void testAuthenticateWithDefaultClient() {
        // Teste com cliente padrão configurado
        AuthRequest request = new AuthRequest();
        request.setClientId("dev-client");
        request.setClientSecret("dev-secret-123");
        
        AuthResponse response = authService.authenticate(request);
        
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("dev-client", response.getClientId());
        assertEquals("Cliente Padrão", response.getClientName());
        assertNotNull(response.getExpiresAt());
        assertTrue(response.getExpiresIn() > 0);
    }
    
    @Test
    @Transactional
    public void testAuthenticateWithDatabaseClient() {
        // Criar cliente no banco
        AuthClientRequest clientRequest = new AuthClientRequest();
        clientRequest.setClientId("test-client");
        clientRequest.setClientSecret("test-secret");
        clientRequest.setNome("Cliente de Teste");
        clientRequest.setDescricao("Cliente para testes");
        
        authService.createClient(clientRequest);
        
        // Testar autenticação
        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId("test-client");
        authRequest.setClientSecret("test-secret");
        
        AuthResponse response = authService.authenticate(authRequest);
        
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("test-client", response.getClientId());
        assertEquals("Cliente de Teste", response.getClientName());
    }
    
    @Test
    @Transactional
    public void testAuthenticateWithInvalidCredentials() {
        AuthRequest request = new AuthRequest();
        request.setClientId("invalid-client");
        request.setClientSecret("invalid-secret");
        
        assertThrows(WebApplicationException.class, () -> {
            authService.authenticate(request);
        });
    }
    
    @Test
    @Transactional
    public void testCreateClient() {
        AuthClientRequest request = new AuthClientRequest();
        request.setClientId("new-client");
        request.setClientSecret("new-secret");
        request.setNome("Novo Cliente");
        request.setDescricao("Descrição do novo cliente");
        
        AuthClientResponse response = authService.createClient(request);
        
        assertNotNull(response);
        assertEquals("new-client", response.getClientId());
        assertEquals("Novo Cliente", response.getNome());
        assertEquals("Descrição do novo cliente", response.getDescricao());
        assertEquals("ATIVO", response.getStatus());
        assertNotNull(response.getDataCriacao());
    }
    
    @Test
    @Transactional
    public void testCreateClientWithDuplicateClientId() {
        // Criar primeiro cliente
        AuthClientRequest request1 = new AuthClientRequest();
        request1.setClientId("duplicate-client");
        request1.setClientSecret("secret1");
        request1.setNome("Cliente 1");
        
        authService.createClient(request1);
        
        // Tentar criar segundo cliente com mesmo client_id
        AuthClientRequest request2 = new AuthClientRequest();
        request2.setClientId("duplicate-client");
        request2.setClientSecret("secret2");
        request2.setNome("Cliente 2");
        
        assertThrows(BadRequestException.class, () -> {
            authService.createClient(request2);
        });
    }
    
    @Test
    @Transactional
    public void testListAllClients() {
        // Criar alguns clientes
        AuthClientRequest request1 = new AuthClientRequest();
        request1.setClientId("client1");
        request1.setClientSecret("secret1");
        request1.setNome("Cliente 1");
        
        AuthClientRequest request2 = new AuthClientRequest();
        request2.setClientId("client2");
        request2.setClientSecret("secret2");
        request2.setNome("Cliente 2");
        
        authService.createClient(request1);
        authService.createClient(request2);
        
        List<AuthClientResponse> clients = authService.listAllClients();
        
        assertEquals(2, clients.size());
        assertTrue(clients.stream().anyMatch(c -> c.getClientId().equals("client1")));
        assertTrue(clients.stream().anyMatch(c -> c.getClientId().equals("client2")));
    }
    
    @Test
    @Transactional
    public void testGetClientById() {
        // Criar cliente
        AuthClientRequest request = new AuthClientRequest();
        request.setClientId("test-client");
        request.setClientSecret("test-secret");
        request.setNome("Cliente de Teste");
        
        AuthClientResponse created = authService.createClient(request);
        
        // Buscar por ID
        AuthClientResponse found = authService.getClientById(created.getId());
        
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("test-client", found.getClientId());
        assertEquals("Cliente de Teste", found.getNome());
    }
    
    @Test
    @Transactional
    public void testGetClientByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            authService.getClientById(999L);
        });
    }
    
    @Test
    @Transactional
    public void testUpdateClient() {
        // Criar cliente
        AuthClientRequest createRequest = new AuthClientRequest();
        createRequest.setClientId("update-client");
        createRequest.setClientSecret("old-secret");
        createRequest.setNome("Cliente Antigo");
        
        AuthClientResponse created = authService.createClient(createRequest);
        
        // Atualizar cliente
        AuthClientRequest updateRequest = new AuthClientRequest();
        updateRequest.setClientId("update-client-new");
        updateRequest.setClientSecret("new-secret");
        updateRequest.setNome("Cliente Atualizado");
        updateRequest.setDescricao("Nova descrição");
        
        AuthClientResponse updated = authService.updateClient(created.getId(), updateRequest);
        
        assertNotNull(updated);
        assertEquals("update-client-new", updated.getClientId());
        assertEquals("Cliente Atualizado", updated.getNome());
        assertEquals("Nova descrição", updated.getDescricao());
        assertNotNull(updated.getDataAtualizacao());
    }
    
    @Test
    @Transactional
    public void testInactivateClient() {
        // Criar cliente
        AuthClientRequest request = new AuthClientRequest();
        request.setClientId("inactivate-client");
        request.setClientSecret("secret");
        request.setNome("Cliente para Inativar");
        
        AuthClientResponse created = authService.createClient(request);
        assertEquals("ATIVO", created.getStatus());
        
        // Inativar cliente
        authService.inactivateClient(created.getId());
        
        AuthClientResponse inactivated = authService.getClientById(created.getId());
        assertEquals("INATIVO", inactivated.getStatus());
    }
    
    @Test
    @Transactional
    public void testActivateClient() {
        // Criar cliente
        AuthClientRequest request = new AuthClientRequest();
        request.setClientId("activate-client");
        request.setClientSecret("secret");
        request.setNome("Cliente para Ativar");
        
        AuthClientResponse created = authService.createClient(request);
        
        // Inativar primeiro
        authService.inactivateClient(created.getId());
        AuthClientResponse inactivated = authService.getClientById(created.getId());
        assertEquals("INATIVO", inactivated.getStatus());
        
        // Ativar novamente
        authService.activateClient(created.getId());
        AuthClientResponse activated = authService.getClientById(created.getId());
        assertEquals("ATIVO", activated.getStatus());
    }
    
    @Test
    @Transactional
    public void testDeleteClient() {
        // Criar cliente
        AuthClientRequest request = new AuthClientRequest();
        request.setClientId("delete-client");
        request.setClientSecret("secret");
        request.setNome("Cliente para Deletar");
        
        AuthClientResponse created = authService.createClient(request);
        
        // Deletar cliente
        authService.deleteClient(created.getId());
        
        // Verificar se foi deletado
        assertThrows(NotFoundException.class, () -> {
            authService.getClientById(created.getId());
        });
    }
} 