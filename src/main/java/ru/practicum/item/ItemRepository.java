package ru.practicum.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getItemById(long id);

    List<Item> getItemsByOwner(long userId);

    List<Item> getItemsByText(String text);

    Item saveNewItem(Item item, long userId);
}
