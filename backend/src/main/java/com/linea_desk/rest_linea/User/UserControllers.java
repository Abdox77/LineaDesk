package com.linea_desk.rest_linea.User;


import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.common.service.AuthenticationService;
import com.linea_desk.rest_linea.common.dto.ApiResponse;
// import com.linea_desk.rest_linea.common.dto.ExceptionResponse;
import com.linea_desk.rest_linea.common.dto.RegisterUserDto;
import com.linea_desk.rest_linea.common.dto.LoginUserDto;
import com.linea_desk.rest_linea.common.dto.LoginResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;


@Log4j2
@RestController
// @RequestMapping("/auth")
public class UserControllers {
    private final JwtService jwtService;
    private final UserService userService;
    private static final Logger log = LogManager.getLogger(UserControllers.class);
    private final AuthenticationService authenticationService;

    public UserControllers(
        JwtService jwtService,
        UserService userService,
        AuthenticationService authenticationService
    ) {
        this.jwtService = jwtService; 
        this.userService = userService;
        this.authenticationService = authenticationService;

    }

    @RequestMapping(path="/auth/login", method=POST)
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
                                            user.getId(), 
                                            user.getUsername(),
                                            user.getDisplayName()
                                        );

            response = new ApiResponse<>(
                    true,
                    "User registered successfully", 
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

    @RequestMapping(path="/auth/signup", method=POST)
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

    
    @GetMapping("/users")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
    

}
