package br.com.wmscode.common.mapper;

import br.com.wmscode.common.dto.PixAgendamentoRequest;
import br.com.wmscode.common.dto.PixAgendamentoResponse;
import br.com.wmscode.system.entity.PixAgendamento;
import jakarta.enterprise.context.ApplicationScoped;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T10:30:46-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250628-1110, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@ApplicationScoped
public class PixAgendamentoMapperImpl implements PixAgendamentoMapper {

    @Override
    public PixAgendamento toEntity(PixAgendamentoRequest request) {
        if ( request == null ) {
            return null;
        }

        PixAgendamento pixAgendamento = new PixAgendamento();

        pixAgendamento.setChavePix( request.getChavePix() );
        pixAgendamento.setNomeBeneficiario( request.getNomeBeneficiario() );
        pixAgendamento.setValor( request.getValor() );
        pixAgendamento.setDescricao( request.getDescricao() );
        pixAgendamento.setDataAgendamento( request.getDataAgendamento() );
        pixAgendamento.setObservacao( request.getObservacao() );

        pixAgendamento.setStatus( PixAgendamento.StatusAgendamento.AGENDADO );
        pixAgendamento.setDataCriacao( java.time.LocalDateTime.now() );

        return pixAgendamento;
    }

    @Override
    public PixAgendamentoResponse toResponse(PixAgendamento entity) {
        if ( entity == null ) {
            return null;
        }

        PixAgendamentoResponse pixAgendamentoResponse = new PixAgendamentoResponse();

        pixAgendamentoResponse.setChavePix( entity.getChavePix() );
        pixAgendamentoResponse.setCodigoTransacao( entity.getCodigoTransacao() );
        pixAgendamentoResponse.setDataAgendamento( entity.getDataAgendamento() );
        pixAgendamentoResponse.setDataCriacao( entity.getDataCriacao() );
        pixAgendamentoResponse.setDataProcessamento( entity.getDataProcessamento() );
        pixAgendamentoResponse.setDescricao( entity.getDescricao() );
        pixAgendamentoResponse.setId( entity.getId() );
        pixAgendamentoResponse.setNomeBeneficiario( entity.getNomeBeneficiario() );
        pixAgendamentoResponse.setObservacao( entity.getObservacao() );
        if ( entity.getStatus() != null ) {
            pixAgendamentoResponse.setStatus( entity.getStatus().name() );
        }
        pixAgendamentoResponse.setValor( entity.getValor() );

        return pixAgendamentoResponse;
    }
}
