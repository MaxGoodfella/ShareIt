package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface JpaItemRepository extends JpaRepository<Item, Integer> {

    Optional<Item> findByNameAndDescription(String name, String description);

    List<Item> findByOwnerId(Integer userId);

    @Query("SELECT i FROM Item i WHERE i.available = true" +
            " AND (LOWER(i.name) LIKE %:text% OR LOWER(i.description) LIKE %:text%) ORDER BY i.id ASC")
    List<Item> searchByNameAndDescription(@Param("text") String text);

//    List<Item> findAllByRequestIdIn(List<Integer> requestIds);


    List<Item> findAllByRequestIdIn(List<Integer> requestIds);



    List<Item> findByOwnerId(Integer userId, Pageable page);


//    @Query("SELECT i FROM Item i WHERE i.available = true" +
//            " AND (LOWER(i.name) LIKE %:text% OR LOWER(i.description) LIKE %:text%) ORDER BY i.id ASC")
//    Page<Item> searchByNameAndDescription(@Param("text") String text, Pageable pageable);

//    List<Item> findAllByRequestId(Integer requestId);
}