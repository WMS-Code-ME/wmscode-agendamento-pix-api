package br.com.wmscode.common.mapper;

import br.com.wmscode.common.dto.PixAgendamentoRequest;
import br.com.wmscode.common.dto.PixAgendamentoResponse;
import br.com.wmscode.system.entity.PixAgendamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface PixAgendamentoMapper {
    
    PixAgendamentoMapper INSTANCE = Mappers.getMapper(PixAgendamentoMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "AGENDADO")
    @Mapping(target = "dataCriacao", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "dataProcessamento", ignore = true)
    @Mapping(target = "codigoTransacao", ignore = true)
    PixAgendamento toEntity(PixAgendamentoRequest request);
    
    PixAgendamentoResponse toResponse(PixAgendamento entity);
} 