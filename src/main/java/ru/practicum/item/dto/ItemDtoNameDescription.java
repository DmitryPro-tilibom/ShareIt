package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ItemDtoNameDescription {
    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;

    @NotBlank(groups = {Create.class})
    @Size(max = 1000, groups = {Create.class, Update.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;
}
