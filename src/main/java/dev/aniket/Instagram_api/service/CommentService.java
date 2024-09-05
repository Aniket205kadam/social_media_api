package dev.aniket.Instagram_api.service;

import dev.aniket.Instagram_api.exception.CommentException;
import dev.aniket.Instagram_api.exception.PostException;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.Comment;

public interface CommentService {
    // create comment
    Comment createComment(Comment comment, String postId, String userId) throws UserException, PostException;

    // get comment by Id
    Comment findCommentById(String commentId) throws CommentException;

    // like the comment
    Comment likeComment(String commentId, String userId) throws CommentException, UserException, PostException;

    // Dislike the comment
    Comment unlikeComment(String commentId, String userId) throws CommentException, UserException, PostException;

    // Delete the comment
    void deleteCommentById(String commentId, String userId) throws CommentException;
}
