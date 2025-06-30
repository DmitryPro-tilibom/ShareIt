package ru.practicum.item;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private static int generatorId = 0;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItems = new LinkedHashMap<>();

    @Override
    public Optional<Item> getItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getItemsByOwner(long userId) {
        return userItems.get(userId);
    }

    @Override
    public List<Item> getItemsByText(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        ((item.getName().toLowerCase().contains(text.toLowerCase())) ||
                                (item.getDescription().toLowerCase().contains(text.toLowerCase()))))
                .collect(Collectors.toList());
    }

    @Override
    public Item saveNewItem(Item item, long userId) {
        item.setId(++generatorId);
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        final List<Item> ownersItems = userItems.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>());
        ownersItems.add(item);
        return item;
    }
}
