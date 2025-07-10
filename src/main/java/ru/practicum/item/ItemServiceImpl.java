package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.dto.BookingMapper;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.NotBookerException;
import ru.practicum.exception.NotOwnerException;
import ru.practicum.item.comment.*;
import ru.practicum.item.comment.dto.CommentDtoText;
import ru.practicum.item.comment.dto.CommentDto;
import ru.practicum.item.comment.dto.CommentMapper;
import ru.practicum.item.dto.ItemDtoNameDescription;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemMapper;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto saveNewItem(ItemDtoNameDescription itemDtoNameDescription, long userId) {
        log.info("Создание новой вещи {}", itemDtoNameDescription.getName());
        User owner = getUser(userId);
        Item item = ItemMapper.toItem(itemDtoNameDescription);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDtoNameDescription itemDtoNameDescription, long userId) {
        log.info("Обновление вещи {} с идентификатором {}", itemDtoNameDescription.getName(), itemId);
        getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", Item.class)));
        String name = itemDtoNameDescription.getName();
        String description = itemDtoNameDescription.getDescription();
        Boolean available = itemDtoNameDescription.getAvailable();
        if (item.getOwner().getId() == userId) {
            if (name != null && !name.isBlank()) {
                item.setName(name);
            }
            if (description != null && !description.isBlank()) {
                item.setDescription(description);
            }
            if (available != null) {
                item.setAvailable(available);
            }
        } else {
            throw new NotOwnerException(String.format("Пользователь с id %s не является собственником %s",
                    userId, name));
        }
        return ItemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long itemId, long userId) {
        log.info("Получение вещи по идентификатору {}", itemId);
        return itemRepository.findById(itemId).map(item -> addBookingsAndComments(item, userId)).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", Item.class)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemsByOwner(long userId) {
        log.info("Получение вещи по владельцу {}", userId);
        getUser(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return addBookingsAndCommentsForList(items);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemBySearch(String text) {
        log.info("Получение вещи по поиску {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).collect(toList());
    }

    @Override
    public CommentDto saveNewComment(long itemId, CommentDtoText commentDtoIn, long userId) {
        User user = getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", Item.class)));
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), LocalDateTime.now())) {
            throw new NotBookerException("Пользователь не пользовался вещью");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDtoIn, item, user));
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto addBookingsAndComments(Item item, long userId) {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        LocalDateTime thisMoment = LocalDateTime.now();
        if (itemDto.getOwner().getId() == userId) {
            itemDto.setLastBooking(bookingRepository
                    .findFirstByItemIdAndStartLessThanEqualAndStatus(itemDto.getId(), thisMoment,
                            BookingStatus.APPROVED, Sort.by(DESC, "end"))
                    .map(BookingMapper::toBookingDtoStatusBooker)
                    .orElse(null));

            itemDto.setNextBooking(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatus(itemDto.getId(), thisMoment,
                            BookingStatus.APPROVED, Sort.by(ASC, "end"))
                    .map(BookingMapper::toBookingDtoStatusBooker)
                    .orElse(null));
        }

        itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList()));

        return itemDto;
    }

    private List<ItemDto> addBookingsAndCommentsForList(List<Item> items) {
        LocalDateTime thisMoment = LocalDateTime.now();

        Map<Item, Booking> itemsWithLastBookings = bookingRepository
                .findByItemInAndStartLessThanEqualAndStatus(items, thisMoment,
                        BookingStatus.APPROVED, Sort.by(DESC, "end"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, Booking> itemsWithNextBookings = bookingRepository
                .findByItemInAndStartAfterAndStatus(items, thisMoment,
                        BookingStatus.APPROVED, Sort.by(ASC, "end"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, List<Comment>> itemsWithComments = commentRepository
                .findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            Booking lastBooking = itemsWithLastBookings.get(item);
            if (!itemsWithLastBookings.isEmpty() && lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDtoStatusBooker(lastBooking));
            }
            Booking nextBooking = itemsWithNextBookings.get(item);
            if (!itemsWithNextBookings.isEmpty() && nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDtoStatusBooker(nextBooking));
            }
            List<CommentDto> commentDtos = itemsWithComments.getOrDefault(item, Collections.emptyList())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(toList());
            itemDto.setComments(commentDtos);

            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", User.class)));
    }
}
