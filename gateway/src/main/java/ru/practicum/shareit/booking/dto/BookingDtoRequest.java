package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utils.Create;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoRequest {

    private long id;

    @FutureOrPresent(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime start;

    @Future(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime end;

    @NotNull(groups = {Create.class})
    private Long itemId;
}
