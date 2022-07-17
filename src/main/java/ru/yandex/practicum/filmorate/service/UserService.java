package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService extends BaseService<User> {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User add(User user) {
        checkName(user);
        checkLogin(user.getLogin());
        user = userStorage.addUser(user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        checkName(user);
        checkLogin(user.getLogin());
        if (getById(user.getId()) != null) {
            userStorage.updateUser(user);
            log.info("Обновлен пользователь {}", user);
        }
        return user;
    }

    @Override
    public List<User> get() {
        List<User> users = userStorage.getUsers();
        log.info("Получен список пользователей {}", users);
        return users;
    }

    @Override
    public User getById(Long id) {
        try {
            User user = userStorage.getUserById(id);
            log.info("Получен пользователь {}", user);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользоветель с id " + id + " не найден");
        }
    }

    @Override
    public void add(Long userId, Long friendId) {
        if (getById(userId) != null && getById(friendId) != null) {
            userStorage.addFriend(userId, friendId);
            log.info("Пользователь {} добавил в друзья {}", getById(userId), getById(friendId));
        }
    }

    @Override
    public void remove(Long userId, Long friendId) {
        if (getById(userId) != null && getById(friendId) != null) {
            userStorage.removeFriend(userId, friendId);
            log.info("Пользователь {} удалил из друзей {}", getById(userId), getById(friendId));
        }
    }

    public List<User> getFriends(Long userId) {
        List<User> friends = new ArrayList<>();
        if (getById(userId) != null) {
            friends = userStorage.getFriends(userId);
            log.info("Получен список друзей {} пользователя с id {}", friends, userId);
        }
        return friends;
    }

    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        List<User> friends = new ArrayList<>();
        if (getById(firstUserId) != null && getById(secondUserId) != null) {
            friends = userStorage.getCommonFriends(firstUserId, secondUserId);
            log.info("Получен список общих друзей пользователей с id {} и {} {}", firstUserId, secondUserId, friends);
        }
        return friends;
    }

    private void checkName(@NotNull User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void checkLogin(@NotNull String login) {
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы. Переданный логин: "
                    + login);
        }
    }
}
