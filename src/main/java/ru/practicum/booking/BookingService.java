package ru.practicum.booking;

import ru.practicum.booking.dto.BookingDtoStartEnd;
import ru.practicum.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto saveNewBooking(BookingDtoStartEnd bookingDtoIn, long userId);

    BookingDto approve(long bookingId, Boolean isApproved, long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllByBooker(String subState, long bookerId);

    List<BookingDto> getAllByOwner(long ownerId, String state);
}