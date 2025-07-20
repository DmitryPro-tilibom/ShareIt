package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoDescription;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto saveNewRequest(ItemRequestDtoDescription requestDtoDescription, long userId) {
        log.info("Создание нового запроса {}", requestDtoDescription.getDescription());
        User requestor = getUser(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDtoDescription);
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getRequestsByRequestor(long userId) {
        log.info("Получение всех запросов по просителю с идентификатором {}", userId);
        getUser(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequestorId(userId, Sort.by(DESC, "created"));
        return addItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Integer from, Integer size, long userId) {
        log.info("Получение всех запросов постранично");
        getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdIsNot(userId, pageable);
        return addItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        log.info("Получение запроса по идентификатору {}", requestId);
        getUser(userId);
        ItemRequestDto requestDtoOut = ItemRequestMapper.toItemRequestDto(requestRepository.findById(requestId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Объект класса %s не найден", ItemRequest.class))));
        requestDtoOut.setItems(itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList()));
        return requestDtoOut;
    }

    private List<ItemRequestDto> addItems(List<ItemRequest> requests) {
        final List<ItemRequestDto> requestsDto = new ArrayList<>();
        for (ItemRequest request : requests) {
            ItemRequestDto requestDtos = ItemRequestMapper.toItemRequestDto(request);
            List<ItemDto> items = itemRepository.findAllByRequestId(request.getId()).stream()
                    .map(ItemMapper::toItemDto).collect(Collectors.toList());
            requestDtos.setItems(items);
            requestsDto.add(requestDtos);
        }
        return requestsDto;
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Объект класса %s не найден", User.class)));
    }
}
