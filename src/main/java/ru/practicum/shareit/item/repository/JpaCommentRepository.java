package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface JpaCommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByItemId(Integer itemId);

    List<Comment> findAllByItemIdIn(List<Integer> itemIds);
}
