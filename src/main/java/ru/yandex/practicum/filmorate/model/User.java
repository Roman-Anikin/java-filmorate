package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class User {

    private Set<Long> friendsIds = new HashSet<>();

    private Long id;

    @NotBlank(message = "электронная почта не может быть пустой")
    @Email(message = "неверный формат электронной почты")
    private String email;

    @NotBlank(message = "логин не может быть пустым и содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;

}
