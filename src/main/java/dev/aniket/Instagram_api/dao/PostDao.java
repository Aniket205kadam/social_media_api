package dev.aniket.Instagram_api.dao;

import dev.aniket.Instagram_api.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostDao extends JpaRepository<Post, String> {
    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 ORDER BY p.createdAt DESC")
    List<Post> findPostByUserId(String userId);

    @Query("SELECT p FROM Post p WHERE p.user.id IN ?1 ORDER BY p.createdAt DESC")
    List<Post> findAllPostByUserIdsSortedByDateDescendingOrder(List<String> usersIds);
}
