package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item add(Integer userId, ItemDto item);

    Item update(Integer userId, Integer itemId, ItemDto item);

    Item getItem(Integer itemId);

    List<Item> getItems(Integer userId);

    List<Item> search(String text);

}