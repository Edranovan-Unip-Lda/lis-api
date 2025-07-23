package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.models.endereco.Endereco;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EnderecoMapper {
    Endereco toEntity(EnderecoDto enderecoDto);

    EnderecoDto toDto(Endereco endereco);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Endereco partialUpdate(EnderecoDto enderecoDto, @MappingTarget Endereco endereco);
}