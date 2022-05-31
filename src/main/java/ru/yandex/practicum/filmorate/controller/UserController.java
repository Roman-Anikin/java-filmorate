package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            user.setId(id++);
        }
        user = checkName(user);
        try {
            if (isLoginValid(user.getLogin())) {
                users.put(user.getId(), user);
                log.info("Добавлен пользователь " + user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        } catch (ValidationException e) {
            log.info("Exception: " + e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        try {
            if (isLoginValid(user.getLogin()) && users.containsKey(user.getId())) {
                user = checkName(user);
                users.put(user.getId(), user);
                log.info("Обновлен пользователь " + user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                log.info("Попытка обновить пользователя {}, которого нет в списке", user);
            }
        } catch (ValidationException e) {
            log.info("Exception: " + e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private User checkName(@NotNull User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    private boolean isLoginValid(@NotNull String login) {
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы. Переданный логин: "
                    + login);
        }
        return true;
    }
}
