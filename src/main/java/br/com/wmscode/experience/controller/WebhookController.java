package br.com.wmscode.experience.controller;

import br.com.wmscode.common.dto.WebhookRequest;
import br.com.wmscode.common.dto.WebhookResponse;
import br.com.wmscode.process.service.WebhookService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/v1/webhooks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WebhookController {
    
    private static final Logger LOG = Logger.getLogger(WebhookController.class);
    
    @Inject
    WebhookService service;
    
    @GET
    public List<WebhookResponse> listarWebhooks() {
        LOG.info("Listando todos os webhooks");
        return service.listarWebhooks();
    }
    
    @GET
    @Path("/ativos")
    public List<WebhookResponse> listarWebhooksAtivos() {
        LOG.info("Listando webhooks ativos");
        return service.listarWebhooks().stream()
                .filter(webhook -> webhook.getStatus() == br.com.wmscode.system.entity.Webhook.StatusWebhook.ATIVO)
                .toList();
    }
    
    @GET
    @Path("/{id}")
    public WebhookResponse buscarWebhook(@PathParam("id") Long id) {
        LOG.info("Buscando webhook ID: " + id);
        return service.buscarWebhook(id);
    }
    
    @POST
    public Response criarWebhook(@Valid WebhookRequest request) {
        LOG.info("Criando novo webhook");
        WebhookResponse webhook = service.criarWebhook(request);
        return Response.status(Response.Status.CREATED).entity(webhook).build();
    }
    
    @PUT
    @Path("/{id}")
    public WebhookResponse atualizarWebhook(@PathParam("id") Long id, @Valid WebhookRequest request) {
        LOG.info("Atualizando webhook ID: " + id);
        return service.atualizarWebhook(id, request);
    }
    
    @DELETE
    @Path("/{id}")
    public Response deletarWebhook(@PathParam("id") Long id) {
        LOG.info("Deletando webhook ID: " + id);
        service.deletarWebhook(id);
        return Response.noContent().build();
    }
    
    @PUT
    @Path("/{id}/ativar")
    public WebhookResponse ativarWebhook(@PathParam("id") Long id) {
        LOG.info("Ativando webhook ID: " + id);
        return service.ativarWebhook(id);
    }
    
    @PUT
    @Path("/{id}/inativar")
    public WebhookResponse inativarWebhook(@PathParam("id") Long id) {
        LOG.info("Inativando webhook ID: " + id);
        return service.inativarWebhook(id);
    }
} 