package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.atividade.CertificadoLicencaAtividadeDto;
import tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroDto;
import tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {EnderecoMapper.class})
public interface CertificadoMapper {
    CertificadoInscricaoCadastro toEntity(CertificadoInscricaoCadastroDto certificadoInscricaoCadastroDto);

    CertificadoInscricaoCadastroDto toDto(CertificadoInscricaoCadastro certificadoInscricaoCadastro);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CertificadoInscricaoCadastro partialUpdate(CertificadoInscricaoCadastroDto certificadoInscricaoCadastroDto, @MappingTarget CertificadoInscricaoCadastro certificadoInscricaoCadastro);

    CertificadoLicencaAtividade toEntity(CertificadoLicencaAtividadeDto certificadoLicencaAtividadeDto);

    CertificadoLicencaAtividadeDto toDto(CertificadoLicencaAtividade certificadoLicencaAtividade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CertificadoLicencaAtividade partialUpdate(CertificadoLicencaAtividadeDto certificadoLicencaAtividadeDto, @MappingTarget CertificadoLicencaAtividade certificadoLicencaAtividade);
}