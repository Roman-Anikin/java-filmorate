package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService extends BaseService<User> {

    private final UserStorage userStorage;
    private Long id = 1L;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User add(Long userId, Long friendId) {
        if (userStorage.getUsers().containsKey(userId)) {
            if (userStorage.getUsers().containsKey(friendId)) {
                getById(userId).getFriendsIds().add(friendId);
                getById(friendId).getFriendsIds().add(userId);
                log.info("Пользователи {} и {} стали друзьями", getById(userId), getById(friendId));
                return getById(userId);
            } else {
                throw new ObjectNotFoundException("Пользоветель с id " + friendId + " не найден");
            }
        } else {
            throw new ObjectNotFoundException("Пользоветель с id " + userId + " не найден");
        }
    }

    @Override
    public User remove(Long userId, Long friendId) {
        if (userStorage.getUsers().containsKey(userId)) {
            if (userStorage.getUsers().containsKey(friendId)) {
                getById(userId).getFriendsIds().remove(friendId);
                getById(friendId).getFriendsIds().remove(userId);
                log.info("Пользователи {} и {} перестали быть друзьями", getById(userId), getById(friendId));
                return getById(userId);
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
            Set<Long> userFriendsIds = getById(userId).getFriendsIds();
            for (Long friendId : userFriendsIds) {
                for (Long id : userStorage.getUsers().keySet()) {
                    if (friendId.equals(id)) {
                        friends.add(getById(id));
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

    @Override
    public User getById(Long id) {
        if (userStorage.getUsers().containsKey(id)) {
            return userStorage.getUsers().get(id);
        } else {
            throw new ObjectNotFoundException("Пользоветель с id " + id + " не найден");
        }
    }

    @Override
    public User add(User user) {
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

    @Override
    public User update(User user) {
        if (isLoginValid(user.getLogin()) && userStorage.getUsers().containsKey(user.getId())) {
            user = checkName(user);
            log.info("Обновлен пользователь " + user);
            return userStorage.updateUser(user);
        } else {
            throw new ObjectNotFoundException("Попытка обновить пользователя {}, которого нет в списке " + user);
        }
    }

    @Override
    public List<User> get() {
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
