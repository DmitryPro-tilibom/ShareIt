package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserDto {

    private long id;

    @NotBlank(groups = Create.class)
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;

    @NotEmpty(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    @Size(max = 512, groups = {Create.class, Update.class})
    private String email;
}
