package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.endereco.SucoDto;
import tl.gov.mci.lis.models.endereco.Suco;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SucoMapper {
    Suco toEntity(SucoDto sucoDto);

    SucoDto toDto(Suco suco);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Suco partialUpdate(SucoDto sucoDto, @MappingTarget Suco suco);
}