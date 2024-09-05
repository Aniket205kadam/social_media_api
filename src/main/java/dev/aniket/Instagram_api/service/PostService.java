package dev.aniket.Instagram_api.service;

import dev.aniket.Instagram_api.exception.PostException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    // create post
    Post createPost(Post post, String userId, MultipartFile... postImages) throws UserException;

    // delete post
    String deletePost(String postId, String userId) throws UserException, PostException;

    // get all post of particular user
    List<Post> findPostByUserId(String userId) throws UserException;

    // get post by id
    Post findPostById(String postId) throws PostException;

    // get all post of give ids
    List<Post> findAllPostByUserIds(List<String> userIds) throws UserException, PostException;

    // saved post by particular user
    String savedPost(String postId, String userId) throws UserException, PostException;

    // un-saved post by particular user
    String unsavedPost(String postId, String userId) throws UserException, PostException;

    // like the post
    Post likePost(String postId, String userId) throws UserException, PostException;

    // unlike the post
    Post unlikePost(String postId, String userId) throws UserException, PostException;

    // mark the post as view
    void markAsView(String postId, String userId) throws PostException, UserException;
}
