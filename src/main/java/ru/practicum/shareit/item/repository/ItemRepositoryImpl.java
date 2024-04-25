package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();

    private int generatedID = 0;

    @Override
    public Item add(Integer userId, Item item) {

        for (Item existingItem : items.values()) {
            if (existingItem.getName().equalsIgnoreCase(item.getName()) &&
                    existingItem.getDescription().equalsIgnoreCase(item.getDescription())) {
                throw new EntityAlreadyExistsException(Item.class, "Item с названием " +
                        item.getName() + " уже зарегистрирован.");
            }
        }

        item.setId(generateID());

        items.put(item.getId(), item);
        return item;

    }

    @Override
    public Item update(Item item) {
        Item existingItem = items.get(item.getId());

        if (existingItem == null) {
            throw new EntityNotFoundException(Item.class, "Item с id " + item.getId() + " не найден.");
        }

        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && !item.getAvailable().equals(existingItem.getAvailable())) {
            existingItem.setAvailable(item.getAvailable());
        }

        items.put(item.getId(), existingItem);

        return existingItem;
    }


    @Override
    public Item getItem(Integer itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new EntityNotFoundException(Integer.class, "Фильм с ID " + itemId + " не найден.");
        }
        return item;
    }

    @Override
    public List<Item> getItems(Integer userId) {
        List<Item> userItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                userItems.add(item);
            }
        }

        return userItems;
    }

    @Override
    public List<Item> search(String text) {

        if (text == null || text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> matchingItems = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getAvailable()) {
                if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    matchingItems.add(item);
                }
            }
        }

        return matchingItems;

    }


    private int generateID() {
        return ++generatedID;
    }

}