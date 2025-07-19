package br.com.wmscode.system.repository;

import br.com.wmscode.system.entity.PixAgendamento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PixAgendamentoRepository implements PanacheRepository<PixAgendamento> {
    
    public List<PixAgendamento> findAgendamentosParaProcessar(LocalDateTime dataLimite) {
        return find("status = ?1 AND dataAgendamento <= ?2", 
                   PixAgendamento.StatusAgendamento.AGENDADO, dataLimite).list();
    }
    
    public List<PixAgendamento> findByStatus(PixAgendamento.StatusAgendamento status) {
        return find("status", status).list();
    }
    
    public List<PixAgendamento> findByChavePix(String chavePix) {
        return find("chavePix", chavePix).list();
    }
    
    public List<PixAgendamento> findByDataAgendamentoBetween(LocalDateTime inicio, LocalDateTime fim) {
        return find("dataAgendamento BETWEEN ?1 AND ?2", inicio, fim).list();
    }
} 