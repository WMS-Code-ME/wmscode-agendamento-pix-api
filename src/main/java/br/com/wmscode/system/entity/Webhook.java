package br.com.wmscode.system.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhooks")
public class Webhook extends PanacheEntity {
    
    @Column(name = "url", nullable = false)
    private String url;
    
    @Column(name = "login", nullable = false)
    private String login;
    
    @Column(name = "senha", nullable = false)
    private String senha;
    
    @Column(name = "descricao")
    private String descricao;
    
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "evento", nullable = false)
    private TipoEvento evento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusWebhook status = StatusWebhook.ATIVO;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    // Getters e Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getLogin() {
        return login;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public TipoEvento getEvento() {
        return evento;
    }
    
    public void setEvento(TipoEvento evento) {
        this.evento = evento;
    }
    
    public StatusWebhook getStatus() {
        return status;
    }
    
    public void setStatus(StatusWebhook status) {
        this.status = status;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
    public enum StatusWebhook {
        ATIVO, INATIVO
    }
    
    public enum TipoEvento {
        PAGAMENTO_PIX
    }
} 