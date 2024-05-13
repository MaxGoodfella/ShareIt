package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "item_id")
//    private Set<Comment> comments;


    public Item(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Item(String description, Boolean available) {
        this.description = description;
        this.available = available;
    }

    public Item(Boolean available) {
        this.available = available;
    }

//    @Getter
//    @Setter
//    @AllArgsConstructor
//    public static class CommentDto {
//
//        // private Integer id;
//
//        private String text;
//
//        @NotNull
//        private Integer itemId;
//
//        @NotNull
//        private Integer authorId;
//
//        private LocalDateTime created;
//
//    }

}