package tl.gov.mci.lis.dtos.mappers;

import org.mapstruct.*;
import tl.gov.mci.lis.dtos.user.UserDto;
import tl.gov.mci.lis.dtos.user.UserLoginDto;
import tl.gov.mci.lis.models.user.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User loginDtooEntity(UserLoginDto userLoginDto);

    UserLoginDto toLoginDto(User user);

    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}