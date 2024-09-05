package dev.aniket.Instagram_api.service.impl;

import dev.aniket.Instagram_api.dao.CommentDao;
import dev.aniket.Instagram_api.dao.PostDao;
import dev.aniket.Instagram_api.dto.UserDto;
import dev.aniket.Instagram_api.exception.CommentException;
import dev.aniket.Instagram_api.exception.PostException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Comment;
import dev.aniket.Instagram_api.model.Post;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.service.CommentService;
import dev.aniket.Instagram_api.service.PostService;
import dev.aniket.Instagram_api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;
    private final UserService userService;
    private final PostService postService;
    private final PostDao postDao;

    @Autowired
    public CommentServiceImpl(CommentDao commentDao, UserService userService, PostService postService, PostDao postDao) {
        this.commentDao = commentDao;
        this.userService = userService;
        this.postService = postService;
        this.postDao = postDao;
    }

    @Override
    public Comment createComment(Comment comment, String postId, String userId) throws UserException, PostException {
        User user = userService.findUserById(userId);
        Post post = postService.findPostById(postId);

        // convert to dto
        UserDto userDto = UserDto
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .username(user.getUsername())
                .build();

        comment.setUser(userDto);
        comment.setId(String.valueOf(UUID.randomUUID()));
        comment.setCreatedAt(LocalDateTime.now());

        Comment createdComment = commentDao.save(comment);

        List<Comment> exitingComments = post.getComments();
        exitingComments.add(createdComment);
        post.setComments(exitingComments);

        // update the post with new comment
        postDao.save(post);

        return createdComment;
    }

    @Override
    public Comment findCommentById(String commentId) throws CommentException {
        Optional<Comment> comment = commentDao.findById(commentId);

        if (comment.isEmpty())
            throw new CommentException("Comment is not exist with id: " + commentId);

        return comment.get();
    }

    @Override
    public Comment likeComment(String commentId, String userId) throws CommentException, UserException, PostException {
        User user = userService.findUserById(userId);
        Comment comment = findCommentById(commentId);

        // convert to dto
        UserDto userDto = UserDto
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .username(user.getUsername())
                .build();

        Set<UserDto> existingLikes = comment.getLikedByUsers();
        existingLikes.add(userDto);
        comment.setLikedByUsers(existingLikes);

        return commentDao.save(comment);
    }

    @Override
    public Comment unlikeComment(String commentId, String userId) throws CommentException, UserException, PostException {
        User user = userService.findUserById(userId);
        Comment comment = findCommentById(commentId);

        // convert to dto
        UserDto userDto = UserDto
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .username(user.getUsername())
                .build();

        Set<UserDto> existingLikes = comment.getLikedByUsers();
        existingLikes.remove(userDto);
        comment.setLikedByUsers(existingLikes);

        return commentDao.save(comment);
    }

    @Override
    public void deleteCommentById(String commentId, String userId) throws CommentException {
        Comment comment = findCommentById(commentId);

        if (comment.getUser().getId().equals(userId)) {
            commentDao.deleteById(commentId);
            log.info("Comment successfully deleted!");
            return;
        }
        throw new CommentException("You are not able to delete this comment");
    }
}
