package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private JpaRequestRepository requestRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaItemRepository itemRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PaginationValidator paginationValidator;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User owner;

    private User requestor;

    private Item item;

    private Request request;

    private RequestDto requestDto;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setId(1);

        requestor = new User();
        requestor.setId(2);

        item = new Item();
        item.setId(1);
        item.setOwner(owner);
        item.setAvailable(true);

        request = new Request();
        request.setId(1);
        request.setDescription("request description");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription("request description");
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setItems(Collections.emptyList());
    }


    @Test
    void addRequest_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.add(requestor.getId(), requestDto));
    }

    @Test
    void addRequest_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(modelMapper.map(any(RequestDto.class), any(Class.class))).thenReturn(request);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        Request result = requestService.add(requestor.getId(), requestDto);

        assertNotNull(result);
        assertEquals(request, result);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void getRequestsSent_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getRequestsSent(requestor.getId()));
    }

    @Test
    void getRequestsSent_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(anyInt()))
                .thenReturn(Collections.singletonList(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        RequestDto mappedRequestDto = new RequestDto();
        mappedRequestDto.setId(request.getId());
        mappedRequestDto.setDescription(request.getDescription());
        mappedRequestDto.setCreated(request.getCreated());
        mappedRequestDto.setItems(Collections.emptyList());

        when(modelMapper.map(any(Request.class), eq(RequestDto.class))).thenReturn(mappedRequestDto);

        List<RequestDto> result = requestService.getRequestsSent(requestor.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
        verify(requestRepository).findByRequestorIdOrderByCreatedDesc(anyInt());
    }

    @Test
    void getRequest_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequest(request.getId(), requestor.getId()));
    }

    @Test
    void getRequest_RequestNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequest(request.getId(), requestor.getId()));
    }

    @Test
    void getRequest_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());
        when(modelMapper.map(any(Request.class), eq(RequestDto.class))).thenReturn(requestDto);

        RequestDto result = requestService.getRequest(request.getId(), requestor.getId());

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        verify(requestRepository).findById(anyInt());
    }

    @Test
    void getRequests_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequests(0, 10, requestor.getId()));
    }

    @Test
    void getRequests_InvalidParameters_FromAndSizeZero() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        doThrow(new BadRequestException(Item.class, "0", "Некорректные параметры пагинации"))
                .when(paginationValidator).validateSearchParameters(eq(0), eq(0));

        assertThrows(BadRequestException.class, () -> requestService.getRequests(0, 0, requestor.getId()));
    }

    @Test
    void getRequests_InvalidParameters_FromNegative() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        doThrow(new BadRequestException(Item.class, "-1", "Некорректные параметры пагинации"))
                .when(paginationValidator).validateSearchParameters(eq(-1), anyInt());

        assertThrows(BadRequestException.class, () -> requestService.getRequests(-1, 10, requestor.getId()));
    }

    @Test
    void getRequests_InvalidParameters_SizeNegative() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        doThrow(new BadRequestException(Item.class, "-1", "Некорректные параметры пагинации"))
                .when(paginationValidator).validateSearchParameters(anyInt(), eq(-1));

        assertThrows(BadRequestException.class, () -> requestService.getRequests(0, -1, requestor.getId()));
    }

    @Test
    void getRequests_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestorIdNot(anyInt(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(request)));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        RequestDto mappedRequestDto = new RequestDto();
        mappedRequestDto.setId(request.getId());
        mappedRequestDto.setDescription(request.getDescription());
        mappedRequestDto.setCreated(request.getCreated());
        mappedRequestDto.setItems(Collections.emptyList());

        when(modelMapper.map(any(Request.class), eq(RequestDto.class))).thenReturn(mappedRequestDto);

        List<RequestDto> result = requestService.getRequests(0, 10, requestor.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
        verify(requestRepository).findAllByRequestorIdNot(anyInt(), any(Pageable.class));
    }

}