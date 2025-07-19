package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.PixPagamentoRequest;
import br.com.wmscode.system.entity.PixAgendamento;
import br.com.wmscode.system.repository.PixAgendamentoRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ProcessadorAgendamentosService {
    
    @Inject
    PixAgendamentoRepository repository;
    
    @Inject
    PixAgendamentoService agendamentoService;
    
    @Inject
    PixPagamentoService pagamentoService;
    
    @Scheduled(every = "1m")
    @Transactional
    void processarAgendamentos() {
        LocalDateTime dataLimite = LocalDateTime.now();
        List<PixAgendamento> agendamentos = repository.findAgendamentosParaProcessar(dataLimite);
        
        for (PixAgendamento agendamento : agendamentos) {
            try {
                processarAgendamento(agendamento);
            } catch (Exception e) {
                agendamentoService.marcarErro(agendamento);
            }
        }
    }
    
    private void processarAgendamento(PixAgendamento agendamento) {
        agendamentoService.processarAgendamento(agendamento);
        
        PixPagamentoRequest pagamentoRequest = new PixPagamentoRequest();
        pagamentoRequest.setChavePix(agendamento.getChavePix());
        pagamentoRequest.setNomeBeneficiario(agendamento.getNomeBeneficiario());
        pagamentoRequest.setValor(agendamento.getValor());
        pagamentoRequest.setDescricao(agendamento.getDescricao());
        pagamentoRequest.setObservacao(agendamento.getObservacao());
        
        var pagamentoResponse = pagamentoService.processarPagamento(pagamentoRequest);
        
        agendamentoService.finalizarProcessamento(agendamento, pagamentoResponse.getCodigoTransacao());
    }
} 