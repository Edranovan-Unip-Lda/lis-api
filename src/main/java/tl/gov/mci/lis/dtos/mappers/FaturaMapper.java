package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.FaturaDto;
import tl.gov.mci.lis.models.pagamento.Fatura;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {AtividadeEconomicaMapper.class})
public interface FaturaMapper {
    Fatura toEntity(FaturaDto faturaDto);

    FaturaDto toDto(Fatura fatura);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Fatura partialUpdate(FaturaDto faturaDto, @MappingTarget Fatura fatura);
}