package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private Long id = 1L;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) {
        if (userStorage.getUsers().containsKey(userId)) {
            if (userStorage.getUsers().containsKey(friendId)) {
                getUserById(userId).getFriendsIds().add(friendId);
                getUserById(friendId).getFriendsIds().add(userId);
                log.info("Пользователи {} и {} стали друзьями", getUserById(userId), getUserById(friendId));
                return getUserById(userId);
            } else {
                throw new ObjectNotFoundException("Пользоветель с id " + friendId + " не найден");
            }
        } else {
            throw new ObjectNotFoundException("Пользоветель с id " + userId + " не найден");
        }
    }

    public User removeFriend(Long userId, Long friendId) {
        if (userStorage.getUsers().containsKey(userId)) {
            if (userStorage.getUsers().containsKey(friendId)) {
                getUserById(userId).getFriendsIds().remove(friendId);
                getUserById(friendId).getFriendsIds().remove(userId);
                log.info("Пользователи {} и {} перестали быть друзьями", getUserById(userId), getUserById(friendId));
                return getUserById(userId);
            } else {
                throw new ObjectNotFoundException("Пользоветель с id " + friendId + " не найден");
            }
        } else {
            throw new ObjectNotFoundException("Пользоветель с id " + userId + " не найден");
        }
    }

    public List<User> getFriends(Long userId) {
        if (userStorage.getUsers().containsKey(userId)) {
            List<User> friends = new ArrayList<>();
            Set<Long> userFriendsIds = getUserById(userId).getFriendsIds();
            for (Long friendId : userFriendsIds) {
                for (Long id : userStorage.getUsers().keySet()) {
                    if (friendId.equals(id)) {
                        friends.add(getUserById(id));
                        break;
                    }
                }
            }
            return friends;
        } else {
            throw new ObjectNotFoundException("Пользоветель с id " + userId + " не найден");
        }
    }

    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        List<User> firstUserFriends = getFriends(firstUserId);
        List<User> secondUserFriends = getFriends(secondUserId);
        List<User> commonFriends = new ArrayList<>(firstUserFriends);
        commonFriends.retainAll(secondUserFriends);
        return commonFriends;
    }

    public User getUserById(Long id) {
        if (userStorage.getUsers().containsKey(id)) {
            return userStorage.getUsers().get(id);
        } else {
            throw new ObjectNotFoundException("Пользоветель с id " + id + " не найден");
        }
    }

    public User addUser(User user) {
        user = checkName(user);
        if (isLoginValid(user.getLogin()) && !userStorage.getUsers().containsKey(user.getId())) {
            if (user.getId() == null) {
                user.setId(id++);
            }
            log.info("Добавлен пользователь " + user);
            return userStorage.addUser(user);
        } else {
            throw new ObjectAlreadyExistException("Попытка добавить пользователя {}, который уже есть в списке " + user);
        }
    }

    public User updateUser(User user) {
        if (isLoginValid(user.getLogin()) && userStorage.getUsers().containsKey(user.getId())) {
            user = checkName(user);
            log.info("Обновлен пользователь " + user);
            return userStorage.updateUser(user);
        } else {
            throw new ObjectNotFoundException("Попытка обновить пользователя {}, которого нет в списке " + user);
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers().values());
    }

    private @NotNull User checkName(@NotNull User user) {
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
