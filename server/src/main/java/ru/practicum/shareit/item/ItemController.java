package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoText;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoNameDescription;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveNewItem(@RequestBody ItemDtoNameDescription itemDtoNameDescription,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.saveNewItem(itemDtoNameDescription, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                                 @RequestBody ItemDtoNameDescription itemDtoNameDescription,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.updateItem(itemId, itemDtoNameDescription, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestParam(defaultValue = "1") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByOwner(from, size, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemBySearch(@RequestParam(defaultValue = "1") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam String text) {
        return itemService.getItemBySearch(from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveNewComment(@PathVariable long itemId,
                                        @RequestBody CommentDtoText commentDtoText,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.saveNewComment(itemId, commentDtoText, userId);
    }
}
