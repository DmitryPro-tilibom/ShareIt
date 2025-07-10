package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDtoStartEnd;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingState;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.exception.*;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto saveNewBooking(BookingDtoStartEnd bookingDtoStartEnd, long userId) {
        User booker = getUser(userId);
        Item item = getItem(bookingDtoStartEnd.getItemId());
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Вещь недоступна для брони");
        }
        if (booker.getId() == item.getOwner().getId()) {
            throw new NotAvailableToBookOwnItemsException("Функция бронировать собственную вещь отсутствует");
        }
        if (!bookingDtoStartEnd.getEnd().isAfter(bookingDtoStartEnd.getStart()) ||
                bookingDtoStartEnd.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongDatesException("Дата начала бронирования должна быть раньше даты возврата");
        }
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        bookingRepository.save(BookingMapper.toBooking(bookingDtoStartEnd, booking));
        log.info("Бронирование с идентификатором {} создано", booking.getId());
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto approve(long bookingId, Boolean isApproved, long userId) {
        Booking booking = getById(bookingId);
        Item item = getItem(booking.getItem().getId());
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ItemIsNotAvailableException("Вещь уже забронирована");
        }
        if (item.getOwner().getId() != userId) {
            throw new IllegalViewAndUpdateException("Подтвердить бронирование может только собственник вещи");
        }
        BookingStatus newBookingStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newBookingStatus);
        log.info("Бронирование с идентификатором {} обновлено", booking.getId());
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        log.info("Получение бронирования по идентификатору {}", bookingId);
        Booking booking = getById(bookingId);
        User booker = booking.getBooker();
        User owner = getUser(booking.getItem().getOwner().getId());
        if (booker.getId() != userId && owner.getId() != userId) {
            throw new IllegalViewAndUpdateException("Только автор или владелец может просматривать данное броинрование");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByBooker(String state, long bookerId) {
        User booker = getUser(bookerId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(booker.getId(), Sort.by(DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStateCurrent(booker.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndStatePast(booker.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStateFuture(booker.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        BookingStatus.REJECTED, Sort.by(DESC, "end"));
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String state) {
        User owner = getUser(ownerId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStateCurrent(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndStatePast(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStateFuture(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Booking getById(long bookingId) {
        log.info("Получение бронирования по идентификатору {}", bookingId);
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", Booking.class)));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", User.class)));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", Item.class)));
    }
}