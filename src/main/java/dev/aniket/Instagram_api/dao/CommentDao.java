package dev.aniket.Instagram_api.dao;

import dev.aniket.Instagram_api.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentDao extends JpaRepository<Comment, String> {

}
