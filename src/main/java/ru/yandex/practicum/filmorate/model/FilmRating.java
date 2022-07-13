package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FilmRating {

    private Integer id;
    private String name;

    public FilmRating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
