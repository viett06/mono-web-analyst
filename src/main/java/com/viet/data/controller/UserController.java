package com.viet.data.controller;

import com.viet.data.dtos.request.user.UpdateUser;
import com.viet.data.dtos.request.user.UserRequest;
import com.viet.data.dtos.response.ApiResponse;
import com.viet.data.dtos.response.UserResponse;
import com.viet.data.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest request){
        UserResponse result= userService.createUser(request);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") Long id){

        UserResponse userResponse = userService.getUser(id);
        return ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .build();
    }
    @GetMapping("/myinfor")
    public ApiResponse<UserResponse> getMyInfor(){

        UserResponse userResponse = userService.getMyInfor();
        return ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .build();
    }
    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        List<UserResponse> userResponses = userService.getUsers();
        return ApiResponse.<List<UserResponse>>builder()
                .result(userResponses)
                .build();
    }
    @DeleteMapping("/{userId}")
    public ApiResponse<Object> deleteUser(@PathVariable("userId") Long id){
        userService.deleteUser(id);
        return ApiResponse.<Object>builder()
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUserByAdmin(@PathVariable("userId") Long userId, @RequestBody UpdateUser request) {
        UserResponse userResponse = userService.updateUser(userId, request);
        return ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .build();
    }
    @PatchMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUser request) {
        UserResponse userResponse = userService.updateUser(userId, request);
        return ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .build();
    }

}
