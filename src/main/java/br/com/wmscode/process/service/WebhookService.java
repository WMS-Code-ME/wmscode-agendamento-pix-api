package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.WebhookRequest;
import br.com.wmscode.common.dto.WebhookResponse;
import br.com.wmscode.common.mapper.WebhookMapper;
import br.com.wmscode.system.entity.Webhook;
import br.com.wmscode.system.repository.WebhookRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WebhookService {
    
    private static final Logger LOG = Logger.getLogger(WebhookService.class);
    
    @Inject
    WebhookRepository webhookRepository;
    
    @Inject
    WebhookMapper webhookMapper;
    
    @Transactional
    public WebhookResponse criarWebhook(WebhookRequest request) {
        LOG.info("Criando webhook para evento: " + request.getEvento());
        
        // Validar se já existe webhook ativo para este evento
        if (webhookRepository.existsAtivoPorEvento(request.getEvento())) {
            throw new WebApplicationException(
                "Já existe um webhook ativo para o evento: " + request.getEvento(), 
                Response.Status.CONFLICT
            );
        }
        
        Webhook webhook = webhookMapper.toEntity(request);
        webhookRepository.persist(webhook);
        
        LOG.info("Webhook criado com ID: " + webhook.id);
        return webhookMapper.toResponse(webhook);
    }
    
    @Transactional
    public WebhookResponse atualizarWebhook(Long id, WebhookRequest request) {
        LOG.info("Atualizando webhook ID: " + id);
        
        Webhook webhook = webhookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException(
                    "Webhook não encontrado",
                    Response.Status.NOT_FOUND));
        
        // Validar se já existe outro webhook ativo para este evento (excluindo o atual)
        if (webhookRepository.existsAtivoPorEventoExcluindoId(request.getEvento(), id)) {
            throw new WebApplicationException(
                "Já existe outro webhook ativo para o evento: " + request.getEvento(), 
                Response.Status.CONFLICT
            );
        }
        
        // Atualizar campos
        webhook.setUrl(request.getUrl());
        webhook.setLogin(request.getLogin());
        webhook.setSenha(request.getSenha());
        webhook.setDescricao(request.getDescricao());
        webhook.setBody(request.getBody());
        webhook.setEvento(request.getEvento());
        
        webhookRepository.persist(webhook);
        
        LOG.info("Webhook atualizado com sucesso");
        return webhookMapper.toResponse(webhook);
    }
    
    @Transactional
    public void deletarWebhook(Long id) {
        LOG.info("Deletando webhook ID: " + id);
        
        Webhook webhook = webhookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Webhook não encontrado", Response.Status.NOT_FOUND));
        
        webhookRepository.delete(webhook);
        LOG.info("Webhook deletado com sucesso");
    }
    
    @Transactional
    public WebhookResponse ativarWebhook(Long id) {
        LOG.info("Ativando webhook ID: " + id);
        
        Webhook webhook = webhookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Webhook não encontrado", Response.Status.NOT_FOUND));
        
        // Validar se já existe webhook ativo para este evento
        if (webhookRepository.existsAtivoPorEventoExcluindoId(webhook.getEvento(), id)) {
            throw new WebApplicationException(
                "Já existe outro webhook ativo para o evento: " + webhook.getEvento(), 
                Response.Status.CONFLICT
            );
        }
        
        webhook.setStatus(Webhook.StatusWebhook.ATIVO);
        webhookRepository.persist(webhook);
        
        LOG.info("Webhook ativado com sucesso");
        return webhookMapper.toResponse(webhook);
    }
    
    @Transactional
    public WebhookResponse inativarWebhook(Long id) {
        LOG.info("Inativando webhook ID: " + id);
        
        Webhook webhook = webhookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Webhook não encontrado", Response.Status.NOT_FOUND));
        
        webhook.setStatus(Webhook.StatusWebhook.INATIVO);
        webhookRepository.persist(webhook);
        
        LOG.info("Webhook inativado com sucesso");
        return webhookMapper.toResponse(webhook);
    }
    
    public List<WebhookResponse> listarWebhooks() {
        return webhookRepository.listAll().stream()
                .map(webhookMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public WebhookResponse buscarWebhook(Long id) {
        Webhook webhook = webhookRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Webhook não encontrado", Response.Status.NOT_FOUND));
        
        return webhookMapper.toResponse(webhook);
    }
    
    public List<Webhook> getWebhooksAtivos() {
        return webhookRepository.findAtivos();
    }
    
    public List<Webhook> getWebhooksAtivosPorEvento(Webhook.TipoEvento evento) {
        return webhookRepository.findAtivosPorEvento(evento);
    }
} 