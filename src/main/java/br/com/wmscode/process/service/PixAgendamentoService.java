package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.PixAgendamentoRequest;
import br.com.wmscode.common.dto.PixAgendamentoResponse;
import br.com.wmscode.common.mapper.PixAgendamentoMapper;
import br.com.wmscode.system.entity.PixAgendamento;
import br.com.wmscode.system.repository.PixAgendamentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PixAgendamentoService {
    
    @Inject
    PixAgendamentoRepository repository;
    
    @Inject
    PixAgendamentoMapper mapper;
    
    @Transactional
    public PixAgendamentoResponse criarAgendamento(PixAgendamentoRequest request) {
        PixAgendamento agendamento = mapper.toEntity(request);
        repository.persist(agendamento);
        return mapper.toResponse(agendamento);
    }
    
    public PixAgendamentoResponse buscarPorId(Long id) {
        PixAgendamento agendamento = repository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));
        return mapper.toResponse(agendamento);
    }
    
    public List<PixAgendamentoResponse> listarTodos() {
        return repository.listAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<PixAgendamentoResponse> buscarPorStatus(PixAgendamento.StatusAgendamento status) {
        return repository.findByStatus(status).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<PixAgendamentoResponse> buscarPorChavePix(String chavePix) {
        return repository.findByChavePix(chavePix).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelarAgendamento(Long id) {
        PixAgendamento agendamento = repository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));
        
        if (agendamento.getStatus() != PixAgendamento.StatusAgendamento.AGENDADO) {
            throw new IllegalStateException("Apenas agendamentos com status AGENDADO podem ser cancelados");
        }
        
        agendamento.setStatus(PixAgendamento.StatusAgendamento.CANCELADO);
        repository.persist(agendamento);
    }
    
    @Transactional
    public void processarAgendamento(PixAgendamento agendamento) {
        agendamento.setStatus(PixAgendamento.StatusAgendamento.PROCESSANDO);
        repository.persist(agendamento);
    }
    
    @Transactional
    public void finalizarProcessamento(PixAgendamento agendamento, String codigoTransacao) {
        agendamento.setStatus(PixAgendamento.StatusAgendamento.PROCESSADO);
        agendamento.setDataProcessamento(java.time.LocalDateTime.now());
        agendamento.setCodigoTransacao(codigoTransacao);
        repository.persist(agendamento);
    }
    
    @Transactional
    public void marcarErro(PixAgendamento agendamento) {
        agendamento.setStatus(PixAgendamento.StatusAgendamento.ERRO);
        agendamento.setDataProcessamento(java.time.LocalDateTime.now());
        repository.persist(agendamento);
    }
} 