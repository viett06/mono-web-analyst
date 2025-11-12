package com.viet.data.services;

import com.viet.data.entity.User;
import com.viet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(String username, String email, String password) {
        User user = new User(username, email, password);
        return userRepository.save(user);
    }

    public boolean userExists(String username, String email) {
        return userRepository.existsByUsername(username) || userRepository.existsByEmail(email);
    }
}