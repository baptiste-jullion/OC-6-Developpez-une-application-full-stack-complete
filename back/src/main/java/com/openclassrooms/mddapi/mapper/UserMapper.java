package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.auth.request.RegisterRequest;
import com.openclassrooms.mddapi.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    User toEntity(RegisterRequest request);
}
