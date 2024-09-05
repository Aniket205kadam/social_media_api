package dev.aniket.Instagram_api.security.service;

import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.security.config.UserPrinciple;
import dev.aniket.Instagram_api.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ApplicationContext context;

    public UserDetailsServiceImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserService userService = context.getBean(UserService.class);
        User user = null;

        try {
            user = userService.findUserByUsername(username);
        } catch (UserException e) {
            throw new UsernameNotFoundException(username + " is not exist!");
        }

        return new UserPrinciple(user);
    }
}
