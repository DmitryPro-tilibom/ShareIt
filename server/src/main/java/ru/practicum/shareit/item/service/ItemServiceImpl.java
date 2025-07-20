package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.NotBookerException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoText;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoNameDescription;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto saveNewItem(ItemDtoNameDescription itemDtoNameDescription, long userId) {
        log.info("Создание новой вещи {}", itemDtoNameDescription.getName());
        User owner = getUser(userId);
        Item item = ItemMapper.toItem(itemDtoNameDescription);
        item.setOwner(owner);
        Long requestId = itemDtoNameDescription.getRequestId();
        if (requestId != null) {
            item.setRequest(requestRepository.findById(requestId).orElseThrow(() ->
                    new ObjectNotFoundException(String.format("Объект класса %s не найден", ItemRequest.class))));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDtoNameDescription itemDtoIn, long userId) {
        log.info("Обновление вещи {} с идентификатором {}", itemDtoIn.getName(), itemId);
        getUser(userId);
        Item item = getItem(itemId);
        String name = itemDtoIn.getName();
        String description = itemDtoIn.getDescription();
        Boolean available = itemDtoIn.getAvailable();
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

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(long itemId, long userId) {
        log.info("Получение вещи по идентификатору {}", itemId);
        final Item item = getItem(itemId);
        return addBookingsAndComments(item, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwner(Integer from, Integer size, long userId) {
        log.info("Получение вещи по владельцу {}", userId);
        getUser(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size,
                Sort.by("id").ascending()));
        return addBookingsAndCommentsForList(items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemBySearch(Integer from, Integer size, String text) {
        log.info("Получение вещи по поиску {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text, PageRequest.of(from / size, size)).stream()
                .map(ItemMapper::toItemDto).collect(toList());
    }

    @Override
    public CommentDto saveNewComment(long itemId, CommentDtoText commentDtoText, long userId) {
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new NotBookerException("Пользователь не пользовался вещью");
        }
        User user = getUser(userId);
        Item item = getItem(itemId);
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDtoText, item, user));
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto addBookingsAndComments(Item item, long userId) {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        LocalDateTime thisMoment = LocalDateTime.now();
        if (itemDto.getOwner().getId() == userId) {
            itemDto.setLastBooking(bookingRepository
                    .findFirstByItemIdAndStartLessThanEqualAndStatus(itemDto.getId(), thisMoment,
                            BookingStatus.APPROVED, Sort.by(DESC, "end"))
                    .map(BookingMapper::toBookingDtoStatus)
                    .orElse(null));

            itemDto.setNextBooking(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatus(itemDto.getId(), thisMoment,
                            BookingStatus.APPROVED, Sort.by(ASC, "end"))
                    .map(BookingMapper::toBookingDtoStatus)
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
            if (itemsWithLastBookings.size() > 0 && lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDtoStatus(lastBooking));
            }
            Booking nextBooking = itemsWithNextBookings.get(item);
            if (itemsWithNextBookings.size() > 0 && nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDtoStatus(nextBooking));
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

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", Item.class)));
    }
}
