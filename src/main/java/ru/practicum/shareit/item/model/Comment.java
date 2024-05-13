package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.EAGER) // eager - подгрузить всю сущность, lazy - только айдишник
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    // @NotNull
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    public Comment(String text, Item item, User author) {
        this.text = text;
        this.item = item;
        this.author = author;
    }

}