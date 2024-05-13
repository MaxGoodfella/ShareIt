package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Email;


@DynamicUpdate
@Data
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
// @Table(name = "USERS", schema = "PUBLIC", uniqueConstraints = @UniqueConstraint(columnNames = "EMAIL"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(String name) {
        this.name = name;
    }

}