package com.kameleoon.voting_on_quotes.service;

import com.kameleoon.voting_on_quotes.model.User;
import com.kameleoon.voting_on_quotes.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createUser(User user) {
        User dbUser = userRepository.save(user);
        if (logger.isInfoEnabled()) {
            logger.info("Created user {}", dbUser);
        }
        return dbUser;
    }

    @Transactional
    public List<User> createUsers(List<User> users) {
        List<User> dbUsers = userRepository.saveAll(users);
        if (logger.isInfoEnabled()) {
            logger.info("Created users {}", dbUsers);
        }
        return dbUsers;
    }

    @Transactional
    public Optional<User> updateUser(Long id, User updatedUser) {
        Optional<User> updated = userRepository.findById(id).map(oldUser -> userRepository.save(updatedUser));
        if (logger.isInfoEnabled()) {
            updated.ifPresent(n -> logger.info("Updated user {}", updated.get()));
        }
        return updated;
    }
}
