package com.viet.data.mapper;

import com.viet.data.dtos.request.user.UpdateUser;
import com.viet.data.dtos.request.user.UserRequest;
import com.viet.data.dtos.response.UserResponse;
import com.viet.data.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserRequest request);
    @Mapping(source = "roles", target = "roleResponses")
    UserResponse toUserResponse(User user);
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUser request);
}