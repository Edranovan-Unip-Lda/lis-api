package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.models.aplicante.Aplicante;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AplicanteMapper {
    Aplicante toEntity(AplicanteDto aplicanteDto);

    @AfterMapping
    default void linkListaHistoricoEstadoAplicante(@MappingTarget Aplicante aplicante) {
        aplicante.getListaHistoricoEstadoAplicante().forEach(listaHistoricoEstadoAplicante -> listaHistoricoEstadoAplicante.setAplicante(aplicante));
    }

    AplicanteDto toDto(Aplicante aplicante);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Aplicante partialUpdate(AplicanteDto aplicanteDto, @MappingTarget Aplicante aplicante);
}