package dev.aniket.Instagram_api.dao;

import dev.aniket.Instagram_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.id IN :users")
    List<User> findAllUsersByUserIds(@Param("users") List<String> userIds);

    @Query("SELECT DISTINCT u FROM User u WHERE u.username LIKE %:query% OR u.email LIKE %:query% OR u.mobile LIKE %:query%")
    List<User> findByQuery(@Param("query") String query);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
