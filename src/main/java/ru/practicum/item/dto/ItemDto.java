package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.booking.dto.BookingDtoStatusBooker;
import ru.practicum.item.comment.dto.CommentDto;
import ru.practicum.user.dto.UserDtoIdName;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoStatusBooker lastBooking;
    private BookingDtoStatusBooker nextBooking;
    private List<CommentDto> comments;
    private UserDtoIdName owner;

    public ItemDto(long id, String name, String description, Boolean available, UserDtoIdName owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
