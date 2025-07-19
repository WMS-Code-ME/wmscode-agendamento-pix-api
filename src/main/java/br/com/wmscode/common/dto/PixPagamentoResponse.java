package br.com.wmscode.common.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PixPagamentoResponse {
    
    private String codigoTransacao;
    private String chavePix;
    private String nomeBeneficiario;
    private BigDecimal valor;
    private String descricao;
    private String observacao;
    private String status;
    private LocalDateTime dataProcessamento;
    private String qrCode;
    private String qrCodeBase64;
} 