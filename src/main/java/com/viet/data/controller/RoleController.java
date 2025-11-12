package com.viet.data.controller;

import com.viet.data.dtos.request.role.RoleRequest;
import com.viet.data.dtos.response.ApiResponse;
import com.viet.data.dtos.response.RoleResponse;
import com.viet.data.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest){
        RoleResponse role = roleService.create(roleRequest);
        return ApiResponse.<RoleResponse>builder()
                .result(role)
                .build();
    }
    @GetMapping
    public ApiResponse<List<RoleResponse>> getRole(){
        List<RoleResponse> roles = roleService.getRole();
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roles)
                .build();
    }
    @DeleteMapping("/{role}")
    public ApiResponse<Void> deleteRole(@PathVariable String role){
        roleService.deleteRole(role);
        return ApiResponse.<Void>builder().build();
    }

}
