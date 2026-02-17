package com.linea_desk.rest_linea.common.service;


import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.common.dto.LoginUserDto;
import com.linea_desk.rest_linea.common.dto.RegisterUserDto;
import com.linea_desk.rest_linea.User.UserRepository;
import com.linea_desk.rest_linea.User.UserResponseDto;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// @Log4j2
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private static final Logger log = LogManager.getLogger(AuthenticationService.class);

    public AuthenticationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager
    ){
        this.userRepository = userRepository;
        this.passwordEncoder= passwordEncoder;
        this.authenticationManager= authenticationManager;
    }

    public Optional<UserResponseDto> signup(RegisterUserDto input) {

        try {
            if (userRepository.findByEmail(input.getEmail()).isPresent()) {
                log.error("User with email " + input.getEmail() + " already exists");
                return Optional.empty();
            }

            String email = input.getEmail();
            String username = input.getUsername();
            String password = passwordEncoder.encode(input.getPassword());

            User user = new User()
                                .setEmail(email)
                                .setUsername(username)
                                .setPassword(password);

            log.error("After the creation of the user variable");
            userRepository.save(user);
            return Optional.of(new UserResponseDto(email, username, password));
        }
        catch (Exception exception) {
            log.error("exception was caught in the authenticationService " + exception.getMessage());
            log.error("Stack trace: " + exception);
            return Optional.empty();
        }
    }
    
    public Optional<User> authenticate(LoginUserDto input) {
        try {

            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    input.getEmail(), 
                    input.getPassword()
                )
            );
            
            return Optional.of(userRepository.findByEmail(input.getEmail())
                .orElseThrow());
        }catch(Exception exception) {
            return Optional.empty();
        }
    }
}
