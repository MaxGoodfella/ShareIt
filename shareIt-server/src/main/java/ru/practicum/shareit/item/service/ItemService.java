package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item add(Integer userId, ItemDto item);

    Item update(Integer userId, Integer itemId, ItemDto item);

    ItemDtoOut getItem(Integer userId, Integer itemId);

    List<ItemDtoOut> getItems(Integer userId, Integer from, Integer size);

    List<Item> search(String text);

    ItemDto.ItemCommentDto addComment(Integer userId, Integer itemId, ItemDto.ItemCommentDto comment);

}