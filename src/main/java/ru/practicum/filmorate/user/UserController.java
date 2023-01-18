package ru.practicum.filmorate.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(path = "/users")
@AllArgsConstructor
@RestController
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return userServiceImpl.addUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userServiceImpl.updateUser(user);
    }

    @GetMapping
    public List<User> get() {
        return userServiceImpl.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userServiceImpl.getUserById(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userServiceImpl.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userServiceImpl.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Long userId) {
        return userServiceImpl.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        return userServiceImpl.getCommonFriends(userId, otherId);
    }
}
