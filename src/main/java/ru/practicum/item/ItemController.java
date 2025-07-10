package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.comment.dto.CommentDtoText;
import ru.practicum.item.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDtoNameDescription;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveNewItem(@Validated(Create.class)
                                   @RequestBody ItemDtoNameDescription itemDtoNameDescription,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST / items {} / user {}", itemDtoNameDescription.getName(), userId);
        return itemService.saveNewItem(itemDtoNameDescription, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                                 @Validated(Update.class)
                                 @RequestBody ItemDtoNameDescription itemDtoNameDescription,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH / items {} / user {}", itemId, userId);
        return itemService.updateItem(itemId, itemDtoNameDescription, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET / items {} / user {}", itemId, userId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET / items / user {}", userId);
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemBySearch(@RequestParam String text) {
        log.info("GET / search / {}", text);
        return itemService.getItemBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                    @Validated(Create.class) @RequestBody CommentDtoText commentDtoText,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST / comment / item {}", itemId);
        return itemService.saveNewComment(itemId, commentDtoText, userId);
    }
}