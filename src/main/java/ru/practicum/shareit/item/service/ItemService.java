package ru.practicum.shareit.item.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item add(Integer userId, ItemDto item);

    Item update(Integer userId, Integer itemId, ItemDto item);

//    Item getItem(Integer itemId);
    ItemDto getItem(Integer userId, Integer itemId);

    // List<Item> getItems(Integer userId);
    List<ItemDto> getItems(Integer userId);

    List<Item> search(String text);

    ItemDto.ItemCommentDto addComment(Integer userId, Integer itemId, ItemDto.ItemCommentDto comment);

}