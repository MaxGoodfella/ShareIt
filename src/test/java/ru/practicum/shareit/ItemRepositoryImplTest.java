package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRepositoryImplTest {

    private static ItemRepositoryImpl itemRepository;
    private static UserRepositoryImpl userRepository;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        itemRepository = new ItemRepositoryImpl();
        userRepository = new UserRepositoryImpl();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    public void testSave() {
        User user1 = new User("user1", "user1@gmail.com");
        User savedUser1 = userRepository.save(user1);
        Item newItem = new Item("item", "item_description", true);

        assertDoesNotThrow(() -> itemRepository.add(savedUser1.getId(), newItem));
    }

    @Test
    public void testSaveWithNoAvailabilityStatus() {
        Item newItem = new Item("item", "item_description");

        Set<ConstraintViolation<Item>> violations = validator.validate(newItem);
        assertFalse(violations.isEmpty(), "Отсутствует статус доступности");
    }

    @Test
    public void testSaveWithNoName() {
        Item newItem = new Item("item_description", true);

        Set<ConstraintViolation<Item>> violations = validator.validate(newItem);
        assertFalse(violations.isEmpty(), "Отсутствует описание");
    }

    @Test
    public void testSaveWithSameNameAndDescription_ShouldThrowEntityAlreadyExistsException() {
        User user1 = new User("user1", "user1@gmail.com");
        User savedUser1 = userRepository.save(user1);

        Item newItem1 = new Item("item", "item_description", false);
        Item newItem2 = new Item("item", "item_description", true);

        itemRepository.add(savedUser1.getId(), newItem1);

        assertThrows(EntityAlreadyExistsException.class, () -> itemRepository.add(savedUser1.getId(), newItem2));
    }

    @Test
    public void testUpdateExistingItem() {
        User user1 = new User("user1", "user1@gmail.com");
        User savedUser1 = userRepository.save(user1);

        Item existingItem1 = new Item("item1", "item1_description", false);
        itemRepository.add(savedUser1.getId(), existingItem1);

        Item updatedItem1 = new Item("item1_updated", "item1_description_updated", true);
        updatedItem1.setId(existingItem1.getId());
        Item resultItem1 = itemRepository.update(updatedItem1);


        assertEquals(updatedItem1, resultItem1, "Информация должна быть обновлена");
        assertEquals("item1_updated", resultItem1.getName(), "Название не совпадает");
        assertEquals("item1_description_updated", resultItem1.getDescription(), "Описание не совпадает");
        assertEquals(true, resultItem1.getAvailable(), "Статус доступности не совпадает");
    }

    @Test
    public void testUpdateUnknownItem_shouldThrowEntityNotFoundException() {
        User user1 = new User("user1", "user1@gmail.com");
        User savedUser1 = userRepository.save(user1);

        Item existingItem = new Item("item1", "item1_description", false);
        itemRepository.add(savedUser1.getId(), existingItem);

        Item updatedItem = new Item("item1updated", "item2updated_description", false);
        updatedItem.setId(2);

        assertThrows(EntityNotFoundException.class, () -> itemRepository.update(updatedItem));
    }

    @Test
    public void testGetItems() {
        User user1 = new User("user1", "user1@gmail.com");
        User savedUser1 = userRepository.save(user1);

        Item newItem1 = new Item("item1", "item1_description", false);
        Item newItem2 = new Item("item2", "item2_description", false);

        Item savedItem1 = itemRepository.add(savedUser1.getId(), newItem1);
        Item savedItem2 = itemRepository.add(savedUser1.getId(), newItem2);
        savedItem1.setOwner(savedUser1);
        savedItem2.setOwner(savedUser1);

        System.out.println(savedItem1);
        System.out.println(savedItem2);

        List<Item> userItems = itemRepository.getItems(savedUser1.getId());

        assertEquals(2, userItems.size(), "Количество элементов не совпадает");
        assertEquals(savedItem1, userItems.get(0), "Первый элемент не совпадает");
        assertEquals(savedItem2, userItems.get(1), "Второй элемент не совпадает");
    }

    @Test
    public void testGetItem() {
        User user1 = new User("user1", "user1@gmail.com");
        User savedUser1 = userRepository.save(user1);

        Item newItem1 = new Item("item1", "item1_description", false);
        Item newItem2 = new Item("item2", "item2_description", false);

        Item savedItem1 = itemRepository.add(savedUser1.getId(), newItem1);
        Item savedItem2 = itemRepository.add(savedUser1.getId(), newItem2);

        assertEquals(savedItem1, itemRepository.getItem(savedItem1.getId()),
                "Пользователь 1 найден");
        assertEquals(savedItem2, itemRepository.getItem(savedItem2.getId()),
                "Пользователь 2 найден");
    }

    @Test
    public void testGetItem_shouldThrowEntityNotFoundException() {
        Item newItem1 = new Item("item1", "item1_description", false);
        Item newItem2 = new Item("item2", "item2_description", false);

        assertThrows(EntityNotFoundException.class, () -> itemRepository.getItem(newItem1.getId()),
                "Пользователь 1 не найден");
        assertThrows(EntityNotFoundException.class, () -> itemRepository.getItem(newItem2.getId()),
                "Пользователь 2 не найден");
    }

    @Test
    public void testSearch() {
        User user1 = new User("user1", "user1@gmail.com");
        User user2 = new User("user2", "user2@gmail.com");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Item newItem1 = new Item("item1", "item1_description", true);
        Item newItem2 = new Item("item2", "item2_description", false);
        Item newItem3 = new Item("item3", "item3_description", false);
        Item newItem4 = new Item("item4", "item4_description", true);

        Item savedItem1 = itemRepository.add(savedUser1.getId(), newItem1);
        Item savedItem2 = itemRepository.add(savedUser1.getId(), newItem2);
        Item savedItem3 = itemRepository.add(savedUser2.getId(), newItem3);
        Item savedItem4 = itemRepository.add(savedUser2.getId(), newItem4);
        savedItem1.setOwner(savedUser1);
        savedItem2.setOwner(savedUser1);
        savedItem3.setOwner(savedUser1);
        savedItem4.setOwner(savedUser1);

        List<Item> matchingItems = itemRepository.search("iTe");

        assertEquals(2, matchingItems.size(), "Количество элементов не совпадает");
        assertEquals(savedItem1, matchingItems.get(0), "Первый элемент не совпадает");
        assertEquals(savedItem4, matchingItems.get(1), "Второй элемент не совпадает");
    }

    @Test
    public void testSearchEmptyQuery() {
        User user1 = new User("user1", "user1@gmail.com");
        User user2 = new User("user2", "user2@gmail.com");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Item newItem1 = new Item("item1", "item1_description", true);
        Item newItem2 = new Item("item2", "item2_description", false);
        Item newItem3 = new Item("item3", "item3_description", false);
        Item newItem4 = new Item("item4", "item4_description", true);

        Item savedItem1 = itemRepository.add(savedUser1.getId(), newItem1);
        Item savedItem2 = itemRepository.add(savedUser1.getId(), newItem2);
        Item savedItem3 = itemRepository.add(savedUser2.getId(), newItem3);
        Item savedItem4 = itemRepository.add(savedUser2.getId(), newItem4);
        savedItem1.setOwner(savedUser1);
        savedItem2.setOwner(savedUser1);
        savedItem3.setOwner(savedUser1);
        savedItem4.setOwner(savedUser1);

        List<Item> matchingItems = itemRepository.search("");

        assertEquals(0, matchingItems.size(), "Количество элементов не совпадает");
    }

}