package br.com.wmscode.process.service;

import br.com.wmscode.system.entity.PixAgendamento;
import br.com.wmscode.system.entity.Webhook;
import br.com.wmscode.system.repository.PixAgendamentoRepository;
import br.com.wmscode.system.repository.WebhookRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class BatchServiceTest {
    
    @Inject
    BatchService batchService;
    
    @Inject
    PixAgendamentoRepository agendamentoRepository;
    
    @Inject
    WebhookRepository webhookRepository;
    
    @BeforeEach
    @Transactional
    void setUp() {
        // Limpar dados antes de cada teste
        agendamentoRepository.deleteAll();
        webhookRepository.deleteAll();
    }
    
    @Test
    @Transactional
    public void testProcessarPagamentosAgendadosSemAgendamentos() {
        // Teste quando não há agendamentos para processar
        batchService.processarPagamentosAgendados();
        
        // Não deve lançar exceção
        assertTrue(true);
    }
    
    @Test
    @Transactional
    public void testProcessarPagamentosAgendadosSemWebhooks() {
        // Criar agendamento para hoje
        PixAgendamento agendamento = new PixAgendamento();
        agendamento.setChavePix("teste@teste.com");
        agendamento.setNomeBeneficiario("Teste");
        agendamento.setValor(new BigDecimal("100.00"));
        agendamento.setDescricao("Teste de agendamento");
        agendamento.setDataAgendamento(LocalDateTime.now());
        agendamento.setDataCriacao(LocalDateTime.now());
        agendamento.setStatus(PixAgendamento.StatusAgendamento.AGENDADO);
        
        agendamentoRepository.persist(agendamento);
        
        // Executar batch sem webhooks
        batchService.processarPagamentosAgendados();
        
        // Agendamento deve permanecer AGENDADO pois não há webhooks
        PixAgendamento agendamentoAtualizado = agendamentoRepository.findById(agendamento.id);
        assertEquals(PixAgendamento.StatusAgendamento.AGENDADO, agendamentoAtualizado.getStatus());
    }
    
    @Test
    @Transactional
    public void testProcessarPagamentosAgendadosComWebhook() {
        // Criar webhook ativo
        Webhook webhook = new Webhook();
        webhook.setUrl("https://httpbin.org/post");
        webhook.setLogin("teste");
        webhook.setSenha("teste");
        webhook.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        webhook.setStatus(Webhook.StatusWebhook.ATIVO);
        webhook.setDataCriacao(LocalDateTime.now());
        
        webhookRepository.persist(webhook);
        
        // Criar agendamento para hoje
        PixAgendamento agendamento = new PixAgendamento();
        agendamento.setChavePix("teste@teste.com");
        agendamento.setNomeBeneficiario("Teste");
        agendamento.setValor(new BigDecimal("100.00"));
        agendamento.setDescricao("Teste de agendamento");
        agendamento.setDataAgendamento(LocalDateTime.now());
        agendamento.setDataCriacao(LocalDateTime.now());
        agendamento.setStatus(PixAgendamento.StatusAgendamento.AGENDADO);
        
        agendamentoRepository.persist(agendamento);
        
        // Executar batch
        batchService.processarPagamentosAgendados();
        
        // Agendamento deve ser marcado como ENVIADO
        PixAgendamento agendamentoAtualizado = agendamentoRepository.findById(agendamento.id);
        assertEquals(PixAgendamento.StatusAgendamento.ENVIADO, agendamentoAtualizado.getStatus());
        assertNotNull(agendamentoAtualizado.getDataProcessamento());
    }
    
    @Test
    @Transactional
    public void testProcessarPagamentosAgendadosApenasDoDia() {
        // Criar webhook ativo
        Webhook webhook = new Webhook();
        webhook.setUrl("https://httpbin.org/post");
        webhook.setLogin("teste");
        webhook.setSenha("teste");
        webhook.setEvento(Webhook.TipoEvento.PAGAMENTO_PIX);
        webhook.setStatus(Webhook.StatusWebhook.ATIVO);
        webhook.setDataCriacao(LocalDateTime.now());
        
        webhookRepository.persist(webhook);
        
        // Criar agendamento para hoje
        PixAgendamento agendamentoHoje = new PixAgendamento();
        agendamentoHoje.setChavePix("hoje@teste.com");
        agendamentoHoje.setNomeBeneficiario("Teste Hoje");
        agendamentoHoje.setValor(new BigDecimal("100.00"));
        agendamentoHoje.setDescricao("Teste de agendamento hoje");
        agendamentoHoje.setDataAgendamento(LocalDateTime.now());
        agendamentoHoje.setDataCriacao(LocalDateTime.now());
        agendamentoHoje.setStatus(PixAgendamento.StatusAgendamento.AGENDADO);
        
        agendamentoRepository.persist(agendamentoHoje);
        
        // Criar agendamento para amanhã
        PixAgendamento agendamentoAmanha = new PixAgendamento();
        agendamentoAmanha.setChavePix("amanha@teste.com");
        agendamentoAmanha.setNomeBeneficiario("Teste Amanhã");
        agendamentoAmanha.setValor(new BigDecimal("200.00"));
        agendamentoAmanha.setDescricao("Teste de agendamento amanhã");
        agendamentoAmanha.setDataAgendamento(LocalDateTime.now().plusDays(1));
        agendamentoAmanha.setDataCriacao(LocalDateTime.now());
        agendamentoAmanha.setStatus(PixAgendamento.StatusAgendamento.AGENDADO);
        
        agendamentoRepository.persist(agendamentoAmanha);
        
        // Executar batch
        batchService.processarPagamentosAgendados();
        
        // Apenas o agendamento de hoje deve ser processado
        PixAgendamento agendamentoHojeAtualizado = agendamentoRepository.findById(agendamentoHoje.id);
        PixAgendamento agendamentoAmanhaAtualizado = agendamentoRepository.findById(agendamentoAmanha.id);
        
        assertEquals(PixAgendamento.StatusAgendamento.ENVIADO, agendamentoHojeAtualizado.getStatus());
        assertEquals(PixAgendamento.StatusAgendamento.AGENDADO, agendamentoAmanhaAtualizado.getStatus());
    }
} 