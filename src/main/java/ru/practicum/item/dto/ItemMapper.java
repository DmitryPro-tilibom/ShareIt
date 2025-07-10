package ru.practicum.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.item.Item;
import ru.practicum.user.dto.UserMapper;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                UserMapper.toUserDtoIdName(item.getOwner())
        );
    }

    public ItemDtoIdName toItemDtoIdName(Item item) {
        return new ItemDtoIdName(
                item.getId(),
                item.getName()
        );
    }

    public Item toItem(ItemDtoNameDescription itemDtoNameDescription) {
        return new Item(
                itemDtoNameDescription.getName(),
                itemDtoNameDescription.getDescription(),
                itemDtoNameDescription.getAvailable()
        );
    }
}
