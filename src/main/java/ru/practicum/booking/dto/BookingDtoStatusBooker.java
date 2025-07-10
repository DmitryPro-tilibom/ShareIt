package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoStatusBooker {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private long bookerId;
}
