package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.ClasseAtividadeDto;
import tl.gov.mci.lis.GrupoAtividadeDto;
import tl.gov.mci.lis.dtos.AtividadeEconomicaDto;
import tl.gov.mci.lis.models.dadosmestre.AtividadeEconomica;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;
import tl.gov.mci.lis.models.dadosmestre.atividade.GrupoAtividade;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AtividadeEconomicaMapper {
    AtividadeEconomica toEntity(AtividadeEconomicaDto atividadeEconomicaDto);

    AtividadeEconomicaDto toDto(AtividadeEconomica atividadeEconomica);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AtividadeEconomica partialUpdate(AtividadeEconomicaDto atividadeEconomicaDto, @MappingTarget AtividadeEconomica atividadeEconomica);

    GrupoAtividade toEntity(GrupoAtividadeDto grupoAtividadeDto);

    GrupoAtividadeDto toDto(GrupoAtividade grupoAtividade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GrupoAtividade partialUpdate(GrupoAtividadeDto grupoAtividadeDto, @MappingTarget GrupoAtividade grupoAtividade);

    ClasseAtividade toEntity(ClasseAtividadeDto classeAtividadeDto);

    ClasseAtividadeDto toDto(ClasseAtividade classeAtividade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ClasseAtividade partialUpdate(ClasseAtividadeDto classeAtividadeDto, @MappingTarget ClasseAtividade classeAtividade);
}