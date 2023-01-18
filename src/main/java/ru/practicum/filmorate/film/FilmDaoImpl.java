package ru.practicum.filmorate.film;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.practicum.filmorate.genre.Genre;
import ru.practicum.filmorate.rating.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate template;
    private final String selectQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, " +
            "r.name AS r_name, g.genre_id AS g_id, g.genre_name AS g_name " +
            "FROM films AS f " +
            "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
            "LEFT JOIN ( " +
            "SELECT fg.film_id AS film_id, array_agg(g.genre_id) AS genre_id, array_agg(g.name) AS genre_name " +
            "FROM film_genre AS fg " +
            "JOIN genres g ON g.genre_id = fg.genre_id " +
            "GROUP BY fg.film_id " +
            ") AS g USING (film_id) ";

    @Override
    public Film saveFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        template.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = selectQuery + "ORDER BY film_id";
        return template.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sql = selectQuery + "WHERE film_id = ?";
        return template.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), filmId);
    }

    @Override
    public void saveLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        template.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        template.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = selectQuery +
                "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) AS user_id " +
                "FROM film_like " +
                "GROUP BY film_id) AS fl ON f.film_id = fl.film_id " +
                "ORDER BY user_id DESC NULLS LAST " +
                "LIMIT ?";
        return template.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new Rating(rs.getLong("rating_id"), rs.getString("r_name")),
                new LinkedHashSet<>());

        if (rs.getArray("g_id") != null) {
            Integer[] ids = (Integer[]) rs.getArray("g_id").getArray();
            String[] names = (String[]) rs.getArray("g_name").getArray();

            for (int i = 0; i < ids.length; i++) {
                film.getGenres().add(new Genre(Long.valueOf(ids[i]), names[i]));
            }
        }
        return film;
    }
}