package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface JpaRequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByRequestorIdOrderByCreatedDesc(Integer userId);

    Page<Request> findAllByRequestorIdNot(Integer userId, Pageable pageable);

}