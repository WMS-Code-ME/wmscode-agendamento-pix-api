package br.com.wmscode.common.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PixAgendamentoRequest {
    
    @NotBlank(message = "Chave PIX é obrigatória")
    private String chavePix;
    
    @NotBlank(message = "Nome do beneficiário é obrigatório")
    private String nomeBeneficiario;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 140, message = "Descrição deve ter no máximo 140 caracteres")
    private String descricao;
    
    @NotNull(message = "Data de agendamento é obrigatória")
    @Future(message = "Data de agendamento deve ser futura")
    private LocalDateTime dataAgendamento;
    
    private String observacao;
} 