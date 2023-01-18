package ru.practicum.filmorate.rating;

import java.util.List;

public interface RatingDao {

    Rating getRatingById(Long ratingId);

    List<Rating> getRatings();
}
