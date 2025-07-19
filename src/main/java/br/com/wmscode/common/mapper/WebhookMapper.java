package br.com.wmscode.common.mapper;

import br.com.wmscode.common.dto.WebhookRequest;
import br.com.wmscode.common.dto.WebhookResponse;
import br.com.wmscode.system.entity.Webhook;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface WebhookMapper {
    
    WebhookMapper INSTANCE = Mappers.getMapper(WebhookMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ATIVO")
    @Mapping(target = "dataCriacao", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "dataAtualizacao", ignore = true)
    Webhook toEntity(WebhookRequest request);
    
    WebhookResponse toResponse(Webhook entity);
} 