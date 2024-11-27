package com.network_applications.source_safe.Repository;

import com.network_applications.source_safe.Model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByFullNameContainingIgnoreCase(String fullName);
}
