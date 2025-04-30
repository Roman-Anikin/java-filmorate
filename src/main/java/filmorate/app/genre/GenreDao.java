package filmorate.app.genre;

import filmorate.app.film.Film;

import java.util.List;

public interface GenreDao {

    Genre getGenreById(Long genreId);

    List<Genre> getGenres();

    void saveFilmGenre(Film film);

}
