package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface JpaRequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByRequestorIdOrderByCreatedDesc(Integer userId);

    // List<RequestDto> findAllByRequestor_IdOrderByCreatedDesc(Integer requestor_id);
//
//    Page<Request> findAll(Pageable pageable);

    Page<Request> findAllByRequestorIdNot(Integer userId, Pageable pageable);


}