package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.empresa.EmpresaRequestDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaCreateDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.models.empresa.Empresa;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmpresaMapper {
    Empresa toEntity(EmpresaDto empresaDto);

    EmpresaDto toDto(Empresa empresa);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Empresa partialUpdate(EmpresaDto empresaDto, @MappingTarget Empresa empresa);

    Empresa toEntity(EmpresaCreateDto empresaCreateDto);

    EmpresaCreateDto toDto1(Empresa empresa);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Empresa partialUpdate(EmpresaCreateDto empresaCreateDto, @MappingTarget Empresa empresa);

    Empresa toEntity(EmpresaRequestDto empresaRequestDto);

    @AfterMapping
    default void linkAcionistas(@MappingTarget Empresa empresa) {
        empresa.getAcionistas().forEach(acionista -> acionista.setEmpresa(empresa));
    }

    EmpresaRequestDto toDto2(Empresa empresa);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Empresa partialUpdate(EmpresaRequestDto empresaRequestDto, @MappingTarget Empresa empresa);
}