package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FilmGenre {

    private Integer id;
    private String name;

    public FilmGenre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
