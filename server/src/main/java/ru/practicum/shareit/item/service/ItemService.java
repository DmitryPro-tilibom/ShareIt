package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDtoText;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoNameDescription;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveNewItem(ItemDtoNameDescription itemDtoIn, long userId);

    ItemDto updateItem(long itemId, ItemDtoNameDescription itemDtoNameDescription, long userId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsByOwner(Integer from, Integer size, long userId);

    List<ItemDto> getItemBySearch(Integer from, Integer size, String text);

    CommentDto saveNewComment(long itemId, CommentDtoText commentDtoText, long userId);
}
