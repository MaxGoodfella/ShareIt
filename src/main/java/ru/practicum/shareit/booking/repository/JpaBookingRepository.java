package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Integer> {


    List<Booking> findBookingsByBooker_Id(Integer userId);


    List<Booking> findBookingsByItem_Owner_Id(Integer userId);

    List<Booking> findByItemIdAndBookerId(Integer itemId, Integer userId);


    // List<Booking> findByItemIdOrderByEndAsc(Integer itemId);

}

// @EntityGraph("Booking.itemAndUser")