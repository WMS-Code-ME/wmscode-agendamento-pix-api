package br.com.wmscode.experience.controller;

import br.com.wmscode.common.dto.PixAgendamentoRequest;
import br.com.wmscode.common.dto.PixAgendamentoResponse;
import br.com.wmscode.process.service.PixAgendamentoService;
import br.com.wmscode.system.entity.PixAgendamento;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/pix/agendamentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PixAgendamentoController {
    
    @Inject
    PixAgendamentoService service;
    
    @POST
    public Response criarAgendamento(@Valid PixAgendamentoRequest request) {
        PixAgendamentoResponse response = service.criarAgendamento(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
    
    @GET
    public Response listarAgendamentos() {
        List<PixAgendamentoResponse> agendamentos = service.listarTodos();
        return Response.ok(agendamentos).build();
    }
    
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        PixAgendamentoResponse agendamento = service.buscarPorId(id);
        return Response.ok(agendamento).build();
    }
    
    @GET
    @Path("/status/{status}")
    public Response buscarPorStatus(@PathParam("status") String status) {
        PixAgendamento.StatusAgendamento statusEnum = PixAgendamento.StatusAgendamento.valueOf(status.toUpperCase());
        List<PixAgendamentoResponse> agendamentos = service.buscarPorStatus(statusEnum);
        return Response.ok(agendamentos).build();
    }
    
    @GET
    @Path("/chave/{chavePix}")
    public Response buscarPorChavePix(@PathParam("chavePix") String chavePix) {
        List<PixAgendamentoResponse> agendamentos = service.buscarPorChavePix(chavePix);
        return Response.ok(agendamentos).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response cancelarAgendamento(@PathParam("id") Long id) {
        service.cancelarAgendamento(id);
        return Response.noContent().build();
    }
} 