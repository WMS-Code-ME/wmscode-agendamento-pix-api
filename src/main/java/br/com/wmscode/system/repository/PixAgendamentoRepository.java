package br.com.wmscode.system.repository;

import br.com.wmscode.system.entity.PixAgendamento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PixAgendamentoRepository implements PanacheRepository<PixAgendamento> {
    
    public List<PixAgendamento> findAgendamentosParaProcessar() {
        return find("status = ?1 and dataAgendamento <= ?2", 
                   PixAgendamento.StatusAgendamento.AGENDADO, LocalDateTime.now()).list();
    }
    
    public List<PixAgendamento> findAgendamentosDoDia(LocalDateTime inicioDia, LocalDateTime fimDia) {
        return find("status = ?1 and dataAgendamento >= ?2 and dataAgendamento <= ?3", 
                   PixAgendamento.StatusAgendamento.AGENDADO, inicioDia, fimDia).list();
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