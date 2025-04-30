package filmorate.app.film;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import filmorate.app.genre.Genre;
import filmorate.app.rating.Rating;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @NotEmpty(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    @NotNull(message = "Рейтинг не может быть пустым")
    private Rating mpa;

    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<Genre> genres;

}
