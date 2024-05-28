package ru.practicum.shareit.item.controller;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static  final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;


    @PostMapping
    public Item add(@RequestHeader(REQUEST_HEADER) Integer userId,
                    @Valid @RequestBody ItemDto item) {
        log.info("Start saving item {}", item);
        Item addedItem = itemService.add(userId, item);
        log.info("Finish saving item {}", addedItem);
        return addedItem;
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader(REQUEST_HEADER) Integer userId,
                       @PathVariable("itemId") Integer itemId,
                       @RequestBody ItemDto itemDto) {
        log.info("Start updating item {}", itemDto);
        Item updatedItem = itemService.update(userId, itemId, itemDto);
        log.info("Finish updating item {}", updatedItem);
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut getItemByItemId(@RequestHeader(REQUEST_HEADER) Integer userId,
                                      @PathVariable Integer itemId) {
        log.info("Start fetching item with id = {}", itemId);
        ItemDtoOut fetchedItem = itemService.getItem(userId, itemId);
        log.info("Finish fetching item with id = {}", fetchedItem.getId());
        return fetchedItem;
    }

    @GetMapping
    public List<ItemDtoOut> getItemsByUserId(@RequestHeader(REQUEST_HEADER) Integer userId,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "100") Integer size) {
        log.info("Start fetching items for user with id = {}", userId);
        List<ItemDtoOut> fetchedItems = itemService.getItems(userId, from, size);
        log.info("Finish fetching items for user with id = {}", userId);
        return fetchedItems;
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(required = false) String text) {
        log.info("Start fetching items by name/description using 'text' parameter = {}", text);
        List<Item> fetchedItems = itemService.search(text);
        log.info("Finish fetching items by name/description using 'text' parameter = {}", text);
        return fetchedItems;
    }

    @PostMapping("/{itemId}/comment")
    public ItemDto.ItemCommentDto  addComment(@RequestHeader(REQUEST_HEADER) Integer userId,
                              @PathVariable("itemId") Integer itemId,
                              @RequestBody ItemDto.ItemCommentDto comment) {
        log.info("Start adding comment {} to item with id = {}", comment, itemId);
        ItemDto.ItemCommentDto  addedComment = itemService.addComment(userId, itemId, comment);
        log.info("Finish adding comment {} to item with id = {}", addedComment, itemId);
        return addedComment;
    }

}