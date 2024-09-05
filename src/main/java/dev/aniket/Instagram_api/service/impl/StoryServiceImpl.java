package dev.aniket.Instagram_api.service.impl;

import dev.aniket.Instagram_api.dao.StoryDao;
import dev.aniket.Instagram_api.dao.UserDao;
import dev.aniket.Instagram_api.dto.UserDto;
import dev.aniket.Instagram_api.exception.StoryException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Story;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.service.StoryService;
import dev.aniket.Instagram_api.service.UserService;
import dev.aniket.Instagram_api.utility.UtilityClass;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class StoryServiceImpl implements StoryService {
    private final StoryDao storyDao;
    private final UserService userService;
    private final UserDao userDao;
    private final FfmpegService ffmpegService;

    @Value("${story}")
    private String storyLocation;

    @Autowired
    public StoryServiceImpl(StoryDao storyDao, UserService userService, UserDao userDao, FfmpegService ffmpegService) {
        this.storyDao = storyDao;
        this.userService = userService;
        this.userDao = userDao;
        this.ffmpegService = ffmpegService;
    }

    @PostConstruct
    public void init() {
        File file = new File(storyLocation);

        if (!file.exists()) {
            boolean status = file.mkdir();
            log.info(status ? file + " is created!" : file + " is not created!");
        } else {
            log.info("{} is already created!", file);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Story createStory(String caption, MultipartFile storyFile, String userId, double videoStart, double videoEnd) throws UserException {
        User user = userService.findUserById(userId);

        // create story
        Story story = new Story();

        // set the details
        story.setCaption(caption);
        story.setId(String.valueOf(UUID.randomUUID()));
        story.setTimestamp(LocalDateTime.now());

        // convert to dto
        UserDto userDto = UserDto
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .username(user.getUsername())
                .build();

        story.setUser(userDto);

        // stored the storyFile
        Path storyPath = null;
        try {
            String uniqueFilename = String.valueOf(UUID.randomUUID());
            uniqueFilename += ("." + UtilityClass.getFileExtension(storyFile.getOriginalFilename(), storyFile.getSize()));

            storyPath = Paths.get(storyLocation, uniqueFilename);

            Files.createFile(storyPath);

            // image
            if (storyFile.getContentType().startsWith("image/")) {
                Files.copy(storyFile.getInputStream(), storyPath, StandardCopyOption.REPLACE_EXISTING);
            }
            // video
            else if (storyFile.getContentType().startsWith("video/")) {
//                Files.copy(storyFile.getInputStream(), storyPath, StandardCopyOption.REPLACE_EXISTING);

                Files.copy(storyFile.getInputStream(), storyPath, StandardCopyOption.REPLACE_EXISTING);

                double videoDuration = ffmpegService.isVideoLessThan30s(storyPath);

                // check the video is greater than 30s
                if (videoDuration > 30) {
                    storyPath = ffmpegService.trimVideoToFirst30s(storyPath, storyFile.getOriginalFilename(), storyFile.getSize(), videoStart, videoEnd);
                }
            }

        } catch (Exception e) {
            try {
                Files.deleteIfExists(storyPath);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e.getMessage());
        }

        // save url of the story
        story.setPath(storyPath.toString());

        Story savedStory = storyDao.save(story);

        // story link to the user
        List<Story> stories = user.getStories();
        stories.add(savedStory);

        // save user
        User savedUser = userDao.save(user);

        return savedStory;
    }

    @Override
    public List<Story> findStoryByUserId(String userId) throws UserException, StoryException {
        List<Story> stories = storyDao.findAllStoryByUserId(userId);

        if (stories.isEmpty())
            throw new UserException("this user doesn't have any story!");

        return stories;
    }

    // delete story by the user
    @Transactional(rollbackOn = Exception.class)
    @Override
    public void deleteStory(String storyId, String userId) throws UserException, StoryException{
        Story story = storyDao.findById(storyId)
                .orElseThrow(() -> new StoryException("Story is not found by this id: " + storyId));

        if (story.getId().equals(userId)) {
            storyDao.deleteUsers_storiesRow(story.getId());

            // delete the content
            Path storyPath = Path.of(story.getPath());
            try {
                Files.deleteIfExists(storyPath);
                log.info("delete story content: {}", story.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            storyDao.deleteStory(story.getId());
            log.info("delete story by user: {}", userId);
            return;
        }
        throw new UserException("You not able to delete the post");
    }

    @Override
    public void markAsView(String storyId, String userId) throws StoryException, UserException {
        Story story = storyDao.findById(storyId)
                .orElseThrow(() -> new StoryException("Story not found by given id: " + storyId));
        User user = userService.findUserById(userId);

        // convert to dto
        UserDto userDto = UserDto
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .username(user.getUsername())
                .build();

        story.getStoryViewers().add(userDto);
        // save updates
        storyDao.save(story);
    }

    // delete story after 24 hours and this method run after 1 min
    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackOn = Exception.class)
    public void deleteStoryAfter24Hours() throws StoryException {
        List<Story> stories = storyDao.findAllStoryBeforeTime(LocalDateTime.now().minusHours(24));

        for (Story currStory : stories) {
            Path currPath = Path.of(currStory.getPath());

            // delete the story content
            try {
                storyDao.deleteUsers_storiesRow(currStory.getId());
                storyDao.deleteStory(currStory.getId());
                Files.deleteIfExists(currPath);
                log.error("delete story after 24 hours: " + currStory.getUser().getName());
            } catch (IOException e) {
                log.error("after 24 hour try to delete the story its failed!");
                throw new StoryException("after 24 hour try to delete the story its failed!");
            }
        }
        log.info("Delete old stories: {}", LocalTime.now());
    }
}

