package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoIdName;
import ru.practicum.shareit.user.dto.UserDtoIdName;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoIdName item;
    private UserDtoIdName booker;
    private BookingStatus status;
}
