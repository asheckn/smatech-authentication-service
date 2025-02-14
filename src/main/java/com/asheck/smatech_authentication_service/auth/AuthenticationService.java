package com.asheck.smatech_authentication_service.auth;

import com.asheck.smatech_authentication_service.config.JwtService;
import com.asheck.smatech_authentication_service.user.Role;
import com.asheck.smatech_authentication_service.user.UpdateUserRequest;
import com.asheck.smatech_authentication_service.user.User;
import com.asheck.smatech_authentication_service.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?>  register(RegisterRequest request, Role role) {

        Optional<User> existingUser = repository.findByEmail(request.email());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email " + request.email() + " already exists");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .isActive(true)
                .isDeleted(false)
                .password(passwordEncoder.encode(request.password()))
                .role(role)
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

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
        }



    }

    public User getUser(String userEmail) {
        return repository.findByEmail(userEmail)
               .orElseThrow();
    }

    public User getUserById(long userId, Role role) {
        return repository.findByIdAndRole(userId, role)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );
    }

    public User getUserByToken(String token, Role role) {

        final String userEmail = jwtService.extractUsername(token);

        return repository.findByEmail(userEmail)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
                );

    }

    public List<User> getUsers(Role role) {
        //get all users with role customer
        return repository.findAllByRole(role).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Users not found")
        );
    }

    public User updateUser(Long id, UpdateUserRequest request) {
        var user = repository.findByIdAndRole(id, Role.CUSTOMER).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        if(request.firstName() != null){
            user.setFirstName(request.firstName());
        }

        if(request.lastName() != null){
            user.setLastName(request.lastName());
        }

        if(request.phoneNumber() != null){
            user.setPhoneNumber(request.phoneNumber());
        }

        if(request.address() != null){
            user.setAddress(request.address());
        }

        return repository.save(user);
    }
}
