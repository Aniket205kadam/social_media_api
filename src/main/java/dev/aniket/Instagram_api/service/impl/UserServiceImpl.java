package dev.aniket.Instagram_api.service.impl;

import dev.aniket.Instagram_api.dao.UserDao;
import dev.aniket.Instagram_api.dto.UserDto;
import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.security.service.JwtService;
import dev.aniket.Instagram_api.security.userClaims.JwtProvider;
import dev.aniket.Instagram_api.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.jwtService = jwtService;
    }

    @Override
    public User registerUser(User user) throws UserException {
        // check the email id is unique
        if (userDao.existsByEmail(user.getEmail()))
            throw new UserException("Email is already exist!");

        // check the username is unique
        if (userDao.existsByUsername(user.getUsername()))
            throw new UserException("Username is already exist!");

        // the account creation only given fields are taken
        User newUser = User
                .builder()
                .id(String.valueOf(UUID.randomUUID()))
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .mobile(user.getMobile())
                .username(user.getUsername())
                .name(user.getName())
                .build();

        return userDao.save(newUser);
    }

    @Override
    public User findUserById(String userId) throws UserException {
        Optional<User> existingUser = userDao.findById(userId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        throw new UserException("user not exist with id: " + userId);
    }

    @Override
    public User findUserProfile(@NotEmpty String token) throws UserException {
        // remove Bearer
        token = token.substring(7);

        String username = jwtService.extractUsernameFromToken(token);

        User existingUser = findUserByUsername(username);

        System.out.println(existingUser.getName());

        return existingUser;
    }

    @Override
    public User findUserByUsername(String username) throws UserException {
        Optional<User> existingUser = userDao.findByUsername(username);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        throw new UserException("user not exist with username: " + username);
    }

    @Override
    public String followUser(String reqUserId, String followUserId) throws UserException {
        User reqUser = findUserById(reqUserId);
        User followUser = findUserById(followUserId);

        UserDto follower = UserDto
                .builder()
                .email(reqUser.getEmail())
                .username(reqUser.getUsername())
                .id(reqUser.getId())
                .name(reqUser.getName())
                .profileImage(reqUser.getProfileImage())
                .build();

        UserDto following = UserDto
                .builder()
                .email(followUser.getEmail())
                .username(followUser.getUsername())
                .id(followUser.getId())
                .name(followUser.getName())
                .profileImage(followUser.getProfileImage())
                .build();

        reqUser.getFollowing().add(following);
        followUser.getFollower().add(follower);

        userDao.save(followUser);
        userDao.save(reqUser);

        return "You are following " + followUser.getUsername();
    }

    @Override
    public String unFollowUser(String reqUserId, String unFollowUserId) throws UserException {
        User reqUser = findUserById(reqUserId);
        User followUser = findUserById(unFollowUserId);

        UserDto follower = UserDto
                .builder()
                .email(reqUser.getEmail())
                .username(reqUser.getUsername())
                .id(reqUser.getId())
                .name(reqUser.getName())
                .profileImage(reqUser.getProfileImage())
                .build();

        UserDto following = UserDto
                .builder()
                .email(followUser.getEmail())
                .username(followUser.getUsername())
                .id(followUser.getId())
                .name(followUser.getName())
                .profileImage(followUser.getProfileImage())
                .build();

        reqUser.getFollowing().remove(following);
        followUser.getFollower().remove(follower);

        userDao.save(followUser);
        userDao.save(reqUser);

        return  "You have unfollowing " + followUser.getUsername();
    }

    @Override
    public List<User> findUserByIds(@NotEmpty List<String> userIds) throws UserException {
        return userDao.findAllUsersByUserIds(userIds);
    }

    @Override
    public List<User> searchUser(@NotEmpty String query) throws UserException {
        List<User> users = userDao.findByQuery(query);

        if (users.isEmpty()) {
            throw new UserException("user not found!");
        }
        return users;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public User updateUserDetails(String userId, String updatedName, String updatedBio, String updatedGender, String updatedWebsite) throws UserException {
        User existingUser = findUserById(userId);

        if (updatedName != null)
            existingUser.setName(updatedName);

        if (updatedBio != null)
            existingUser.setBio(updatedBio);

        if (updatedGender != null)
            existingUser.setGender(updatedGender);

        if (updatedWebsite != null)
            existingUser.setWebsite(updatedWebsite);

        return userDao.save(existingUser);
    }
}
