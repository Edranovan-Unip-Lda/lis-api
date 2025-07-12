package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.endereco.MunicipioDto;
import tl.gov.mci.lis.models.endereco.Municipio;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MunicipioMapper {
    Municipio toEntity(MunicipioDto municipioDto);

    MunicipioDto toDto(Municipio municipio);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Municipio partialUpdate(MunicipioDto municipioDto, @MappingTarget Municipio municipio);
}