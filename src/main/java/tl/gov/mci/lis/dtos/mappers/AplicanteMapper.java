package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.aplicante.*;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AplicanteMapper {
    Aplicante toEntity(AplicanteDto aplicanteDto);

    AplicanteDto toDto(Aplicante aplicante);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Aplicante partialUpdate(AplicanteDto aplicanteDto, @MappingTarget Aplicante aplicante);

    Aplicante toEntity(AplicantePageDto aplicantePageDto);

    AplicantePageDto toDto1(Aplicante aplicante);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Aplicante partialUpdate(AplicantePageDto aplicantePageDto, @MappingTarget Aplicante aplicante);

    Aplicante toEntity(AplicanteRequestDto aplicanteRequestDto);

    AplicanteRequestDto toDto2(Aplicante aplicante);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Aplicante partialUpdate(AplicanteRequestDto aplicanteRequestDto, @MappingTarget Aplicante aplicante);

    HistoricoEstadoAplicante toEntity(HistoricoEstadoAplicanteDto historicoEstadoAplicanteDto);

    HistoricoEstadoAplicanteDto toDto(HistoricoEstadoAplicante historicoEstadoAplicante);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    HistoricoEstadoAplicante partialUpdate(HistoricoEstadoAplicanteDto historicoEstadoAplicanteDto, @MappingTarget HistoricoEstadoAplicante historicoEstadoAplicante);

    Aplicante toEntity(AplicanteReqsDto aplicanteReqsDto);

    AplicanteReqsDto toDto3(Aplicante aplicante);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Aplicante partialUpdate(AplicanteReqsDto aplicanteReqsDto, @MappingTarget Aplicante aplicante);
}