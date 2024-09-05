package dev.aniket.Instagram_api.controller;

import dev.aniket.Instagram_api.exception.PostException;
import dev.aniket.Instagram_api.exception.StoryException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Story;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.response.MessageResponse;
import dev.aniket.Instagram_api.service.StoryService;
import dev.aniket.Instagram_api.service.UserService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/stories")
public class StoryController {
    private final StoryService storyService;
    private final UserService userService;

    @Autowired
    public StoryController(StoryService storyService, UserService userService) {
        this.storyService = storyService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Story> createStory(
            @RequestParam String caption,
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Double videoStart,
            @RequestParam(required = false) Double videoEnd,
            @RequestParam MultipartFile multipartFile
            ) throws UserException {
        User user = userService.findUserProfile(token);
        Story createStory = null;

        if (videoStart != null && videoEnd != null) {
            createStory = storyService.createStory(caption, multipartFile, user.getId(), videoStart, videoEnd);
        }
        else {
            createStory = storyService.createStory(caption, multipartFile, user.getId(), 0, 30);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, "/api/v1/stories/" + createStory.getId())
                .body(createStory);
    }

    @GetMapping("/all/users/{userId}")
    public ResponseEntity<List<Story>> findAllStoryUserId(@PathVariable String userId) throws StoryException, UserException {
        List<Story> stories = storyService.findStoryByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stories);
    }

    @DeleteMapping()
    public ResponseEntity<MessageResponse> deleteStoryById (
            @PathVariable String storyId,
            @RequestHeader("Authorization") String token
    ) throws UserException, StoryException {
        User user = userService.findUserProfile(token);
        storyService.deleteStory(storyId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MessageResponse.builder().message("Successfully: deleted the story").build());
    }

    @PostMapping("/mark-view/{storyId}")
    public ResponseEntity<MessageResponse> markedViewForPost(
            @RequestHeader("Authorization") String token,
            @NotEmpty @PathVariable String storyId
    ) throws UserException, StoryException {
        User user = userService.findUserProfile(token);

        storyService.markAsView(storyId, user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MessageResponse.builder().message("Story id : " + storyId + ", viewed by user id: " + user.getId()).build());
    }
}
