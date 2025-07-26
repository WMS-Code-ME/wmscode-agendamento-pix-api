package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.PagamentoWebhookRequest;
import br.com.wmscode.process.client.PagamentoClient;
import br.com.wmscode.system.entity.PixAgendamento;
import br.com.wmscode.system.entity.Webhook;
import br.com.wmscode.system.repository.PixAgendamentoRepository;
import br.com.wmscode.system.repository.WebhookRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class BatchService {
    
    private static final Logger LOG = Logger.getLogger(BatchService.class);
    
    private final PixAgendamentoRepository agendamentoRepository;
    private final WebhookRepository webhookRepository;

    @Inject
    public BatchService(PixAgendamentoRepository agendamentoRepository, WebhookRepository webhookRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.webhookRepository = webhookRepository;
    }
    
    /**
     * Executa uma vez por dia às 8h da manhã para processar agendamentos do dia
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void processarPagamentosAgendados() {
        LOG.info("Iniciando processamento diário de pagamentos agendados");
        
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDia = hoje.atStartOfDay();
        LocalDateTime fimDia = hoje.atTime(23, 59, 59);
        
        // Buscar agendamentos do dia atual com status AGENDADO
        List<PixAgendamento> agendamentos = agendamentoRepository.findAgendamentosDoDia(inicioDia, fimDia);
        
        if (agendamentos.isEmpty()) {
            LOG.info("Nenhum agendamento encontrado para o dia: " + hoje);
            return;
        }
        
        LOG.info("Encontrados " + agendamentos.size() + " agendamentos para processar no dia: " + hoje);
        
        // Buscar webhooks ativos para o evento PAGAMENTO_PIX
        List<Webhook> webhooks = webhookRepository.findAtivosPorEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        
        if (webhooks.isEmpty()) {
            LOG.warn("Nenhum webhook ativo encontrado para o evento PAGAMENTO_PIX");
            return;
        }
        
        LOG.info("Encontrados " + webhooks.size() + " webhooks ativos para notificação");
        
        // Processar cada agendamento individualmente
        for (PixAgendamento agendamento : agendamentos) {
            processarAgendamentoIndividual(agendamento, webhooks);
        }
        
        LOG.info("Processamento diário concluído");
    }
    
    /**
     * Processa um agendamento individual, enviando para todos os webhooks
     */
    private void processarAgendamentoIndividual(PixAgendamento agendamento, List<Webhook> webhooks) {
        LOG.info("Processando agendamento ID: " + agendamento.id + " - Valor: " + agendamento.getValor() + 
                " - Data: " + agendamento.getDataAgendamento());
        
        boolean sucessoEnvio = false;
        String erroDetalhado = "";
        
        try {
            // Criar payload para o webhook
            PagamentoWebhookRequest payload = new PagamentoWebhookRequest();
            payload.setIdAgendamento(agendamento.id);
            payload.setValor(agendamento.getValor());
            payload.setChavePix(agendamento.getChavePix());
            payload.setNomeBeneficiario(agendamento.getNomeBeneficiario());
            payload.setDescricao(agendamento.getDescricao());
            payload.setDataAgendamento(agendamento.getDataAgendamento());
            payload.setDataProcessamento(LocalDateTime.now());
            
            // Tentar enviar para cada webhook
            for (Webhook webhook : webhooks) {
                try {
                    enviarParaWebhook(webhook, payload);
                    LOG.debug("Agendamento " + agendamento.id + " enviado com sucesso para webhook: " + webhook.getUrl());
                    sucessoEnvio = true;
                } catch (Exception e) {
                    String erro = "Erro ao enviar para webhook " + webhook.getUrl() + ": " + e.getMessage();
                    LOG.error(erro, e);
                    erroDetalhado += erro + "; ";
                }
            }
            
            // Atualizar status do agendamento
            if (sucessoEnvio) {
                agendamento.setStatus(PixAgendamento.StatusAgendamento.ENVIADO);
                agendamento.setDataProcessamento(LocalDateTime.now());
                agendamentoRepository.persist(agendamento);
                LOG.info("Agendamento " + agendamento.id + " marcado como ENVIADO");
            } else {
                agendamento.setStatus(PixAgendamento.StatusAgendamento.ERRO);
                agendamento.setObservacao("Falha no envio para todos os webhooks: " + erroDetalhado);
                agendamento.setDataProcessamento(LocalDateTime.now());
                agendamentoRepository.persist(agendamento);
                LOG.error("Agendamento " + agendamento.id + " marcado como ERRO: " + erroDetalhado);
            }
            
        } catch (Exception e) {
            String erro = "Erro geral no processamento do agendamento " + agendamento.id + ": " + e.getMessage();
            LOG.error(erro, e);
            
            agendamento.setStatus(PixAgendamento.StatusAgendamento.ERRO);
            agendamento.setObservacao(erro);
            agendamento.setDataProcessamento(LocalDateTime.now());
            agendamentoRepository.persist(agendamento);
        }
    }
    
    /**
     * Envia o payload para um webhook específico
     */
    private void enviarParaWebhook(Webhook webhook, PagamentoWebhookRequest payload) {
        try {
            // Criar cliente REST dinâmico para a URL do webhook
            PagamentoClient client = RestClientBuilder.newBuilder()
                    .baseUri(URI.create(webhook.getUrl()))
                    .build(PagamentoClient.class);
            
            // Configurar autenticação básica
            String auth = java.util.Base64.getEncoder()
                    .encodeToString((webhook.getLogin() + ":" + webhook.getSenha()).getBytes());
            
            // Se o webhook tem um body personalizado, usar ele, senão usar o payload padrão
            Object bodyToSend;
            if (webhook.getBody() != null && !webhook.getBody().trim().isEmpty()) {
                bodyToSend = webhook.getBody();
                LOG.debug("Usando body personalizado do webhook: " + webhook.getBody());
            } else {
                bodyToSend = payload;
                LOG.debug("Usando payload padrão para webhook");
            }
            
            // Fazer a chamada HTTP
            Response response = client.enviarPagamento(bodyToSend);
            
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                LOG.debug("Webhook " + webhook.getUrl() + " respondeu com sucesso: " + response.getStatus());
            } else {
                throw new RuntimeException("Webhook respondeu com status: " + response.getStatus());
            }
            
        } catch (Exception e) {
            LOG.error("Erro ao enviar para webhook " + webhook.getUrl() + ": " + e.getMessage(), e);
            throw e;
        }
    }
} 