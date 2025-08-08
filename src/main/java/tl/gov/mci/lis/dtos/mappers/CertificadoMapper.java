package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroDto;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {EnderecoMapper.class})
public interface CertificadoMapper {
    CertificadoInscricaoCadastro toEntity(CertificadoInscricaoCadastroDto certificadoInscricaoCadastroDto);

    CertificadoInscricaoCadastroDto toDto(CertificadoInscricaoCadastro certificadoInscricaoCadastro);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CertificadoInscricaoCadastro partialUpdate(CertificadoInscricaoCadastroDto certificadoInscricaoCadastroDto, @MappingTarget CertificadoInscricaoCadastro certificadoInscricaoCadastro);
}