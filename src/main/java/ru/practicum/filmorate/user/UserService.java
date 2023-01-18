package ru.practicum.filmorate.user;

import java.util.List;

public interface UserService {

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(Long userId);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long firstUserId, Long secondUserId);
}
