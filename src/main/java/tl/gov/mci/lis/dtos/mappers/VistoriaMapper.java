package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaDto;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaReqDto;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {EnderecoMapper.class, EnderecoMapper.class, AtividadeEconomicaMapper.class, FaturaMapper.class})
public interface VistoriaMapper {
    PedidoVistoria toEntity(PedidoVistoriaDto pedidoVistoriaDto);

    PedidoVistoriaDto toDto(PedidoVistoria pedidoVistoria);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PedidoVistoria partialUpdate(PedidoVistoriaDto pedidoVistoriaDto, @MappingTarget PedidoVistoria pedidoVistoria);

    PedidoVistoria toEntity(PedidoVistoriaReqDto pedidoVistoriaReqDto);

    PedidoVistoriaReqDto toDto1(PedidoVistoria pedidoVistoria);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PedidoVistoria partialUpdate(PedidoVistoriaReqDto pedidoVistoriaReqDto, @MappingTarget PedidoVistoria pedidoVistoria);
}