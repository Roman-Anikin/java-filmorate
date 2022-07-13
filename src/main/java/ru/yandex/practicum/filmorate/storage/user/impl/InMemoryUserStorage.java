package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public void addFriend(Long user_id, Long friend_id) {

    }

    @Override
    public void removeFriend(Long user_id, Long friend_id) {

    }

    @Override
    public List<User> getFriends(Long id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        return null;
    }
}