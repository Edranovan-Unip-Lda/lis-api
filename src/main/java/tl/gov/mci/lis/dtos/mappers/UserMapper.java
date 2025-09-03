package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.user.UserDetailDto;
import tl.gov.mci.lis.dtos.user.UserDto;
import tl.gov.mci.lis.dtos.user.UserLoginDto;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.user.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User loginDtooEntity(UserLoginDto userLoginDto);

    UserLoginDto toLoginDto(User user);

    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDto, @MappingTarget User user);

    User toEntity(UserDetailDto userDetailDto);

    @AfterMapping
    default void linkEmpresa(@MappingTarget User user) {
        Empresa empresa = user.getEmpresa();
        if (empresa != null) {
            empresa.setUtilizador(user);
        }
    }

    UserDetailDto toDto1(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDetailDto userDetailDto, @MappingTarget User user);
}