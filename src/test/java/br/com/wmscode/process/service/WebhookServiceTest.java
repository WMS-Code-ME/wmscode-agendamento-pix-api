package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.WebhookRequest;
import br.com.wmscode.common.dto.WebhookResponse;
import br.com.wmscode.system.entity.Webhook;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class WebhookServiceTest {
    
    @Inject
    WebhookService webhookService;
    
    @Inject
    br.com.wmscode.system.repository.WebhookRepository webhookRepository;
    
    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar todos os webhooks antes de cada teste
        webhookRepository.deleteAll();
    }
    
    @Test
    @Transactional
    public void testCriarWebhook() {
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setDescricao("Webhook de teste");
        request.setBody("{\"custom\": \"body\"}");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        WebhookResponse response = webhookService.criarWebhook(request);
        
        assertNotNull(response.getId());
        assertEquals(request.getUrl(), response.getUrl());
        assertEquals(request.getLogin(), response.getLogin());
        assertEquals(request.getDescricao(), response.getDescricao());
        assertEquals(request.getBody(), response.getBody());
        assertEquals(request.getEvento(), response.getEvento());
        assertEquals(Webhook.StatusWebhook.ATIVO, response.getStatus());
    }
    
    @Test
    @Transactional
    public void testCriarWebhookDuplicado() {
        WebhookRequest request1 = new WebhookRequest();
        request1.setUrl("https://api.exemplo1.com/webhook");
        request1.setLogin("usuario1");
        request1.setSenha("senha123");
        request1.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        WebhookRequest request2 = new WebhookRequest();
        request2.setUrl("https://api.exemplo2.com/webhook");
        request2.setLogin("usuario2");
        request2.setSenha("senha456");
        request2.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        // Criar primeiro webhook
        webhookService.criarWebhook(request1);
        
        // Tentar criar segundo webhook com mesmo evento deve falhar
        assertThrows(WebApplicationException.class, () -> {
            webhookService.criarWebhook(request2);
        });
    }
    
    @Test
    @Transactional
    public void testAtualizarWebhook() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        WebhookResponse webhook = webhookService.criarWebhook(request);
        
        // Atualizar webhook
        WebhookRequest updateRequest = new WebhookRequest();
        updateRequest.setUrl("https://api.novo.com/webhook");
        updateRequest.setLogin("novo_usuario");
        updateRequest.setSenha("nova_senha");
        updateRequest.setDescricao("Webhook atualizado");
        updateRequest.setBody("{\"novo\": \"body\"}");
        updateRequest.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        WebhookResponse updated = webhookService.atualizarWebhook(webhook.getId(), updateRequest);
        
        assertEquals(updateRequest.getUrl(), updated.getUrl());
        assertEquals(updateRequest.getLogin(), updated.getLogin());
        assertEquals(updateRequest.getDescricao(), updated.getDescricao());
        assertEquals(updateRequest.getBody(), updated.getBody());
    }
    
    @Test
    @Transactional
    public void testListarWebhooks() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        webhookService.criarWebhook(request);
        
        List<WebhookResponse> webhooks = webhookService.listarWebhooks();
        assertFalse(webhooks.isEmpty());
    }
    
    @Test
    @Transactional
    public void testBuscarWebhook() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        WebhookResponse created = webhookService.criarWebhook(request);
        
        WebhookResponse found = webhookService.buscarWebhook(created.getId());
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getUrl(), found.getUrl());
    }
    
    @Test
    @Transactional
    public void testAtivarWebhook() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        WebhookResponse webhook = webhookService.criarWebhook(request);
        
        // Inativar primeiro
        webhookService.inativarWebhook(webhook.getId());
        
        // Ativar novamente
        WebhookResponse activated = webhookService.ativarWebhook(webhook.getId());
        assertEquals(Webhook.StatusWebhook.ATIVO, activated.getStatus());
    }
    
    @Test
    @Transactional
    public void testInativarWebhook() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        WebhookResponse webhook = webhookService.criarWebhook(request);
        
        // Inativar
        WebhookResponse inactivated = webhookService.inativarWebhook(webhook.getId());
        assertEquals(Webhook.StatusWebhook.INATIVO, inactivated.getStatus());
    }
    
    @Test
    @Transactional
    public void testDeletarWebhook() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        WebhookResponse webhook = webhookService.criarWebhook(request);
        
        // Deletar
        webhookService.deletarWebhook(webhook.getId());
        
        // Tentar buscar deve falhar
        assertThrows(WebApplicationException.class, () -> {
            webhookService.buscarWebhook(webhook.getId());
        });
    }
} 