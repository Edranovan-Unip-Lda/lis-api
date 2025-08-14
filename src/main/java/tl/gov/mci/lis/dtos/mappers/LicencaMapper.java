package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.licenca.PedidoLicencaAtividadeDto;
import tl.gov.mci.lis.dtos.licenca.PedidoLicencaAtividadeReqsDto;
import tl.gov.mci.lis.dtos.licenca.PessoaDto;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;
import tl.gov.mci.lis.models.atividade.Pessoa;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {EnderecoMapper.class, AtividadeEconomicaMapper.class, LicencaMapper.class, LicencaMapper.class, FaturaMapper.class})
public interface LicencaMapper {
    Pessoa toEntity(PessoaDto pessoaDto);

    PessoaDto toDto(Pessoa pessoa);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Pessoa partialUpdate(PessoaDto pessoaDto, @MappingTarget Pessoa pessoa);

    PedidoLicencaAtividade toEntity(PedidoLicencaAtividadeDto pedidoLicencaAtividadeDto);

    PedidoLicencaAtividadeDto toDto(PedidoLicencaAtividade pedidoLicencaAtividade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PedidoLicencaAtividade partialUpdate(PedidoLicencaAtividadeDto pedidoLicencaAtividadeDto, @MappingTarget PedidoLicencaAtividade pedidoLicencaAtividade);

    PedidoLicencaAtividade toEntity(PedidoLicencaAtividadeReqsDto pedidoLicencaAtividadeReqsDto);

    PedidoLicencaAtividadeReqsDto toReqsDto(PedidoLicencaAtividade pedidoLicencaAtividade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PedidoLicencaAtividade partialUpdate(PedidoLicencaAtividadeReqsDto pedidoLicencaAtividadeReqsDto, @MappingTarget PedidoLicencaAtividade pedidoLicencaAtividade);
}