package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.pagamento.Fatura;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {FaturaMapper.class})
public interface PedidoInscricaoCadastroMapper {
    PedidoInscricaoCadastro toEntity(PedidoInscricaoCadastroDto pedidoInscricaoCadastroDto);

    @AfterMapping
    default void linkFatura(@MappingTarget PedidoInscricaoCadastro pedidoInscricaoCadastro) {
        Fatura fatura = pedidoInscricaoCadastro.getFatura();
        if (fatura != null) {
            fatura.setPedidoInscricaoCadastro(pedidoInscricaoCadastro);
        }
    }

    PedidoInscricaoCadastroDto toDto(PedidoInscricaoCadastro pedidoInscricaoCadastro);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PedidoInscricaoCadastro partialUpdate(PedidoInscricaoCadastroDto pedidoInscricaoCadastroDto, @MappingTarget PedidoInscricaoCadastro pedidoInscricaoCadastro);
}