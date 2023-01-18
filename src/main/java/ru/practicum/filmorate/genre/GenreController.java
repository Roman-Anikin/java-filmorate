package ru.practicum.filmorate.genre;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(path = "/genres")
@AllArgsConstructor
@RestController
public class GenreController {

    private final GenreServiceImpl service;

    @GetMapping("/{genreId}")
    public Genre getGenreById(@PathVariable Long genreId) {
        return service.getGenreById(genreId);
    }

    @GetMapping
    public List<Genre> getGenres() {
        return service.getGenres();
    }
}
