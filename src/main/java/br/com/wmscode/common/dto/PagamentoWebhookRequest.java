package br.com.wmscode.common.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagamentoWebhookRequest {
    
    private Long idAgendamento;
    private String codigoTransacao;
    private String chavePix;
    private String nomeBeneficiario;
    private BigDecimal valor;
    private String descricao;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataProcessamento;
    private String status;
} 