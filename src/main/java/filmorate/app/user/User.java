package filmorate.app.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    private Long id;

    @NotBlank(message = "электронная почта не может быть пустой")
    @Email(message = "неверный формат электронной почты")
    private String email;

    @NotBlank(message = "логин не может быть пустым или содержать пробелы")
    @NotEmpty(message = "логин не может быть пустым или содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;

}
