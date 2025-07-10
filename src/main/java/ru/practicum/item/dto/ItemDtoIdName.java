package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDtoIdName {
    private long id;
    private String name;
}
