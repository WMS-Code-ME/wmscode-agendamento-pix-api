package br.com.wmscode.experience.controller;

import br.com.wmscode.common.dto.WebhookRequest;
import br.com.wmscode.system.entity.Webhook;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
public class WebhookControllerTest {
    
    @Inject
    br.com.wmscode.system.repository.WebhookRepository webhookRepository;
    
    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar todos os webhooks antes de cada teste
        webhookRepository.deleteAll();
    }
    
    @Test
    public void testCriarWebhook() {
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setDescricao("Webhook de teste");
        request.setBody("{\"custom\": \"body\"}");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/webhooks")
        .then()
            .statusCode(201)
            .body("url", equalTo(request.getUrl()))
            .body("login", equalTo(request.getLogin()))
            .body("descricao", equalTo(request.getDescricao()))
            .body("body", equalTo(request.getBody()))
            .body("evento", equalTo(request.getEvento().toString()))
            .body("status", equalTo(Webhook.StatusWebhook.ATIVO.toString()));
    }
    
    @Test
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
        given()
            .contentType(ContentType.JSON)
            .body(request1)
        .when()
            .post("/api/v1/webhooks")
        .then()
            .statusCode(201);
        
        // Tentar criar segundo webhook com mesmo evento deve falhar
        given()
            .contentType(ContentType.JSON)
            .body(request2)
        .when()
            .post("/api/v1/webhooks")
        .then()
            .statusCode(409);
    }
    
    @Test
    public void testBuscarWebhook() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        Integer id = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/webhooks")
        .then()
            .statusCode(201)
            .extract()
            .path("id");
        
        // Buscar webhook
        given()
        .when()
            .get("/api/v1/webhooks/" + id)
        .then()
            .statusCode(200)
            .body("id", equalTo(id))
            .body("url", equalTo(request.getUrl()));
    }
    
    @Test
    public void testListarWebhooks() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/webhooks")
        .then()
            .statusCode(201);
        
        // Listar webhooks
        given()
        .when()
            .get("/api/v1/webhooks")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
    }
    
    @Test
    public void testAtivarWebhook() {
        // Criar webhook
        WebhookRequest request = new WebhookRequest();
        request.setUrl("https://api.exemplo.com/webhook");
        request.setLogin("usuario");
        request.setSenha("senha123");
        request.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        Integer id = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/webhooks")
        .then()
            .statusCode(201)
            .extract()
            .path("id");
        
        // Inativar primeiro
        given()
        .when()
            .put("/api/v1/webhooks/" + id + "/inativar")
        .then()
            .statusCode(200);
        
        // Ativar novamente
        given()
        .when()
            .put("/api/v1/webhooks/" + id + "/ativar")
        .then()
            .statusCode(200)
            .body("status", equalTo(Webhook.StatusWebhook.ATIVO.toString()));
    }
} 