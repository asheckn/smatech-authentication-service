package com.asheck.smatech_authentication_service.auth;
import com.asheck.smatech_authentication_service.user.Role;
import com.asheck.smatech_authentication_service.user.UpdateUserRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "authorization")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    //Register a customer
    @PostMapping("/register")
    public ResponseEntity<?> register(
          @RequestBody RegisterRequest request
    ){
        return service.register(request, Role.CUSTOMER);
    }

    //Register an admin
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(
            @RequestBody RegisterRequest request
    ){
        return service.register(request, Role.ADMIN);
    }

    //Login or authenticate a user both customer and admin
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/get-customers")
    public ResponseEntity<?> getCustomers(){
        return ResponseEntity.ok(service.getUsers(Role.CUSTOMER));
    }

    @GetMapping("/get-admins")
    public ResponseEntity<?> getAdmins(){
        return ResponseEntity.ok(service.getUsers(Role.ADMIN));
    }

    @GetMapping("/get-user/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        return ResponseEntity.ok(service.getUserById(id, Role.CUSTOMER));
    }

    @GetMapping("/get-Admin/{id}")
    public ResponseEntity<?> getAdmin(@PathVariable Long id){
        return ResponseEntity.ok(service.getUserById(id, Role.ADMIN));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles(){
        return ResponseEntity.ok(Role.values());
    }

    @PatchMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ){
        return ResponseEntity.ok(service.updateUser(id, request));
    }

}
