// src/main/java/com/freighthub/core/repository/UserRepository.java

package com.freighthub.core.repository;

import com.freighthub.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u")
    List<User> findAllUsers();
}
