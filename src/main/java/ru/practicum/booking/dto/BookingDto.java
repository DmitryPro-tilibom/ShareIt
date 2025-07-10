package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.dto.ItemDtoIdName;
import ru.practicum.user.dto.UserDtoIdName;

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
