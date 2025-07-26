package br.com.wmscode.common.mapper;

import br.com.wmscode.common.dto.WebhookRequest;
import br.com.wmscode.common.dto.WebhookResponse;
import br.com.wmscode.system.entity.Webhook;
import jakarta.enterprise.context.ApplicationScoped;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T10:58:36-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Ubuntu)"
)
@ApplicationScoped
public class WebhookMapperImpl implements WebhookMapper {

    @Override
    public Webhook toEntity(WebhookRequest request) {
        if ( request == null ) {
            return null;
        }

        Webhook webhook = new Webhook();

        webhook.setUrl( request.getUrl() );
        webhook.setLogin( request.getLogin() );
        webhook.setSenha( request.getSenha() );
        webhook.setDescricao( request.getDescricao() );
        webhook.setBody( request.getBody() );
        webhook.setEvento( request.getEvento() );

        webhook.setStatus( Webhook.StatusWebhook.ATIVO );
        webhook.setDataCriacao( java.time.LocalDateTime.now() );

        return webhook;
    }

    @Override
    public WebhookResponse toResponse(Webhook entity) {
        if ( entity == null ) {
            return null;
        }

        WebhookResponse webhookResponse = new WebhookResponse();

        webhookResponse.setId( entity.id );
        webhookResponse.setUrl( entity.getUrl() );
        webhookResponse.setLogin( entity.getLogin() );
        webhookResponse.setDescricao( entity.getDescricao() );
        webhookResponse.setBody( entity.getBody() );
        webhookResponse.setEvento( entity.getEvento() );
        webhookResponse.setStatus( entity.getStatus() );
        webhookResponse.setDataCriacao( entity.getDataCriacao() );
        webhookResponse.setDataAtualizacao( entity.getDataAtualizacao() );

        return webhookResponse;
    }
}
