package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmGenreDaoImpl;

import java.util.List;

@Slf4j
@Service
public class FilmGenreService {

    private final FilmGenreDaoImpl filmGenreDao;

    public FilmGenreService(FilmGenreDaoImpl filmGenreDao) {
        this.filmGenreDao = filmGenreDao;
    }

    public FilmGenre getGenreById(Integer id) {
        try {
            FilmGenre genre = filmGenreDao.getGenreById(id);
            log.info("Получен жанр {}", genre);
            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Жанр с id " + id + " не найден");
        }
    }

    public List<FilmGenre> getGenres() {
        List<FilmGenre> genres = filmGenreDao.getGenres();
        log.info("Получены жанры {}", genres);
        return genres;
    }
}
