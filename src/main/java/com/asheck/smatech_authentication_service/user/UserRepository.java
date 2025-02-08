package com.asheck.smatech_authentication_service.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository  <User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndRole(long id, Role role);

    Optional<List<User>> findAllByRole(Role role);
}
