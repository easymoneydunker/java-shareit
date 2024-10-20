package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.client.CommentClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @MockBean
    private CommentClient commentClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void getByUserId_ShouldReturnUserItems() throws Exception {
        when(itemClient.getItemsByUserId(1L)).thenReturn(ResponseEntity.ok().body(new Object[]{}));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk());
    }

    @Test
    void getById_ShouldReturnItem() throws Exception {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");

        when(itemClient.getItemById(eq(1L), eq(itemId))).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(get("/items/{itemId}", itemId).header("X-Sharer-User-Id", 1L)).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void create_ShouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");

        when(itemClient.createItem(eq(1L), any(ItemDto.class))).thenReturn(ResponseEntity.status(201).body(itemDto));

        mockMvc.perform(post("/items").header("X-Sharer-User-Id", 1L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("New Item"));
    }

    @Test
    void deleteById_ShouldDeleteItem() throws Exception {
        long itemId = 1L;

        when(itemClient.deleteItemById(eq(1L), eq(itemId))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/items/{itemId}", itemId).header("X-Sharer-User-Id", 1L)).andExpect(status().isOk());
    }

    @Test
    void update_ShouldReturnUpdatedItem() throws Exception {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");

        when(itemClient.updateItem(eq(1L), eq(itemId), any(ItemDto.class))).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(patch("/items/{itemId}", itemId).header("X-Sharer-User-Id", 1L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    void search_ShouldReturnItems() throws Exception {
        when(itemClient.searchItems(eq("test"), eq(1L))).thenReturn(ResponseEntity.ok().body(new Object[]{}));

        mockMvc.perform(get("/items/search").header("X-Sharer-User-Id", 1L).param("text", "test")).andExpect(status().isOk());
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Nice item!");

        when(commentClient.addComment(eq(1L), eq(itemId), any(CommentDto.class))).thenReturn(ResponseEntity.ok(commentDto));

        mockMvc.perform(post("/items/{itemId}/comment", itemId).header("X-Sharer-User-Id", 1L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(commentDto))).andExpect(status().isOk()).andExpect(jsonPath("$.text").value("Nice item!"));
    }
}

