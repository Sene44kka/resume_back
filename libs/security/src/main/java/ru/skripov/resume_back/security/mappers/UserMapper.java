package ru.skripov.resume_back.security.mappers;

import org.mapstruct.*;
import ru.skripov.resume_back.security.dto.UserDto;
import ru.skripov.resume_back.security.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Named("toDto")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "middleName", source = "middleName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    UserDto toDto(User user);

    @Named("toEntity")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "middleName", source = "middleName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    User toEntity(UserDto userDto);
}
