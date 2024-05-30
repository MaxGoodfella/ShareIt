package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
@DataJpaTest
class JpaRequestRepositoryIntegrationTest {

    @Autowired
    private JpaRequestRepository requestRepository;

    @Autowired
    private JpaUserRepository userRepository;

    private User user1;

    private User user2;

    @BeforeEach
    public void addRequestsAndUsers() {

        user1 = userRepository.save(User
                .builder()
                .name("user1")
                .email("user1@email.com")
                .build());

        user2 = userRepository.save(User
                .builder()
                .name("user2")
                .email("user2@email.com")
                .build());

        requestRepository.save(Request
                .builder()
                .description("description1")
                .created(LocalDateTime.now())
                .requestor(user1)
                .build());

        requestRepository.save(Request
                .builder()
                .description("description2")
                .created(LocalDateTime.now().minusDays(1))
                .requestor(user1)
                .build());

        requestRepository.save(Request
                .builder()
                .description("description3")
                .created(LocalDateTime.now())
                .requestor(user2)
                .build());

    }

    @AfterEach
    public void removeRequests() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findByRequestorIdOrderByCreatedDesc() {
        List<Request> requestsList = requestRepository.findByRequestorIdOrderByCreatedDesc(user1.getId());

        assertThat(requestsList).hasSize(2);
        assertThat(requestsList.get(0).getCreated()).isAfter(requestsList.get(1).getCreated());
    }

    @Test
    void findAllByRequestorIdNot() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Request> requestsPage = requestRepository.findAllByRequestorIdNot(user1.getId(), pageable);

        List<Request> requestList = requestsPage.getContent();

        assertThat(requestList).hasSize(1);
        assertThat(requestList.get(0).getRequestor().getId()).isEqualTo(user2.getId());
        assertThat(requestList.get(0).getDescription()).isEqualTo("description3");
    }

}