package com.viet.data.mapper;

import com.viet.data.dtos.request.role.RoleRequest;
import com.viet.data.dtos.response.RoleResponse;
import com.viet.data.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest roleRequest);
    RoleResponse toRoleResponse(Role role);
}