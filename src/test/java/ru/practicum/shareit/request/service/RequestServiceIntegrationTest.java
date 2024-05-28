package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.username=postgres",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RequestServiceIntegrationTest {

    private final EntityManager em;

    private final RequestService service;


    @Test
    void add() {
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        em.flush();

        LocalDateTime now = LocalDateTime.now().withNano(0);

        RequestDto requestDto = RequestDto.builder()
                .description("request description")
                .created(now)
                .items(Collections.emptyList())
                .build();

        Request savedRequest = service.add(owner.getId(), requestDto);

        TypedQuery<Request> query = em.createQuery("SELECT r FROM Request r WHERE r.id = :id", Request.class);
        Request foundRequest = query.setParameter("id", savedRequest.getId())
                .getSingleResult();

        assertNotNull(foundRequest.getId());
        assertEquals(requestDto.getDescription(), foundRequest.getDescription());
        assertEquals(requestDto.getCreated(), foundRequest.getCreated().withNano(0));
    }

    @Test
    void getRequestsSent() {
        User user = new User(null, "user", "user@email.com");
        em.persist(user);
        em.flush();

        RequestDto request1 = RequestDto.builder()
                .description("request description 1")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
        RequestDto request2 = RequestDto.builder()
                .description("request description 2")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        service.add(user.getId(), request1);
        service.add(user.getId(), request2);

        List<RequestDto> requestsSent = service.getRequestsSent(user.getId());

        assertNotNull(requestsSent);
        assertEquals(2, requestsSent.size());
        assertEquals("request description 2", requestsSent.get(0).getDescription());
        assertEquals("request description 1", requestsSent.get(1).getDescription());
    }

    @Test
    void getRequest() {
        User user = new User(null, "user", "user@email.com");
        em.persist(user);
        em.flush();

        RequestDto requestDto = RequestDto.builder()
                .description("request description")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
        Request savedRequest = service.add(user.getId(), requestDto);

        RequestDto foundRequestDto = service.getRequest(savedRequest.getId(), user.getId());

        assertNotNull(foundRequestDto);
        assertEquals("request description", foundRequestDto.getDescription());
    }

    @Test
    void getRequests() {
        User user1 = new User(null, "user1", "user1@email.com");
        em.persist(user1);
        em.flush();

        User user2 = new User(null, "user2", "user2@email.com");
        em.persist(user2);
        em.flush();

        RequestDto request1 = RequestDto.builder()
                .description("request description 1")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
        RequestDto request2 = RequestDto.builder()
                .description("request description 2")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        service.add(user1.getId(), request1);
        service.add(user2.getId(), request2);

        List<RequestDto> requests = service.getRequests(0, 10, user1.getId());

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals("request description 2", requests.get(0).getDescription());
    }

}