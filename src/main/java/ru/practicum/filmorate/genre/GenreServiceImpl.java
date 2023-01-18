package ru.practicum.filmorate.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.filmorate.exception.ObjectNotFoundException;
import ru.practicum.filmorate.film.Film;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;

    @Override
    public Genre getGenreById(Long genreId) {
        try {
            Genre genre = genreDao.getGenreById(genreId);
            log.info("Получен жанр {}", genre);
            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Жанр с id " + genreId + " не найден");
        }
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = genreDao.getGenres();
        log.info("Получены жанры {}", genres);
        return genres;
    }

    @Override
    public void saveFilmGenre(Film film) {
        genreDao.saveFilmGenre(film);
        log.info("Жанры фильма {} сохранены", film);
    }
}
