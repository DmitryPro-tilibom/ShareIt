package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoStartEnd;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto saveNewBooking(BookingDtoStartEnd bookingDtoStartEnd, long userId);

    BookingDto approve(long bookingId, Boolean isApproved, long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllByBooker(Integer from, Integer size, String state, long bookerId);

    List<BookingDto> getAllByOwner(Integer from, Integer size, String state, long ownerId);
}
