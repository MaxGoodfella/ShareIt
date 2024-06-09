package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(REQUEST_HEADER) Long userId,
                                      @Valid @RequestBody ItemDto item) {
        log.info("Start saving item {}", item);
        ResponseEntity<Object> response = itemClient.add(userId, item);
        log.info("Finish saving item {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(REQUEST_HEADER) Long userId,
                                         @PathVariable("itemId") Long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Start updating item {}", itemDto);
        ResponseEntity<Object> response = itemClient.update(userId, itemId, itemDto);
        log.info("Finish updating item {}", response);
        return response;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByItemId(@RequestHeader(REQUEST_HEADER) Long userId,
                                                  @PathVariable Long itemId) {
        log.info("Start fetching item with id = {}", itemId);
        ResponseEntity<Object> response = itemClient.getItem(itemId, userId);
        log.info("Finish fetching item with id = {}", itemId);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(REQUEST_HEADER) Long userId,
                                                   @RequestParam(required = false, defaultValue = "0") Long from,
                                                   @RequestParam(required = false, defaultValue = "100") Long size) {
        log.info("Start fetching items for user with id = {}", userId);
        ResponseEntity<Object> response = itemClient.getItems(userId, from, size);
        log.info("Finish fetching items for user with id = {}", userId);
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(required = false) String text) {
        log.info("Start fetching items by name/description using 'text' parameter = {}", text);
        ResponseEntity<Object> response = itemClient.search(text);
        log.info("Finish fetching items by name/description using 'text' parameter = {}", text);
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(REQUEST_HEADER) Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestBody CommentDto commentDto) {
        log.info("Start adding comment {} to item with id = {}", commentDto, itemId);
        ResponseEntity<Object> response = itemClient.addComment(userId, itemId, commentDto);
        log.info("Finish adding comment {} to item with id = {}", response, itemId);
        return response;
    }

}