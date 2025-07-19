package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.PixPagamentoRequest;
import br.com.wmscode.common.dto.PixPagamentoResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class PixPagamentoService {
    
    public PixPagamentoResponse processarPagamento(PixPagamentoRequest request) {
        String codigoTransacao = gerarCodigoTransacao();
        
        PixPagamentoResponse response = new PixPagamentoResponse();
        response.setCodigoTransacao(codigoTransacao);
        response.setChavePix(request.getChavePix());
        response.setNomeBeneficiario(request.getNomeBeneficiario());
        response.setValor(request.getValor());
        response.setDescricao(request.getDescricao());
        response.setObservacao(request.getObservacao());
        response.setStatus("PROCESSADO");
        response.setDataProcessamento(LocalDateTime.now());
        response.setQrCode(gerarQrCode(request));
        response.setQrCodeBase64(gerarQrCodeBase64(request));
        
        return response;
    }
    
    private String gerarCodigoTransacao() {
        return "PIX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String gerarQrCode(PixPagamentoRequest request) {
        return String.format("00020126580014br.gov.bcb.pix0136%s520400005303986540%.2f5802BR5913%s6006%s62070503***6304",
            request.getChavePix(),
            request.getValor(),
            request.getNomeBeneficiario(),
            request.getDescricao());
    }
    
    private String gerarQrCodeBase64(PixPagamentoRequest request) {
        String qrCode = gerarQrCode(request);
        return java.util.Base64.getEncoder().encodeToString(qrCode.getBytes());
    }
} 