package dev.aniket.Instagram_api.service.impl;

import dev.aniket.Instagram_api.dao.PostDao;
import dev.aniket.Instagram_api.dao.UserDao;
import dev.aniket.Instagram_api.dto.UserDto;
import dev.aniket.Instagram_api.exception.PostException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Post;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.service.PostService;
import dev.aniket.Instagram_api.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostDao postDao;
    private final UserService userService;
    private final UserDao userDao;

    @Value("${post}")
    private String postLocation;

    @Autowired
    public PostServiceImpl(PostDao postDao, UserService userService, UserDao userDao) {
        this.postDao = postDao;
        this.userService = userService;
        this.userDao = userDao;
    }

    @PostConstruct
    public void init() {
        File file = new File(postLocation);

        if (!file.exists()) {
            boolean status = file.mkdir();
            log.info(status ? file + " is created!" : file + " is not created!");
        } else {
            log.info("{} is already created!", file);
        }
    }

    @Override
    public Post createPost(Post post, String userId, MultipartFile... postContents) throws UserException {
        User user = userService.findUserById(userId);
        List<String> postContentPaths = new ArrayList<>();

        // convert to dto
        UserDto userDto = UserDto
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .username(user.getUsername())
                .build();

        // save the post
        String uniqueName = String.valueOf(UUID.randomUUID());
        post.setId(uniqueName);

        try {
            File postFolder = new File(postLocation, uniqueName);
            Path postPath = Paths.get(postLocation, uniqueName);
            postFolder.mkdir();


            // save the images or videos
            for (MultipartFile postContent : postContents) {
                if (postContent.getContentType().startsWith("image/")) {
                    InputStream inputStream = postContent.getInputStream();
                    String uniqueFilename = String.valueOf(UUID.randomUUID());

                    String fileExtension = "." + getFileExtension(postContent.getOriginalFilename(), postContent.getSize());
                    uniqueFilename += fileExtension;

                    Path path = Paths.get(String.valueOf(postPath), uniqueFilename);
                    Files.createFile(path);

                    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

                    // hold the proper path
                    Resource resource = new FileSystemResource(path);

                    postContentPaths.add(resource.toString());
                } else if (postContent.getContentType().startsWith("video/")) {
                    InputStream inputStream = postContent.getInputStream();
                    String uniqueFilename = String.valueOf(UUID.randomUUID());

                    String fileExtension = "." + getFileExtension(postContent.getOriginalFilename(), postContent.getSize());
                    uniqueFilename += fileExtension;

                    Path path = Paths.get(String.valueOf(postPath), uniqueFilename);
                    Files.createFile(path);

                    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

                    // hold the proper path
                    Resource resource = new FileSystemResource(path);

                    postContentPaths.add(resource.toString());
                } else {
                    log.error("{} this is not acceptable!", postContent.getOriginalFilename());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        post.setUser(userDto);
        post.setPostContent(postContentPaths);
        post.setCreatedAt(LocalDateTime.now());


        return postDao.save(post);
    }

    private String getFileExtension(String originalFilename, long size) {
        // find the last index of dot character
        int lastIndexOfDot = originalFilename.lastIndexOf(".");

        if (lastIndexOfDot > 0 && lastIndexOfDot < (size - 1)) {
            return originalFilename.substring(lastIndexOfDot + 1);
        }
        return null;
    }

    @Override
    public String deletePost(String postId, String userId) throws UserException, PostException {
        Post post = findPostById(postId);

        User user = userService.findUserById(userId);

        if (post.getUser().getId().equals(user.getId())) {
            postDao.deleteById(postId);
            //TODO post also from the user account
            return "Post Delete Successfully";
        }
        throw new PostException("You can't delete other user's post!");
    }

    @Override
    public List<Post> findPostByUserId(String userId) throws UserException {
        List<Post> posts = postDao.findPostByUserId(userId);

        if (posts.isEmpty()) {
            throw new UserException("this user does not have any post");
        }

        return posts;
    }

    @Override
    public Post findPostById(String postId) throws PostException {
        Optional<Post> post = postDao.findById(postId);

        if (post.isPresent())
            return post.get();

        throw new PostException("Post not found with id: " + postId);
    }

    @Override
    public List<Post> findAllPostByUserIds(List<String> userIds) throws UserException, PostException {
        List<Post> posts = postDao.findAllPostByUserIdsSortedByDateDescendingOrder(userIds);

        if (posts.isEmpty())
            throw new PostException("No post available");

        return posts;
    }

    @Override
    public String savedPost(String postId, String userId) throws UserException, PostException {
        Post post = findPostById(postId);

        User user = userService.findUserById(userId);

        if (!user.getSavedPost().contains(post)) {
            user.getSavedPost().add(post);
        }

        userDao.save(user);

        return "Post Saved Successfully!";
    }

    @Override
    public String unsavedPost(String postId, String userId) throws UserException, PostException {
        Post post = findPostById(postId);

        User user = userService.findUserById(userId);

        if (user.getSavedPost().contains(post)) {
            user.getSavedPost().remove(post);
        }

        userDao.save(user);

        return "Post remove Successfully!";
    }

    @Override
    public Post likePost(String postId, String userId) throws UserException, PostException {
        Post post = findPostById(postId);

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

        post.getLikedByUsers().add(userDto);

        return postDao.save(post);
    }

    @Override
    public Post unlikePost(String postId, String userId) throws UserException, PostException {
        Post post = findPostById(postId);

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

        Set<UserDto> likedUser = post.getLikedByUsers();
        likedUser.remove(userDto);
        post.setLikedByUsers(likedUser);

        return postDao.save(post);
    }

    @Override
    public void markAsView(String postId, String userId) throws PostException, UserException {
        Post post = findPostById(postId);

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

        post.getPostViewers().add(userDto);

        // save updates
        postDao.save(post);
    }
}
