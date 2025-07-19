package br.com.wmscode.experience.controller;

import br.com.wmscode.common.dto.PixPagamentoRequest;
import br.com.wmscode.common.dto.PixPagamentoResponse;
import br.com.wmscode.process.service.PixPagamentoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/pix/pagamentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PixPagamentoController {
    
    @Inject
    PixPagamentoService service;
    
    @POST
    public Response processarPagamento(@Valid PixPagamentoRequest request) {
        PixPagamentoResponse response = service.processarPagamento(request);
        return Response.ok(response).build();
    }
} 