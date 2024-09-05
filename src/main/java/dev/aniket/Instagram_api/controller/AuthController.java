package dev.aniket.Instagram_api.controller;

import dev.aniket.Instagram_api.security.service.JwtService;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> registerUser(@RequestBody User user) throws UserException {
        User createdUser = userService.registerUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @GetMapping("/demo")
    public String getDemo() {
        return "secure me!";
    }

    @PostMapping("/signin")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) throws UserException {
        Authentication authentication
                = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(username);

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(token);
        }

        throw new BadCredentialsException("Invalid username or password!");
    }
}
