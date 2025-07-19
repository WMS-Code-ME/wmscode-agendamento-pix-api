package br.com.wmscode.system.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pix_agendamentos")
public class PixAgendamento extends PanacheEntity {
    
    @Column(name = "chave_pix", nullable = false)
    private String chavePix;
    
    @Column(name = "nome_beneficiario", nullable = false)
    private String nomeBeneficiario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(nullable = false, length = 140)
    private String descricao;
    
    @Column(name = "data_agendamento", nullable = false)
    private LocalDateTime dataAgendamento;
    
    @Column(length = 500)
    private String observacao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_processamento")
    private LocalDateTime dataProcessamento;
    
    @Column(name = "codigo_transacao", length = 50)
    private String codigoTransacao;
    
    public enum StatusAgendamento {
        AGENDADO,
        PROCESSANDO,
        PROCESSADO,
        ENVIADO,
        ERRO,
        CANCELADO
    }
    
    public String getChavePix() {
        return chavePix;
    }
    
    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }
    
    public String getNomeBeneficiario() {
        return nomeBeneficiario;
    }
    
    public void setNomeBeneficiario(String nomeBeneficiario) {
        this.nomeBeneficiario = nomeBeneficiario;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }
    
    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }
    
    public String getObservacao() {
        return observacao;
    }
    
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    public StatusAgendamento getStatus() {
        return status;
    }
    
    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }
    
    public void setDataProcessamento(LocalDateTime dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }
    
    public String getCodigoTransacao() {
        return codigoTransacao;
    }
    
    public void setCodigoTransacao(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
} 