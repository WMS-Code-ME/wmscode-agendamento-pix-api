package br.com.wmscode.common.mapper;

import br.com.wmscode.common.dto.WebhookRequest;
import br.com.wmscode.common.dto.WebhookResponse;
import br.com.wmscode.system.entity.Webhook;
import jakarta.enterprise.context.ApplicationScoped;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-19T15:18:59-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250628-1110, environment: Java 21.0.7 (Eclipse Adoptium)"
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

        webhookResponse.setBody( entity.getBody() );
        webhookResponse.setDataAtualizacao( entity.getDataAtualizacao() );
        webhookResponse.setDataCriacao( entity.getDataCriacao() );
        webhookResponse.setDescricao( entity.getDescricao() );
        webhookResponse.setEvento( entity.getEvento() );
        webhookResponse.setId( entity.id );
        webhookResponse.setLogin( entity.getLogin() );
        webhookResponse.setStatus( entity.getStatus() );
        webhookResponse.setUrl( entity.getUrl() );

        return webhookResponse;
    }
}
