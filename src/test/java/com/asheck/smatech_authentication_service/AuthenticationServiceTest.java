package com.asheck.smatech_authentication_service;

import com.asheck.smatech_authentication_service.auth.AuthenticationRequest;
import com.asheck.smatech_authentication_service.auth.AuthenticationResponse;
import com.asheck.smatech_authentication_service.auth.AuthenticationService;
import com.asheck.smatech_authentication_service.auth.RegisterRequest;
import com.asheck.smatech_authentication_service.config.JwtService;
import com.asheck.smatech_authentication_service.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;
    

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("John", "Doe", "john.doe@example.com", "password", "123456789", "123 Street");
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    void testRegister_NewUser_ShouldReturnCreatedResponse() {
        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockedJwtToken");

        ResponseEntity<?> response = authenticationService.register(registerRequest, Role.CUSTOMER);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testRegister_ExistingUser_ShouldReturnConflictResponse() {
        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authenticationService.register(registerRequest, Role.CUSTOMER);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User with email " + registerRequest.email() + " already exists", response.getBody());
    }

    @Test
    void testAuthenticate_ValidCredentials_ShouldReturnToken() {
        AuthenticationRequest authRequest = new AuthenticationRequest("john.doe@example.com", "password");
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockedJwtToken");

        Object response = authenticationService.authenticate(authRequest);

        assertTrue(response instanceof AuthenticationResponse);
        assertEquals("mockedJwtToken", ((AuthenticationResponse) response).getToken());
    }

    @Test
    void testAuthenticate_InvalidCredentials_ShouldReturnUnauthorizedResponse() {
        AuthenticationRequest authRequest = new AuthenticationRequest("wrong@example.com", "password");
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.empty());

        Object response = authenticationService.authenticate(authRequest);

        assertTrue(response instanceof ResponseEntity);
        assertEquals(HttpStatus.UNAUTHORIZED, ((ResponseEntity<?>) response).getStatusCode());
    }

    @Test
    void testGetUser_ExistingUser_ShouldReturnUser() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        User foundUser = authenticationService.getUser("john.doe@example.com");

        assertNotNull(foundUser);
        assertEquals("john.doe@example.com", foundUser.getEmail());
    }

    @Test
    void testGetUserById_UserExists_ShouldReturnUser() {
        when(userRepository.findByIdAndRole(1L, Role.CUSTOMER)).thenReturn(Optional.of(user));

        User foundUser = authenticationService.getUserById(1L, Role.CUSTOMER);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
    }

    @Test
    void testGetUserById_UserNotFound_ShouldThrowException() {
        when(userRepository.findByIdAndRole(1L, Role.CUSTOMER)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> authenticationService.getUserById(1L, Role.CUSTOMER));
    }

    @Test
    void testGetUsers_UsersExist_ShouldReturnList() {
        when(userRepository.findAllByRole(Role.CUSTOMER)).thenReturn(Optional.of(List.of(user)));

        List<User> users = authenticationService.getUsers(Role.CUSTOMER);

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void testGetUsers_UsersNotFound_ShouldThrowException() {
        when(userRepository.findAllByRole(Role.CUSTOMER)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> authenticationService.getUsers(Role.CUSTOMER));
    }

    @Test
    void testUpdateUser_ExistingUser_ShouldReturnUpdatedUser() {
        UpdateUserRequest updateRequest = new UpdateUserRequest("Jane", "Doe", "test@mail.com","987654321", "456 Avenue");
        when(userRepository.findByIdAndRole(1L, Role.CUSTOMER)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = authenticationService.updateUser(1L, updateRequest);

        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
    }

    @Test
    void testUpdateUser_UserNotFound_ShouldThrowException() {
        UpdateUserRequest updateRequest = new UpdateUserRequest("Jane", "Doe", "test@mail.com","987654321", "456 Avenue");
        when(userRepository.findByIdAndRole(1L, Role.CUSTOMER)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> authenticationService.updateUser(1L, updateRequest));
    }
}

