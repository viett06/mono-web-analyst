package com.viet.data.mapper;

import com.viet.data.dtos.request.user.UpdateUser;
import com.viet.data.dtos.request.user.UserRequest;
import com.viet.data.dtos.response.UserResponse;
import com.viet.data.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "datasets", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUser(UserRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "datasets", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUser request);
}