package br.com.wmscode.process.service;

import br.com.wmscode.common.dto.PixAgendamentoRequest;
import br.com.wmscode.common.dto.PixAgendamentoResponse;
import br.com.wmscode.system.entity.PixAgendamento;
import br.com.wmscode.system.repository.PixAgendamentoRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PixAgendamentoServiceTest {
    
    @Inject
    PixAgendamentoService service;
    
    @Inject
    PixAgendamentoRepository agendamentoRepository;
    
    @Test
    @Transactional
    public void testCriarAgendamento() {
        PixAgendamentoRequest request = criarRequestValido();
        
        PixAgendamentoResponse resultado = service.criarAgendamento(request);
        
        assertNotNull(resultado.getId());
        assertEquals(request.getChavePix(), resultado.getChavePix());
        assertEquals(request.getNomeBeneficiario(), resultado.getNomeBeneficiario());
        assertEquals(request.getValor(), resultado.getValor());
        assertEquals("AGENDADO", resultado.getStatus());
    }
    
    @Test
    @Transactional
    public void testBuscarPorId() {
        PixAgendamentoRequest request = criarRequestValido();
        PixAgendamentoResponse criado = service.criarAgendamento(request);
        
        PixAgendamentoResponse resultado = service.buscarPorId(criado.getId());
        
        assertEquals(criado.getId(), resultado.getId());
        assertEquals(request.getChavePix(), resultado.getChavePix());
    }
    
    @Test
    public void testBuscarPorIdNaoEncontrado() {
        assertThrows(NotFoundException.class, () -> {
            service.buscarPorId(999L);
        });
    }
    
    @Test
    @Transactional
    public void testListarTodos() {
        PixAgendamentoRequest request1 = criarRequestValido();
        PixAgendamentoRequest request2 = criarRequestValido();
        request2.setChavePix("teste2@teste.com");
        
        service.criarAgendamento(request1);
        service.criarAgendamento(request2);
        
        List<PixAgendamentoResponse> resultado = service.listarTodos();
        
        assertTrue(resultado.size() >= 2);
    }
    
    @Test
    @Transactional
    public void testBuscarPorStatus() {
        PixAgendamentoRequest request = criarRequestValido();
        service.criarAgendamento(request);
        
        List<PixAgendamentoResponse> resultado = service.buscarPorStatus(PixAgendamento.StatusAgendamento.AGENDADO);
        
        assertFalse(resultado.isEmpty());
        assertEquals("AGENDADO", resultado.get(0).getStatus());
    }
    
    @Test
    @Transactional
    public void testBuscarPorChavePix() {
        PixAgendamentoRequest request = criarRequestValido();
        service.criarAgendamento(request);
        
        List<PixAgendamentoResponse> resultado = service.buscarPorChavePix("teste@teste.com");
        
        assertFalse(resultado.isEmpty());
        assertEquals("teste@teste.com", resultado.get(0).getChavePix());
    }
    
    @Test
    @Transactional
    public void testCancelarAgendamento() {
        PixAgendamentoRequest request = criarRequestValido();
        PixAgendamentoResponse criado = service.criarAgendamento(request);
        
        service.cancelarAgendamento(criado.getId());
        
        PixAgendamentoResponse resultado = service.buscarPorId(criado.getId());
        assertEquals("CANCELADO", resultado.getStatus());
    }
    
    @Test
    @Transactional
    public void testAtualizarStatusAgendamento() {
        PixAgendamentoRequest request = criarRequestValido();
        PixAgendamentoResponse criado = service.criarAgendamento(request);
        
        // Buscar a entidade para atualizar
        PixAgendamento agendamento = agendamentoRepository.findById(criado.getId());
        
        // Atualizar status para ENVIADO
        agendamento.setStatus(PixAgendamento.StatusAgendamento.ENVIADO);
        agendamento.setDataProcessamento(LocalDateTime.now());
        
        service.atualizarAgendamento(agendamento);
        
        PixAgendamentoResponse atualizado = service.buscarPorId(criado.getId());
        assertEquals(PixAgendamento.StatusAgendamento.ENVIADO.toString(), atualizado.getStatus());
        assertNotNull(atualizado.getDataProcessamento());
    }
    
    private PixAgendamentoRequest criarRequestValido() {
        PixAgendamentoRequest request = new PixAgendamentoRequest();
        request.setChavePix("teste@teste.com");
        request.setNomeBeneficiario("Teste");
        request.setValor(new BigDecimal("100.00"));
        request.setDescricao("Teste de agendamento");
        request.setDataAgendamento(LocalDateTime.now().plusDays(1));
        return request;
    }
} 