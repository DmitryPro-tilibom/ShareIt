package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoDescription;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestServiceImpl requestService;

    @PostMapping
    public ItemRequestDto saveNewRequest(@RequestBody ItemRequestDtoDescription requestDtoDescription,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.saveNewRequest(requestDtoDescription, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(defaultValue = "1") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequestById(requestId, userId);
    }
}
