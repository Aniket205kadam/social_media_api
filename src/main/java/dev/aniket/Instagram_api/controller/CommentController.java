package dev.aniket.Instagram_api.controller;

import dev.aniket.Instagram_api.exception.CommentException;
import dev.aniket.Instagram_api.exception.PostException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Comment;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.response.MessageResponse;
import dev.aniket.Instagram_api.service.CommentService;
import dev.aniket.Instagram_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/create/posts/{postId}")
    public ResponseEntity<Comment> createComment(
            @PathVariable String postId,
            @RequestBody Comment comment,
            @RequestHeader("Authorization") String token
    ) throws UserException, PostException {
        System.out.println("Enter to the create comment controller");
        User user = userService.findUserProfile(token);

        System.out.println("post1");
        Comment createComment = commentService.createComment(comment, postId, user.getId());
        System.out.println("Post2");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/api/v1/comments/" + createComment.getId())
                .body(createComment);
    }

    @PutMapping("/like/{commentId}")
    public ResponseEntity<Comment> likeComment (
         @PathVariable String commentId,
         @RequestHeader("Authorization") String token
    ) throws UserException, PostException, CommentException {
        User user = userService.findUserProfile(token);

        Comment comment = commentService.likeComment(commentId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(comment);
    }

    @PutMapping("/unlike/{commentId}")
    public ResponseEntity<Comment> unlikeComment (
            @PathVariable String commentId,
            @RequestHeader("Authorization") String token
    ) throws UserException, PostException, CommentException {
        User user = userService.findUserProfile(token);

        Comment comment = commentService.unlikeComment(commentId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(comment);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(
            @RequestHeader("Authorization") String token,
            @PathVariable String commentId
    ) throws UserException, CommentException {
        User user = userService.findUserById(token);
        commentService.deleteCommentById(commentId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MessageResponse.builder().message("Successfully: deleted comment with id: " + commentId).build());
    }
}
