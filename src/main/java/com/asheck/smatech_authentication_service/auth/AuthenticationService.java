package com.asheck.smatech_authentication_service.auth;

import com.asheck.smatech_authentication_service.config.JwtService;
import com.asheck.smatech_authentication_service.user.Role;
import com.asheck.smatech_authentication_service.user.User;
import com.asheck.smatech_authentication_service.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?>  register(RegisterRequest request) {

        Optional<User> existingUser = repository.findByEmail(request.email());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email " + request.email() + " already exists");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.CUSTOMER)
                .build();

        repository.save(user);




        var jwtToken = jwtService.generateToken(user);
       return  ResponseEntity.status(HttpStatus.CREATED).body(AuthenticationResponse.builder()
                .success(true)
                .token(jwtToken)
                .build());

    }

    public Object authenticate(AuthenticationRequest request) {

        var user = repository.findByEmail(request.getEmail());


        if (user.isPresent()) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var jwtToken = jwtService.generateToken(user.get());

            return AuthenticationResponse.builder()
                    .success(true)
                    .token(jwtToken)
                    .data(user)
                    .build();
        }else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }



    }

    public User getUser(String userEmail) {
        return repository.findByEmail(userEmail)
               .orElseThrow();
    }
}
