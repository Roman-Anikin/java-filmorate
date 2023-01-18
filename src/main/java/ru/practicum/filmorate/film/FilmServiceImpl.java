package ru.practicum.filmorate.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.ObjectNotFoundException;
import ru.practicum.filmorate.exception.ValidationException;
import ru.practicum.filmorate.genre.GenreService;
import ru.practicum.filmorate.user.User;
import ru.practicum.filmorate.user.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmDao filmDao;
    private final UserService userService;
    private final GenreService genreService;

    @Override
    public Film addFilm(Film film) {
        checkReleaseDate(film.getReleaseDate());
        filmDao.saveFilm(film);
        Optional.ofNullable(film.getGenres()).ifPresent(genres -> genreService.saveFilmGenre(film));
        log.info("Добавлен фильм " + film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkReleaseDate(film.getReleaseDate());
        Film newFilm = checkFilm(film.getId());
        Optional.ofNullable(film.getName()).ifPresent(newFilm::setName);
        Optional.ofNullable(film.getDescription()).ifPresent(newFilm::setDescription);
        Optional.ofNullable(film.getReleaseDate()).ifPresent(newFilm::setReleaseDate);
        Optional.of(film.getDuration()).ifPresent(newFilm::setDuration);
        Optional.ofNullable(film.getMpa()).ifPresent(newFilm::setMpa);
        Optional.ofNullable(film.getGenres()).ifPresent(newFilm::setGenres);
        filmDao.updateFilm(newFilm);
        genreService.saveFilmGenre(newFilm);
        log.info("Обновлен фильм " + newFilm);
        return newFilm;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = filmDao.getFilms();
        log.info("Получен список фильмов {}", films);
        return films;
    }

    @Override
    public Film getFilmById(Long filmId) {
        Film film = checkFilm(filmId);
        log.info("Получен фильм {}", film);
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        User user = userService.getUserById(userId);
        Film film = checkFilm(filmId);
        filmDao.saveLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", user, film);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        User user = userService.getUserById(userId);
        Film film = checkFilm(filmId);
        filmDao.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк к фильму {}", user, film);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmDao.getPopularFilms(count);
        log.info("Получен список популярных фильмов {}", films);
        return films;
    }

    private void checkReleaseDate(LocalDate date) {
        if (date.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года. Переданная дата: "
                    + date);
        }
    }

    private Film checkFilm(Long filmId) {
        try {
            return filmDao.getFilmById(filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Фильм с id " + filmId + " не найден");
        }
    }
}