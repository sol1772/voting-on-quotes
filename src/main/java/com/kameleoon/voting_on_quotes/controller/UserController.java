package com.kameleoon.voting_on_quotes.controller;

import com.kameleoon.voting_on_quotes.model.User;
import com.kameleoon.voting_on_quotes.service.UserService;
import com.kameleoon.voting_on_quotes.util.AppErrorResponse;
import com.kameleoon.voting_on_quotes.util.AppRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.kameleoon.voting_on_quotes.util.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> find(@PathVariable("id") Long id) {
        Optional<User> user = userService.getUserById(id);
        return ResponseEntity.of(user);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }
        return getNewUser(user);
    }

    @PostMapping("/add-all")
    public ResponseEntity<List<User>> addUsers(@RequestBody List<User> users, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            returnErrorsToClient(bindingResult);
        }
        List<User> created = userService.createUsers(users);
        return ResponseEntity.ok().body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> editUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> updated;
        try {
            updated = userService.updateUser(id, updatedUser);
        } catch (RuntimeException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error when updating user with id {}: {}", id, e.getMessage());
            }
            throw new AppRuntimeException(e.getMessage());
        }
        return updated.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> getNewUser(updatedUser));
    }

    public ResponseEntity<User> getNewUser(@RequestBody User user) {
        User created = userService.createUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @ExceptionHandler
    private ResponseEntity<AppErrorResponse> handleException(AppRuntimeException e) {
        AppErrorResponse response = new AppErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
