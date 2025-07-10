package ru.practicum.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.dto.ItemMapper;
import ru.practicum.user.dto.UserMapper;

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

    public BookingDtoStatusBooker toBookingDtoStatusBooker(Booking booking) {
        return new BookingDtoStatusBooker(
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
