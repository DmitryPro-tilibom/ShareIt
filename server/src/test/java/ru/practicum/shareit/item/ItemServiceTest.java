package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.NotBookerException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoText;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoNameDescription;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoIdName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemService itemService;

    private final long id = 1L;
    private final User user = new User(id, "User", "user@mail.ru");
    private final User notOwner = new User(2L, "User2", "user2@mail.ru");
    private final ItemDtoNameDescription itemDtoNameDescription = new ItemDtoNameDescription("item", "cool item", true, null);
    private final ItemDto itemDtoOut = new ItemDto(id, "item", "cool item", true,
            new UserDtoIdName(id, "User"));
    private final Item item = new Item(id, "item", "cool item", true, user, null);
    private final CommentDto commentDto = new CommentDto(id, "abc", "User",
            LocalDateTime.of(2023, 7, 1, 12, 12, 12));
    private final Comment comment = new Comment(id, "abc", item, user,
            LocalDateTime.of(2023, 7, 1, 12, 12, 12));
    private final Booking booking = new Booking(id, null, null, item, user, BookingStatus.WAITING);

    @Test
    void saveNewItem_whenUserFound_thenSavedItem() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actualItemDto = itemService.saveNewItem(itemDtoNameDescription, id);

        Assertions.assertEquals(ItemMapper.toItemDto(item), actualItemDto);
        Assertions.assertNull(item.getRequest());
    }

    @Test
    void saveNewItem_whenUserNotFound_thenNotSavedItem() {
        when((userRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () -> itemService.saveNewItem(itemDtoNameDescription, 2L));
    }

    @Test
    void saveNewItem_whenNoName_thenNotSavedItem() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        doThrow(DataIntegrityViolationException.class).when(itemRepository).save(any(Item.class));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemService.saveNewItem(itemDtoNameDescription, id));
    }

    @Test
    void updateItem_whenUserIsOwner_thenUpdatedItem() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        ItemDto actualItemDto = itemService.updateItem(id, itemDtoNameDescription, id);

        Assertions.assertEquals(itemDtoOut, actualItemDto);
    }

    @Test
    void updateItem_whenUserNotOwner_thenNotUpdatedItem() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(notOwner));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Assertions.assertThrows(NotOwnerException.class, () -> itemService.updateItem(id, itemDtoNameDescription, 2L));
    }

    @Test
    void getItemById_whenItemFound_thenReturnedItem() {
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemId(id)).thenReturn(List.of(comment));
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        final ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setLastBooking(BookingMapper.toBookingDtoStatus(booking));
        itemDto.setNextBooking(BookingMapper.toBookingDtoStatus(booking));
        itemDto.setComments(List.of(CommentMapper.toCommentDto(comment)));

        ItemDto actualItemDto = itemService.getItemById(id, id);

        Assertions.assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getItemById_whenItemNotFound_thenExceptionThrown() {
        when((itemRepository).findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(2L, id));
    }

    @Test
    void getItemsByOwner_CorrectArgumentsForPaging_thenReturnItems() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));

        List<ItemDto> targetItems = itemService.getItemsByOwner(0, 10, id);

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository, times(1))
                .findAllByOwnerId(anyLong(), any());
    }

    @Test
    void getItemBySearch_whenTextNotBlank_thenReturnItems() {
        when(itemRepository.search(any(), any())).thenReturn(List.of(item));

        List<ItemDto> targetItems = itemService.getItemBySearch(0, 10, "abc");

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository, times(1))
                .search(any(), any());
    }

    @Test
    void getItemBySearch_whenTextIsBlank_thenReturnEmptyList() {
        List<ItemDto> targetItems = itemService.getItemBySearch(0, 10, "");

        Assertions.assertTrue(targetItems.isEmpty());
        Assertions.assertEquals(0, targetItems.size());
        verify(itemRepository, never()).search(any(), any());
    }

    @Test
    void saveNewComment_whenUserWasBooker_thenSavedComment() {
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenReturn(comment);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        CommentDto actualComment = itemService.saveNewComment(id, new CommentDtoText("abc"), id);

        Assertions.assertEquals(commentDto, actualComment);
    }

    @Test
    void saveNewComment_whenUserWasNotBooker_thenThrownException() {
        when((bookingRepository).existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(false);

        Assertions.assertThrows(NotBookerException.class, () ->
                itemService.saveNewComment(2L, new CommentDtoText("abc"), id));
    }
}
