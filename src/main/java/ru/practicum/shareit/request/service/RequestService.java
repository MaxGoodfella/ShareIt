package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {

    Request add(Integer userId, RequestDto requestDto);

    List<RequestDto> getRequestsSent(Integer userId);

    RequestDto getRequest(Integer requestId, Integer userId);

    List<RequestDto> getRequests(Integer from, Integer size, Integer userId);

}