package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                UserMapper.toUserDtoIdName(item.getOwner())
        );
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public ItemDtoIdName toItemDtoIdName(Item item) {
        return new ItemDtoIdName(
                item.getId(),
                item.getName()
        );
    }

    public Item toItem(ItemDtoNameDescription itemDtoIn) {
        return new Item(
                itemDtoIn.getName(),
                itemDtoIn.getDescription(),
                itemDtoIn.getAvailable()
        );
    }
}
