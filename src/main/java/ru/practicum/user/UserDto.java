package ru.practicum.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotEmpty(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    private String email;
}
