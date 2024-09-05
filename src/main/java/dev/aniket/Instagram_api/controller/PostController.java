package dev.aniket.Instagram_api.controller;

import dev.aniket.Instagram_api.exception.PostException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Post;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.response.MessageResponse;
import dev.aniket.Instagram_api.service.PostService;
import dev.aniket.Instagram_api.service.UserService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
public class  PostController {
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPost(
            @NotEmpty @RequestParam String caption,
            @RequestParam(required = false) String location,
            @RequestHeader("Authorization") String token,
            @RequestParam MultipartFile... postContents) throws UserException
    {
//        String token = webRequest.getHeader(SecurityContext.HEADER);

        if (!token.isEmpty()) {
            String userId = userService.findUserProfile(token).getId();

            // create post object
            Post post = Post
                    .builder()
                    .caption(caption)
                    .location(location)
                    .build();

            Post savedPost = postService.createPost(post, userId, postContents);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header(HttpHeaders.LOCATION, "/api/v1/posts/" + savedPost.getId())
                    .body(savedPost);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(MessageResponse.builder().message("Failed: to upload the post!").build());
    }


    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Post>> findPostByUserId(@PathVariable String userId) throws UserException {
        List<Post> posts = postService.findPostByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(posts);
    }

    @GetMapping("/following/{userIds}")
    public ResponseEntity<List<Post>> findAllPostByUserIds(@PathVariable List<String> userIds) throws UserException, PostException {
        List<Post> posts = postService.findAllPostByUserIds(userIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(posts);
    }

    //TODO get all post of following

    @GetMapping("/{postId}")
    public ResponseEntity<Post> findPostById(@PathVariable String postId) throws PostException {
        Post post = postService.findPostById(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(post);
    }

    @PutMapping("/like/{postId}")
    public ResponseEntity<Post> likePost (
            @PathVariable String postId,
            @RequestHeader("Authorization") String token
    ) throws UserException, PostException
    {
        User user = userService.findUserProfile(token);
        Post likedPost = postService.likePost(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(likedPost);
    }

    @PutMapping("/unlike/{postId}")
    public ResponseEntity<Post> unlikePost (
            @PathVariable String postId,
            @RequestHeader("Authorization") String token
    ) throws UserException, PostException
    {
        User user = userService.findUserProfile(token);
        Post likedPost = postService.unlikePost(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(likedPost);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<MessageResponse> deletePost(
            @PathVariable String postId,
            @RequestHeader("Authorization") String token
    ) throws UserException, PostException
    {
        User user = userService.findUserProfile(token);
        String message = postService.deletePost(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(MessageResponse.builder().message(message).build());
    }

    @PutMapping("/saved-post/{postId}")
    public ResponseEntity<MessageResponse> savedPost(
            @PathVariable String postId,
            @RequestHeader("Authorization") String token
    ) throws UserException, PostException
    {
        User user = userService.findUserProfile(token);
        String message = postService.savedPost(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MessageResponse.builder().message(message).build());
    }

    @PutMapping("/unsaved-post/{postId}")
    public ResponseEntity<MessageResponse> unsavedPost(
            @PathVariable String postId,
            @RequestHeader("Authorization") String token
    ) throws UserException, PostException
    {
        User user = userService.findUserProfile(token);
        String message = postService.unsavedPost(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MessageResponse.builder().message(message).build());
    }

    @PostMapping("/mark-view/{postId}")
    public ResponseEntity<MessageResponse> markedViewForPost(
            @RequestHeader("Authorization") String token,
            @NotEmpty @PathVariable String postId
    ) throws UserException, PostException {
        User user = userService.findUserProfile(token);

        postService.markAsView(postId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MessageResponse.builder().message("Post id : " + postId + ", viewed by user id: " + user.getId()).build());
    }
}
