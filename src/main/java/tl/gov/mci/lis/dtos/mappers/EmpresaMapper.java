package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.empresa.GerenteDto;
import tl.gov.mci.lis.dtos.empresa.RepresentanteDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaRequestDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaCreateDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.empresa.Gerente;
import tl.gov.mci.lis.models.empresa.Representante;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {EnderecoMapper.class, PostoAdministrativoMapper.class})
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

    Gerente toEntity(GerenteDto gerenteDto);

    GerenteDto toDto(Gerente gerente);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Gerente partialUpdate(GerenteDto gerenteDto, @MappingTarget Gerente gerente);

    Representante toEntity(RepresentanteDto representanteDto);

    RepresentanteDto toDto(Representante representante);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Representante partialUpdate(RepresentanteDto representanteDto, @MappingTarget Representante representante);
}