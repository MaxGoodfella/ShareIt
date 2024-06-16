package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private static  final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final RequestService requestService;


    @PostMapping
    public Request add(@RequestHeader(REQUEST_HEADER) Integer userId,
                       @Valid @RequestBody RequestDto requestDto) {
        log.info("Start saving request {}", requestDto);
        Request addedRequest = requestService.add(userId, requestDto);
        log.info("Finish saving request {}", addedRequest);
        return addedRequest;
    }

    @GetMapping
    public List<RequestDto> getRequestsSentByUserId(@RequestHeader(REQUEST_HEADER) Integer userId) {
        log.info("Start fetching requests from user with id = {}", userId);
        List<RequestDto> fetchedRequests = requestService.getRequestsSent(userId);
        log.info("Finish fetching requests from user with id = {}", userId);
        return fetchedRequests;
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@PathVariable("requestId") Integer requestId,
                                 @RequestHeader(REQUEST_HEADER) Integer userId) {
        log.info("Start fetching request with id = {}", requestId);
        RequestDto fetchedRequest = requestService.getRequest(requestId, userId);
        log.info("Finish fetching request with id = {}", requestId);
        return fetchedRequest;
    }

    @GetMapping("/all")
    public List<RequestDto> getRequests(@RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "100") Integer size,
                                        @RequestHeader(REQUEST_HEADER) Integer userId) {
        log.info("Start fetching requests using 'from' parameter = {} and 'size' parameter = {}", from, size);
        List<RequestDto> fetchedRequests = requestService.getRequests(from, size, userId);
        log.info("Start fetching requests using 'from' parameter = {} and 'size' parameter = {}", from, size);
        return fetchedRequests;
    }

}