package ru.practicum.filmorate.rating;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.ObjectNotFoundException;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingDao ratingDao;

    @Override
    public Rating getRatingById(Long ratingId) {
        try {
            Rating rating = ratingDao.getRatingById(ratingId);
            log.info("Получен рейтинг {}", rating);
            return rating;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Рейтинг с id " + ratingId + " не найден");
        }
    }

    @Override
    public List<Rating> getRatings() {
        List<Rating> ratings = ratingDao.getRatings();
        log.info("Получены рейтинги {}", ratings);
        return ratings;
    }
}
