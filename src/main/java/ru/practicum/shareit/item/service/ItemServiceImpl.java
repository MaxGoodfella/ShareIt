package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Item add(Integer userId, ItemDto itemDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        Item newItem = modelMapper.map(itemDto, Item.class);

        if (itemRepository.findByNameAndDescription(newItem.getName(), newItem.getDescription()).isPresent()) {
            throw new EntityAlreadyExistsException(Item.class, "Item с названием '" +
                    newItem.getName() + "' и описанием '" + newItem.getDescription() + "' уже зарегистрирован.");
        }

        newItem.setOwner(user);

        return itemRepository.save(newItem);
    }

    @Override
    public Item update(Integer userId, Integer itemId, ItemDto itemDto) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class,
                        "Вещь с id " + itemId + " не найдена."));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(Integer.class,
                    "Пользователь с id = " + userId +  " не имеет права обновлять эту вещь.");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(existingItem.getAvailable())) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.save(existingItem);

    }

    @Override
    public Item getItem(Integer itemId) { // работает
        Optional<Item> item = itemRepository.findById(itemId);
        return item.orElseThrow(() -> new EntityNotFoundException(Item.class,
                "Вещь с id " + itemId + " не найдена."));
    }

    @Override
    public List<Item> getItems(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByNameAndDescription(text.toLowerCase());

        // return itemRepository.findByAvailableIsTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
    }

}