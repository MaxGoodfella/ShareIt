package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Item add(Integer userId, ItemDto itemDto) {
        User user = userRepository.findById(userId);

        Item newItem = modelMapper.map(itemDto, Item.class);

        newItem.setOwner(user);

        return itemRepository.add(userId, newItem);
    }

    @Override
    public Item update(Integer userId, Integer itemId, ItemDto itemDto) {
        User user = userRepository.findById(userId);
        Item existingItem = itemRepository.getItem(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(Integer.class,
                    "Пользователь с id = " + userId +  " не имеет права обновлять эту вещь.");
        }

        Item updatedItem = new Item();
        updatedItem.setId(itemId);

        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(existingItem.getAvailable())) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }

        updatedItem.setOwner(user);
        return itemRepository.update(updatedItem);
    }

    @Override
    public Item getItem(Integer itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<Item> getItems(Integer userId) {
        userRepository.findById(userId);
        return itemRepository.getItems(userId);
    }

    @Override
    public List<Item> search(String text) {
        return itemRepository.search(text);
    }

}