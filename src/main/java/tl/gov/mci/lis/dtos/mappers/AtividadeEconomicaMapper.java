package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.atividade.ClasseAtividadeDto;
import tl.gov.mci.lis.dtos.atividade.GrupoAtividadeDto;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;
import tl.gov.mci.lis.models.dadosmestre.atividade.GrupoAtividade;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AtividadeEconomicaMapper {

    GrupoAtividade toEntity(GrupoAtividadeDto grupoAtividadeDto);

    GrupoAtividadeDto toDto(GrupoAtividade grupoAtividade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GrupoAtividade partialUpdate(GrupoAtividadeDto grupoAtividadeDto, @MappingTarget GrupoAtividade grupoAtividade);

    ClasseAtividade toEntity(ClasseAtividadeDto classeAtividadeDto);

    ClasseAtividadeDto toDto(ClasseAtividade classeAtividade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ClasseAtividade partialUpdate(ClasseAtividadeDto classeAtividadeDto, @MappingTarget ClasseAtividade classeAtividade);
}