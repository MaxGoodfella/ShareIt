package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final RequestClient requestClient;


    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(REQUEST_HEADER) Long userId,
                                      @Valid @RequestBody RequestDto requestDto) {
        log.info("Start saving request {}", requestDto);
        ResponseEntity<Object> response = requestClient.add(userId, requestDto);
        log.info("Finish saving request {}", response);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsSentByUserId(@RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("Start fetching requests from user with id = {}", userId);
        ResponseEntity<Object> response = requestClient.getRequestsSent(userId);
        log.info("Finish fetching requests from user with id = {}", userId);
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable("requestId") Long requestId,
                                             @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("Start fetching request with id = {}", requestId);
        ResponseEntity<Object> response = requestClient.getRequest(userId, requestId);
        log.info("Finish fetching request with id = {}", requestId);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") Long from,
                                              @Positive @RequestParam(required = false, defaultValue = "100") Long size,
                                              @RequestHeader(REQUEST_HEADER) Long userId) {
        log.info("Start fetching requests using 'from' parameter = {} and 'size' parameter = {}", from, size);
        ResponseEntity<Object> response = requestClient.getRequests(userId, from, size);
        log.info("Finish fetching requests using 'from' parameter = {} and 'size' parameter = {}", from, size);
        return response;
    }

}