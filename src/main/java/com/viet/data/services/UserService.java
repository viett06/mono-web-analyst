package com.viet.data.services;

import com.viet.data.constant.PredefinedRole;
import com.viet.data.dtos.request.user.UpdateUser;
import com.viet.data.dtos.request.user.UserRequest;
import com.viet.data.dtos.response.UserResponse;
import com.viet.data.entity.Role;
import com.viet.data.entity.User;
import com.viet.data.exception.AppException;
import com.viet.data.exception.ErrorCode;
import com.viet.data.mapper.UserMapper;
import com.viet.data.repository.RoleRepository;
import com.viet.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.viet.data.exception.ErrorCode.USERNAME_ALREADY_EXISTS;
import static com.viet.data.exception.ErrorCode.USER_NOT_EXISTED;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Transactional
    public UserResponse createUser(UserRequest request){
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent((role)->{roles.add(role);});
        user.setRoles(roles);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(){
        log.info("in method get users");
        List<User> user = userRepository.findAll();
        return user.stream().map(userMapper::toUserResponse).toList();
    }
    @PostAuthorize("returnObject.name == authentication.name")
    public UserResponse getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new AppException(USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public UserResponse getMyInfor(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserByAdmin(Long userId, UpdateUser userUpdateRequest){
        User user = userRepository.findById(userId).orElseThrow(()-> new AppException(USER_NOT_EXISTED));
        if (userRepository.findByUsername(userUpdateRequest.getUsername()).isPresent()) {
            throw new AppException(USERNAME_ALREADY_EXISTS);
        }

        userMapper.updateUser(user,userUpdateRequest);
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        var roles = roleRepository.findAllById(userUpdateRequest.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUser userUpdateRequest){
        User user = userRepository.findById(userId).orElseThrow(()-> new AppException(USER_NOT_EXISTED));
        if (userRepository.findByUsername(userUpdateRequest.getUsername()).isPresent()) {
            throw new AppException(USERNAME_ALREADY_EXISTS);
        }
        userMapper.updateUser(user,userUpdateRequest);
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }
    public User findUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(()-> new AppException(USER_NOT_EXISTED));
    }
}