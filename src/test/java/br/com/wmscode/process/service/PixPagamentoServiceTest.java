package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.PixPagamentoRequest;
import br.com.wmscode.common.dto.PixPagamentoResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

@QuarkusTest
class PixPagamentoServiceTest {

    @Inject
    PixPagamentoService service;

    @Test
    void processarPagamento_DeveProcessarPagamentoComSucesso() {
        PixPagamentoRequest request = criarRequestValido();

        PixPagamentoResponse response = service.processarPagamento(request);

        assertNotNull(response);
        assertNotNull(response.getCodigoTransacao());
        assertTrue(response.getCodigoTransacao().startsWith("PIX"));
        assertEquals(request.getChavePix(), response.getChavePix());
        assertEquals(request.getNomeBeneficiario(), response.getNomeBeneficiario());
        assertEquals(request.getValor(), response.getValor());
        assertEquals(request.getDescricao(), response.getDescricao());
        assertEquals("PROCESSADO", response.getStatus());
        assertNotNull(response.getDataProcessamento());
        assertNotNull(response.getQrCode());
        assertNotNull(response.getQrCodeBase64());
    }

    @Test
    void processarPagamento_DeveGerarCodigoTransacaoUnico() {
        PixPagamentoRequest request = criarRequestValido();

        PixPagamentoResponse response1 = service.processarPagamento(request);
        PixPagamentoResponse response2 = service.processarPagamento(request);

        assertNotEquals(response1.getCodigoTransacao(), response2.getCodigoTransacao());
    }

    @Test
    void processarPagamento_DeveGerarQrCodeValido() {
        PixPagamentoRequest request = criarRequestValido();
        request.setChavePix("teste@email.com");
        request.setValor(new BigDecimal("50.00"));
        request.setNomeBeneficiario("João Silva");
        request.setDescricao("Teste PIX");

        PixPagamentoResponse response = service.processarPagamento(request);

        assertNotNull(response.getQrCode());
        assertTrue(response.getQrCode().startsWith("000201"));
        assertTrue(response.getQrCode().contains("br.gov.bcb.pix"));
        assertTrue(response.getQrCode().contains("teste@email.com"));
        assertTrue(response.getQrCode().contains("50,00"));
        assertTrue(response.getQrCode().endsWith("6304"));
    }

    @Test
    void processarPagamento_DeveGerarQrCodeBase64Valido() {
        PixPagamentoRequest request = criarRequestValido();

        PixPagamentoResponse response = service.processarPagamento(request);

        assertNotNull(response.getQrCodeBase64());
        assertTrue(response.getQrCodeBase64().length() > 0);
    }

    private PixPagamentoRequest criarRequestValido() {
        PixPagamentoRequest request = new PixPagamentoRequest();
        request.setChavePix("teste@email.com");
        request.setNomeBeneficiario("João Silva");
        request.setValor(new BigDecimal("100.00"));
        request.setDescricao("Pagamento teste");
        request.setObservacao("Observação de teste");
        return request;
    }
} 