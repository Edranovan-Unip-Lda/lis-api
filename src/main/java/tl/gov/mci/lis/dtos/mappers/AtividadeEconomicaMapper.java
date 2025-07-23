package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.AtividadeEconomicaDto;
import tl.gov.mci.lis.models.dadosmestre.AtividadeEconomica;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AtividadeEconomicaMapper {
    AtividadeEconomica toEntity(AtividadeEconomicaDto atividadeEconomicaDto);

    AtividadeEconomicaDto toDto(AtividadeEconomica atividadeEconomica);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AtividadeEconomica partialUpdate(AtividadeEconomicaDto atividadeEconomicaDto, @MappingTarget AtividadeEconomica atividadeEconomica);
}