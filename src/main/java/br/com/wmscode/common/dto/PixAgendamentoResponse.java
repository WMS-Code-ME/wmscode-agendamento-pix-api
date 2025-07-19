package br.com.wmscode.common.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PixAgendamentoResponse {
    
    private Long id;
    private String chavePix;
    private String nomeBeneficiario;
    private BigDecimal valor;
    private String descricao;
    private LocalDateTime dataAgendamento;
    private String observacao;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataProcessamento;
    private String codigoTransacao;
} 