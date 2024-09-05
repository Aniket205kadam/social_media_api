package dev.aniket.Instagram_api.service;

import dev.aniket.Instagram_api.exception.StoryException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Story;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoryService {
    // create story
    Story createStory(String caption, MultipartFile storyFile, String userId, double videoStart, double videoEnd) throws UserException;

    // get story by user id
    List<Story> findStoryByUserId(String userId) throws UserException, StoryException;

    // delete story by story id
    void deleteStory(String storyId, String id) throws UserException, StoryException;

    // mark the story as view
    void markAsView(String storyId, String userId) throws StoryException, UserException;
}
