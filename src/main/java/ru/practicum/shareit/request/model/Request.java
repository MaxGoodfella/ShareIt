package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@DynamicUpdate
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    private User requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

}