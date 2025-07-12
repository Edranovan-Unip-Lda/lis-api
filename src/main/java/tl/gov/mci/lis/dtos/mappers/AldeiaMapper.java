package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.endereco.AldeiaDto;
import tl.gov.mci.lis.models.endereco.Aldeia;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AldeiaMapper {
    Aldeia toEntity(AldeiaDto aldeiaDto);

    AldeiaDto toDto(Aldeia aldeia);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Aldeia partialUpdate(AldeiaDto aldeiaDto, @MappingTarget Aldeia aldeia);
}