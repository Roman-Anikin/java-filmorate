package ru.practicum.filmorate.rating;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class RatingDaoImpl implements RatingDao {

    private final JdbcTemplate template;

    @Override
    public Rating getRatingById(Long ratingId) {
        return template.queryForObject("SELECT * FROM ratings WHERE rating_id = ?",
                (rs, rowNum) -> makeRating(rs), ratingId);
    }

    @Override
    public List<Rating> getRatings() {
        return template.query("SELECT * FROM ratings ORDER BY rating_id",
                (rs, rowNum) -> makeRating(rs));
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        return new Rating(rs.getLong("rating_id"), rs.getString("name"));
    }
}