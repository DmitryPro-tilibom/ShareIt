package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDtoIdName(booking.getItem()),
                UserMapper.toUserDtoIdName(booking.getBooker()),
                booking.getStatus()
        );
    }

    public BookingDtoStatus toBookingDtoStatus(Booking booking) {
        return new BookingDtoStatus(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker().getId()
        );
    }

    public Booking toBooking(BookingDtoStartEnd bookingDtoStartEnd, Booking booking) {
        booking.setStart(bookingDtoStartEnd.getStart());
        booking.setEnd(bookingDtoStartEnd.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}
