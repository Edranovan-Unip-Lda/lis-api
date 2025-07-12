package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.endereco.PostoAdministrativoDto;
import tl.gov.mci.lis.models.endereco.PostoAdministrativo;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostoAdministrativoMapper {
    PostoAdministrativo toEntity(PostoAdministrativoDto postoAdministrativoDto);

    PostoAdministrativoDto toDto(PostoAdministrativo postoAdministrativo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostoAdministrativo partialUpdate(PostoAdministrativoDto postoAdministrativoDto, @MappingTarget PostoAdministrativo postoAdministrativo);
}