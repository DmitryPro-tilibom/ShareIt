package ru.practicum.item;

import ru.practicum.item.comment.dto.CommentDtoText;
import ru.practicum.item.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDtoNameDescription;
import ru.practicum.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsByOwner(long userId);

    List<ItemDto> getItemBySearch(String text);

    ItemDto saveNewItem(ItemDtoNameDescription itemDtoIn, long userId);

    ItemDto updateItem(long itemId, ItemDtoNameDescription itemDtoIn, long userId);

    CommentDto saveNewComment(long itemId, CommentDtoText commentDtoIn, long userId);
}
