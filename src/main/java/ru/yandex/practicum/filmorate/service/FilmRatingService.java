package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.film.FilmRatingDao;

import java.util.List;

@Slf4j
@Service
public class FilmRatingService {

    private final FilmRatingDao filmRatingDao;

    public FilmRatingService(FilmRatingDao filmRatingDao) {
        this.filmRatingDao = filmRatingDao;
    }

    public FilmRating getRatingById(Integer id) {
        try {
            FilmRating rating = filmRatingDao.getRatingById(id);
            log.info("Получен рейтинг {}", rating);
            return rating;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Рейтинг с id " + id + " не найден");
        }
    }

    public List<FilmRating> getRatings() {
        List<FilmRating> ratings = filmRatingDao.getRatings();
        log.info("Получены рейтинги {}", ratings);
        return ratings;
    }
}
