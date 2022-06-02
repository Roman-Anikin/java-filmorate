package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class User {

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
