package com.linea_desk.rest_linea.User;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.linea_desk.rest_linea.common.exceptions.DuplicateResourceException;



@Service
public class UserServices {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

    public UserServices(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);
        return users;
    }

    public User updateProfile(User user, UpdateProfileDto dto) {
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            Optional<User> existing = userRepository.findByUsername(dto.getUsername());
            if (existing.isPresent() && !existing.get().getUserId().equals(user.getUserId())) {
                throw new DuplicateResourceException("User", "username", dto.getUsername());
            }
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            Optional<User> existing = userRepository.findByEmail(dto.getEmail());
            if (existing.isPresent() && !existing.get().getUserId().equals(user.getUserId())) {
                throw new DuplicateResourceException("User", "email", dto.getEmail());
            }
            user.setEmail(dto.getEmail());
        }
        return userRepository.save(user);
    }

    public void changePassword(User user, ChangePasswordDto dto) {
        if (user.getPassword() != null && !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
