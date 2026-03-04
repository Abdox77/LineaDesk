package com.linea_desk.rest_linea.User;

import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.common.service.AuthenticationService;
import com.linea_desk.rest_linea.common.dto.ApiResponse;
import com.linea_desk.rest_linea.common.dto.RegisterUserDto;
import com.linea_desk.rest_linea.common.dto.LoginUserDto;
import com.linea_desk.rest_linea.common.dto.LoginResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;


@RestController
public class UserControllers {
    private final JwtService jwtService;
    private final UserServices userService;
    private static final Logger log = LogManager.getLogger(UserControllers.class);
    private final AuthenticationService authenticationService;

    public UserControllers(
        JwtService jwtService,
        UserServices userService,
        AuthenticationService authenticationService
    ) {
        this.jwtService = jwtService; 
        this.userService = userService;
        this.authenticationService = authenticationService;

    }

    @PostMapping(path="/auth/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginUserDto loginUserDto)
    {
        ApiResponse<?> response;
   
        try {
            Optional<User> authenticatedUser = authenticationService.authenticate(loginUserDto); 
            if (authenticatedUser.isEmpty()) {
                response = new ApiResponse<>(
                                false,
                                "Invalid email or password"
                            );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = authenticatedUser.get();
            String jwtToken = jwtService.generateToken(user);
            if (jwtToken == null) {
                throw new Exception("Error generating the jwt token");
            }

            LoginResponse loginResponse = new LoginResponse(
                                            jwtToken,
                                            user.getUserId(),
                                            user.getUsername(),
                                            user.getDisplayName()
                                        );

            response = new ApiResponse<>(
                    true,
                    "Login successful",
                            loginResponse
            );
        }
        catch (Exception e) {
            response = new ApiResponse<>(
                            false, 
                            "Internal Server Error"
                            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path="/auth/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody RegisterUserDto registerUserDto)
    {
        try {
            ApiResponse<Optional<UserResponseDto>> response;
            Optional<UserResponseDto> registeredUser;
            
            log.debug("registerUserDto content : " + registerUserDto.getUsername() + " " + registerUserDto.getEmail() + " " + registerUserDto.getPassword());
            
            registeredUser = authenticationService.signup(registerUserDto);
            response = new ApiResponse<>(
                    true,
                    "User registered successfully",
                    registeredUser
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (Exception e) {
            log.error("Exception was thrown in {RegisterController}" + e.getMessage());
            ApiResponse<Void> response = new ApiResponse<>(
                    false,
                    "Internal Server Error"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    


}
