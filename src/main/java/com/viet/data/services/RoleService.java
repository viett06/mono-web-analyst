package com.viet.data.services;

import com.viet.data.dtos.request.role.RoleRequest;
import com.viet.data.dtos.response.RoleResponse;
import com.viet.data.mapper.RoleMapper;
import com.viet.data.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMapper roleMapper;
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse create(RoleRequest roleRequest){
        var role = roleMapper.toRole(roleRequest);
        role=roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getRole(){
        var roles = roleRepository.findAll();
        return roles.stream().map(role -> roleMapper.toRoleResponse(role)).toList();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(String role){
        roleRepository.deleteById(role);
    }
}
