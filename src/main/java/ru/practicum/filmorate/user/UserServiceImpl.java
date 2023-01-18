package ru.practicum.filmorate.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public User addUser(User user) {
        checkName(user);
        userDao.addUser(user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkName(user);
        User newUser = checkUser(user.getId());
        Optional.ofNullable(user.getEmail()).ifPresent(newUser::setEmail);
        Optional.ofNullable(user.getLogin()).ifPresent(newUser::setLogin);
        Optional.ofNullable(user.getName()).ifPresent(newUser::setName);
        Optional.ofNullable(user.getBirthday()).ifPresent(newUser::setBirthday);
        userDao.updateUser(newUser);
        log.info("Обновлен пользователь {}", newUser);
        return newUser;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = userDao.getUsers();
        log.info("Получен список пользователей {}", users);
        return users;
    }

    @Override
    public User getUserById(Long userId) {
        User user = checkUser(userId);
        log.info("Получен пользователь {}", user);
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User firstUser = checkUser(userId);
        User secondUser = checkUser(friendId);
        userDao.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья {}", firstUser, secondUser);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User firstUser = checkUser(userId);
        User secondUser = checkUser(friendId);
        userDao.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей {}", firstUser, secondUser);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = checkUser(userId);
        List<User> friends = userDao.getFriends(userId);
        log.info("Получен список друзей {} пользователя {}", friends, user);
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        User firstUser = checkUser(firstUserId);
        User secondUser = checkUser(secondUserId);
        List<User> friends = userDao.getCommonFriends(firstUserId, secondUserId);
        log.info("Получен список общих друзей пользователей {} и {} {}", firstUser, secondUser, friends);
        return friends;
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private User checkUser(Long userId) {
        try {
            return userDao.getUserById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}