package ru.practicum.item.comment.dto;

import lombok.Data;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CommentDtoText {
    @Size(max = 1000, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String text;
}
