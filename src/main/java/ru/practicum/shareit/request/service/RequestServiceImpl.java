package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.paginationvalidation.PaginationValidator;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.JpaRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final JpaRequestRepository requestRepository;

    private final JpaUserRepository userRepository;

    private final JpaItemRepository itemRepository;

    private final ModelMapper modelMapper;

    private final PaginationValidator paginationValidator;


    @Override
    @Transactional
    public Request add(Integer userId, RequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Request newRequest = modelMapper.map(requestDto, Request.class);

        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequestor(user);

        return requestRepository.save(newRequest);

    }

    @Override
    public List<RequestDto> getRequestsSent(Integer userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));


        List<Request> requestList = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        List<Integer> idList = requestList.stream()
                .map(Request::getId)
                .collect(Collectors.toList());


        Map<Integer, List<RequestDto.RequestItemDto>> items = itemRepository.findAllByRequestIdIn(idList)
                .stream()
                .map(ItemMapper::toRequestItemDto)
                .collect(groupingBy(RequestDto.RequestItemDto::getRequestId, toList()));

        return requestList.stream()
                .map(request -> {
                    RequestDto requestDto = modelMapper.map(request, RequestDto.class);
                    requestDto.setItems(items.getOrDefault(request.getId(), Collections.emptyList()));
                    return requestDto;
                })
                .collect(Collectors.toList());

    }

    @Override
    public RequestDto getRequest(Integer requestId, Integer userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(Request.class, String.valueOf(requestId),
                        "Запрос с id " + requestId + " не найден."));

        Map<Integer, List<RequestDto.RequestItemDto>> items = itemRepository.findAllByRequestIdIn(List.of(requestId))
                .stream()
                .map(ItemMapper::toRequestItemDto)
                .collect(groupingBy(RequestDto.RequestItemDto::getRequestId, toList()));

        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setItems(items.getOrDefault(request.getId(), Collections.emptyList()));

        return requestDto;

    }

    @Override
    public List<RequestDto> getRequests(Integer from, Integer size, Integer userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        paginationValidator.validateSearchParameters(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        Page<Request> requestPage = requestRepository.findAllByRequestorIdNot(userId, pageable);

        List<Request> requestList = requestPage.getContent();

        if (requestList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> idList = requestList.stream()
                .map(Request::getId)
                .collect(Collectors.toList());

        Map<Integer, List<RequestDto.RequestItemDto>> items = itemRepository.findAllByRequestIdIn(idList)
                .stream()
                .map(ItemMapper::toRequestItemDto)
                .collect(Collectors.groupingBy(RequestDto.RequestItemDto::getRequestId));

        return requestList.stream()
                .map(request -> {
                    RequestDto requestDto = modelMapper.map(request, RequestDto.class);
                    requestDto.setItems(items.getOrDefault(request.getId(), Collections.emptyList()));
                    return requestDto;
                })
                .collect(Collectors.toList());

    }

}