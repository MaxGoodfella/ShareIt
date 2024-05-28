package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.username=postgres",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceIntegrationTest {

    private final EntityManager em;

    private final UserService service;


    @Test
    void save() {
        User user = new User(1, "username", "user@email.com");

        service.save(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User savedUser = query.setParameter("email", user.getEmail())
                .getSingleResult();

        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(user.getName()));
        assertThat(savedUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void update() {
        User sourceUser = new User("username", "user@email.com");
        em.persist(sourceUser);
        em.flush();

        User updatedUser = new User("newUsername", "newuser@email.com");

        service.update(sourceUser.getId(), updatedUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User foundUser = query.setParameter("id", sourceUser.getId()).getSingleResult();

        assertThat(foundUser.getName(), equalTo(updatedUser.getName()));
        assertThat(foundUser.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    void findById() {
        User sourceUser = new User("username", "user@email.com");

        em.persist(sourceUser);
        em.flush();

        User targetUser = service.findById(sourceUser.getId());

        assertThat(targetUser, notNullValue());
        assertThat(targetUser.getName(), equalTo(sourceUser.getName()));
        assertThat(targetUser.getEmail(), equalTo(sourceUser.getEmail()));
    }

    @Test
    void findAll() {
        List<User> sourceUsers = List.of(
                new User("Ivan", "ivan@email"),
                new User("Petr", "petr@email"),
                new User("Vasilii", "vasilii@email")
        );

        for (User user : sourceUsers) {
            em.persist(user);
        }
        em.flush();

        List<User> targetUsers = service.findAll();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (User sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem( allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void deleteById() {
        User sourceUser = new User("username", "user@email.com");
        em.persist(sourceUser);
        em.flush();

        service.deleteById(sourceUser.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        List<User> foundUsers = query.setParameter("id", sourceUser.getId()).getResultList();

        assertThat(foundUsers, hasSize(0));
    }

}