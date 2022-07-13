package ru.yandex.practicum.filmorate.storage.user.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Primary
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate template;

    public UserDbStorage(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public User addUser(User user) {
        String sql = "insert into users (email, login, name, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?";
        template.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from users order by user_id";
        return template.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUserById(Long id) {
        String sql = "select * from users where user_id = ?";
        return template.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public void addFriend(Long user_id, Long friend_id) {
        String sql = "insert into user_friends (user_id, friend_id) values (?, ?)";
        template.update(sql, user_id, friend_id);
    }

    @Override
    public void removeFriend(Long user_id, Long friend_id) {
        String sql = "delete from user_friends where user_id = ? and friend_id = ?";
        template.update(sql, user_id, friend_id);
    }

    @Override
    public List<User> getFriends(Long id) {
        String sql = "select * from users where user_id in " +
                "(select friend_id from user_friends where user_id = ?)" +
                " order by user_id";
        return template.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        String sql = "select * from users where user_id in ( " +
                "select friend_id from user_friends where user_id = ? " +
                "intersect " +
                "select friend_id from user_friends where user_id = ?) " +
                "order by user_id";
        return template.query(sql, (rs, rowNum) -> makeUser(rs), firstUserId, secondUserId);
    }

    private User makeUser(@NotNull ResultSet rs) throws SQLException {
        return new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
    }
}
