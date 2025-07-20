package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDtoIdName;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoStatus lastBooking;
    private BookingDtoStatus nextBooking;
    private List<CommentDto> comments;
    private UserDtoIdName owner;
    private Long requestId;

    public ItemDto(long id, String name, String description, Boolean available, UserDtoIdName owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
