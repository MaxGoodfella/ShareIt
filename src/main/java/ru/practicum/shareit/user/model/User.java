package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

//@Data
//@NoArgsConstructor
//public class User {
//
//    private Integer id;
//
//    @NotBlank
//    private String name;
//
//    @NotBlank
//    @Email
//    private String email;
//
//    public User(String name) {
//        this.name = name;
//    }
//
//
//
//    public User(String name, String email) {
//        this.name = name;
//        this.email = email;
//    }
//}

@Data
@NoArgsConstructor
public class User {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name) {
        this.name = name;
    }
}
