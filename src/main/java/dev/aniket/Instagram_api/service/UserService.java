package dev.aniket.Instagram_api.service;

import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.User;

import java.util.List;

public interface UserService {
    User registerUser(User user) throws UserException;

    User findUserById(String id) throws UserException;

    User findUserProfile(String token) throws UserException;

    User findUserByUsername(String username) throws UserException;

    String followUser(String reqUserId, String followUserId) throws UserException;

    String unFollowUser(String reqUserId, String followUserId) throws UserException;

    List<User> findUserByIds(List<String> userIds) throws UserException;

    List<User> searchUser(String query) throws UserException;

    User updateUserDetails(String id, String name, String bio, String gender, String website) throws UserException;
}
