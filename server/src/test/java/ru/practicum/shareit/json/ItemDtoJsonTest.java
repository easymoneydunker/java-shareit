package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.config.TestConfig;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = TestConfig.class)
public class ItemDtoJsonTest {
    @Autowired
    private ItemMapper itemMapper;

    @Test
    void longMapping() {
        Long id = 1L;
        ItemRequest mapped = itemMapper.map(1L);

        assertThat(Objects.nonNull(mapped));
    }

    @Test
    void itemMapping() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertThat(Objects.equals(itemDto.getId(), item.getId()));
        assertThat(Objects.equals(itemDto.getName(), item.getName()));

        Item mapped = itemMapper.toItem(itemDto);
        assertThat(Objects.equals(mapped.getId(), item.getId()));
        assertThat(Objects.equals(mapped.getName(), item.getName()));
    }

    @Test
    void requestMapping() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Test Request");

        ItemRequest mapped = itemMapper.map(1L);

        assertThat(Objects.equals(mapped.getId(), request.getId()));
        assertThat(Objects.equals(mapped.getDescription(), request.getDescription()));
    }
}
