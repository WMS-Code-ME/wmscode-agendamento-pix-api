package br.com.wmscode.system.repository;

import br.com.wmscode.system.entity.Webhook;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WebhookRepository implements PanacheRepository<Webhook> {
    
    public List<Webhook> findAtivos() {
        return find("status", Webhook.StatusWebhook.ATIVO).list();
    }
    
    public List<Webhook> findAtivosPorEvento(Webhook.TipoEvento evento) {
        return find("status = ?1 and evento = ?2", 
                   Webhook.StatusWebhook.ATIVO, evento).list();
    }
    
    public Optional<Webhook> findAtivoPorEvento(Webhook.TipoEvento evento) {
        return find("status = ?1 and evento = ?2", 
                   Webhook.StatusWebhook.ATIVO, evento).firstResultOptional();
    }
    
    public boolean existsAtivoPorEvento(Webhook.TipoEvento evento) {
        return count("status = ?1 and evento = ?2", 
                    Webhook.StatusWebhook.ATIVO, evento) > 0;
    }
    
    public boolean existsAtivoPorEventoExcluindoId(Webhook.TipoEvento evento, Long idExcluir) {
        return count("status = ?1 and evento = ?2 and id != ?3", 
                    Webhook.StatusWebhook.ATIVO, evento, idExcluir) > 0;
    }
} 