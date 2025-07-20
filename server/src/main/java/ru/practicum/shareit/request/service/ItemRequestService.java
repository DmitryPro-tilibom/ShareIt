package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoDescription;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveNewRequest(ItemRequestDtoDescription requestDtoDescription, long userId);

    List<ItemRequestDto> getRequestsByRequestor(long userId);

    List<ItemRequestDto> getAllRequests(Integer from, Integer size, long userId);

    ItemRequestDto getRequestById(long requestId, long userId);
}
