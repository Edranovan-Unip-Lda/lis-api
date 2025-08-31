package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.vistoria.*;
import tl.gov.mci.lis.models.vistoria.AutoVistoria;
import tl.gov.mci.lis.models.vistoria.Participante;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;
import tl.gov.mci.lis.models.vistoria.Requerente;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {EnderecoMapper.class, EnderecoMapper.class, AtividadeEconomicaMapper.class, FaturaMapper.class, PostoAdministrativoMapper.class, VistoriaMapper.class, VistoriaMapper.class, UserMapper.class})
public interface VistoriaMapper {
    PedidoVistoria toEntity(PedidoVistoriaDto pedidoVistoriaDto);

    PedidoVistoriaDto toDto(PedidoVistoria pedidoVistoria);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PedidoVistoria partialUpdate(PedidoVistoriaDto pedidoVistoriaDto, @MappingTarget PedidoVistoria pedidoVistoria);

    PedidoVistoria toEntity(PedidoVistoriaReqDto pedidoVistoriaReqDto);

    PedidoVistoriaReqDto toDto1(PedidoVistoria pedidoVistoria);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PedidoVistoria partialUpdate(PedidoVistoriaReqDto pedidoVistoriaReqDto, @MappingTarget PedidoVistoria pedidoVistoria);

    Requerente toEntity(RequerenteDto requerenteDto);

    RequerenteDto toDto(Requerente requerente);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Requerente partialUpdate(RequerenteDto requerenteDto, @MappingTarget Requerente requerente);

    Participante toEntity(ParticipanteDto participanteDto);

    ParticipanteDto toDto(Participante participante);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Participante partialUpdate(ParticipanteDto participanteDto, @MappingTarget Participante participante);

    AutoVistoria toEntity(AutoVistoriaDto autoVistoriaDto);

    @AfterMapping
    default void linkMembrosEquipaVistoria(@MappingTarget AutoVistoria autoVistoria) {
        autoVistoria.getMembrosEquipaVistoria().forEach(membrosEquipaVistoria -> membrosEquipaVistoria.setAutoVistoria(autoVistoria));
    }

    @AfterMapping
    default void linkDocumentos(@MappingTarget AutoVistoria autoVistoria) {
        autoVistoria.getDocumentos().forEach(documento -> documento.setAutoVistoria(autoVistoria));
    }

    AutoVistoriaDto toDto(AutoVistoria autoVistoria);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AutoVistoria partialUpdate(AutoVistoriaDto autoVistoriaDto, @MappingTarget AutoVistoria autoVistoria);
}