package ru.practicum.filmorate.genre;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.filmorate.film.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate template;

    @Override
    public Genre getGenreById(Long genreId) {
        return template.queryForObject("SELECT * FROM genres WHERE genre_id = ?",
                (rs, rowNum) -> makeGenre(rs), genreId);
    }

    @Override
    public List<Genre> getGenres() {
        return template.query("SELECT * FROM genres ORDER BY genre_id",
                (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    @Transactional
    public void saveFilmGenre(Film film) {
        template.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());

        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        template.batchUpdate(sql, film.getGenres(), 100,
                (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                });
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getLong("genre_id"), rs.getString("name"));
    }
}
