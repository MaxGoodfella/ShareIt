package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

// @NamedEntityGraph(name = "Booking.itemAndUser", attributeNodes = { @NamedAttributeNode("item"), @NamedAttributeNode("booker")})

@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking implements Comparable<Booking> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    // @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.EAGER) // eager - подгрузить всю сущность, lazy - только айдишник
    @JoinColumn(name = "item_id") // предпочтительней делать Lazy - как закончишь проект, проверь как с ним работает сначала
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id")
    private User booker;

    @Column(name = "status")
    private BookingState status;

    @JsonIgnore
    @Transient
    private BookingTimeState bookingTimeState;


    @Override
    public int compareTo(Booking booking) {
        return booking.getEnd().compareTo(this.end);
    }

}