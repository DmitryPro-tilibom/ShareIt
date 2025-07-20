package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoDescription;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private final User requestor = new User(2L, "user2", "user2@mail.ru");
    private final User user = new User(1L, "User", "user@mail.ru");
    private final ItemRequest request = new ItemRequest(1L, "description", requestor, LocalDateTime.now());
    private final Item item = new Item(1L, "item", "cool", true, user, request);

    @Test
    void saveNewRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.save(any())).thenReturn(request);

        final ItemRequestDto actualRequest = requestService.saveNewRequest(
                new ItemRequestDtoDescription("description"), 2L);

        Assertions.assertEquals(ItemRequestMapper.toItemRequestDto(request), actualRequest);
    }

    @Test
    void getRequestsByRequestor_whenUserFound_thenSavedRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestorId(anyLong(), any())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        final ItemRequestDto requestDtoOut = ItemRequestMapper.toItemRequestDto(request);
        requestDtoOut.setItems(List.of(ItemMapper.toItemDto(item)));

        List<ItemRequestDto> actualRequests = requestService.getRequestsByRequestor(2L);

        Assertions.assertEquals(List.of(requestDtoOut), actualRequests);
    }

    @Test
    void getRequestsByRequestor_whenUserNotFound_thenThrownException() {
        when((userRepository).findById(3L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () ->
                requestService.getRequestsByRequestor(3L));
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        final ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);
        requestDto.setItems(List.of(ItemMapper.toItemDto(item)));

        ItemRequestDto actualRequest = requestService.getRequestById(1L, 1L);

        Assertions.assertEquals(requestDto, actualRequest);
    }
}
