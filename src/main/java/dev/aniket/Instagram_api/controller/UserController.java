package dev.aniket.Instagram_api.controller;

import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.response.MessageResponse;
import dev.aniket.Instagram_api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) throws UserException {
        User user = userService.findUserById(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) throws UserException {
        User user = userService.findUserByUsername(username);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PutMapping("/follow/{followUserId}")
    public ResponseEntity<MessageResponse> followUser(
            @RequestHeader("Authorization") String token,
            @PathVariable String followUserId) throws UserException {
        User reqUser = userService.findUserProfile(token);
        String status = userService.followUser(reqUser.getId(), followUserId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MessageResponse.builder()
                        .message(status)
                        .build()
                );
    }

    @PutMapping("/unfollow/{followUserId}")
    public ResponseEntity<MessageResponse> unfollowUser(
            @RequestHeader("Authorization") String token,
            @PathVariable String followUserId
    ) throws UserException {
        User reqUser = userService.findUserProfile(token);
        String status = userService.unFollowUser(reqUser.getId(), followUserId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MessageResponse.builder()
                        .message(status)
                        .build()
                );
    }

    @GetMapping("/req")
    public ResponseEntity<User> findUserProfile(@RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfile(token);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(user);
    }

    @GetMapping("/multiple-users")
    public ResponseEntity<List<User>> getUserByUserIds(@RequestParam List<String> usersIds) throws UserException {
        List<User> users = userService.findUserByIds(usersIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

//    http://localhost:8080/api/v1/users/search?keyword=query
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUser(@RequestParam("keyword") String query) throws UserException {
        List<User> users = userService.searchUser(query);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String website
    ) throws UserException {
        User profileUser = userService.findUserProfile(token);

        // update user
        User updatedUser = userService.updateUserDetails(profileUser.getId(), name, bio, gender, website);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }
}
