package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.PixAgendamentoRequest;
import br.com.wmscode.common.dto.PixAgendamentoResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@QuarkusTest
class PixAgendamentoServiceTest {

    @Inject
    PixAgendamentoService service;

    @Test
    void criarAgendamento_DeveCriarAgendamentoComSucesso() {
        PixAgendamentoRequest request = criarRequestValido();

        PixAgendamentoResponse resultado = service.criarAgendamento(request);

        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(request.getChavePix(), resultado.getChavePix());
        assertEquals(request.getNomeBeneficiario(), resultado.getNomeBeneficiario());
        assertEquals(request.getValor(), resultado.getValor());
        assertEquals(request.getDescricao(), resultado.getDescricao());
        assertEquals(request.getDataAgendamento(), resultado.getDataAgendamento());
        assertEquals("AGENDADO", resultado.getStatus());
    }

    @Test
    void buscarPorId_QuandoExiste_DeveRetornarAgendamento() {
        PixAgendamentoRequest request = criarRequestValido();
        PixAgendamentoResponse agendamento = service.criarAgendamento(request);

        PixAgendamentoResponse resultado = service.buscarPorId(agendamento.getId());

        assertNotNull(resultado);
        assertEquals(agendamento.getId(), resultado.getId());
    }

    @Test
    void buscarPorId_QuandoNaoExiste_DeveLancarExcecao() {
        assertThrows(NotFoundException.class, () -> {
            service.buscarPorId(999L);
        });
    }

    @Test
    void listarTodos_DeveRetornarLista() {
        PixAgendamentoRequest request = criarRequestValido();
        service.criarAgendamento(request);

        List<PixAgendamentoResponse> resultado = service.listarTodos();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void cancelarAgendamento_DeveCancelarComSucesso() {
        PixAgendamentoRequest request = criarRequestValido();
        PixAgendamentoResponse agendamento = service.criarAgendamento(request);

        service.cancelarAgendamento(agendamento.getId());

        PixAgendamentoResponse resultado = service.buscarPorId(agendamento.getId());
        assertEquals("CANCELADO", resultado.getStatus());
    }

    private PixAgendamentoRequest criarRequestValido() {
        PixAgendamentoRequest request = new PixAgendamentoRequest();
        request.setChavePix("teste@email.com");
        request.setNomeBeneficiario("João Silva");
        request.setValor(new BigDecimal("100.00"));
        request.setDescricao("Teste de agendamento");
        request.setDataAgendamento(LocalDateTime.now().plusHours(1));
        request.setObservacao("Observação de teste");
        return request;
    }
} 