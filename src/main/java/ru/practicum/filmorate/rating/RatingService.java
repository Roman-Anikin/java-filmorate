package ru.practicum.filmorate.rating;

import java.util.List;

public interface RatingService {

    Rating getRatingById(Long ratingId);

    List<Rating> getRatings();

}
