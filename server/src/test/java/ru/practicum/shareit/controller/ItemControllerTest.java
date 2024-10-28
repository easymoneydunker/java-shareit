package ru.practicum.shareit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @Mock
    private CommentService commentService;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemController itemController;

    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");

        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);
        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(item);
    }

    @Test
    void getById_ShouldReturnItemDto_WhenItemExists() {
        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemDto);

        ItemDto foundItemDto = itemController.getById(1L, 1L);

        assertNotNull(foundItemDto);
        assertEquals("Test Item", foundItemDto.getName());
        verify(itemService).findById(1L, 1L);
    }

    @Test
    void getByUserId_ShouldReturnListOfItemDtos() {
        List<ItemDto> items = List.of(itemDto);
        when(itemService.findItemsByUserId(anyLong())).thenReturn(items);

        List<ItemDto> result = itemController.getByUserId(1L).stream().toList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getName());
        verify(itemService).findItemsByUserId(1L);
    }

    @Test
    void create_ShouldReturnAddedItemDto() {
        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(itemDto);

        ItemDto addedItem = itemController.create(1L, itemDto);

        assertNotNull(addedItem);
        assertEquals("Test Item", addedItem.getName());
        verify(itemService).create(itemDto, 1L);
    }

    @Test
    void update_ShouldReturnUpdatedItemDto() {
        when(itemService.update(any(Item.class), anyLong(), anyLong())).thenReturn(itemDto);

        ItemDto updatedItem = itemController.update(1L, item, 1L);

        assertNotNull(updatedItem);
        assertEquals("Test Item", updatedItem.getName());
        verify(itemService).update(item, 1L, 1L);
    }

    @Test
    void addComment_ShouldReturnCommentDto() {
        when(commentService.create(any(Comment.class), anyLong(), anyLong())).thenReturn(commentDto);

        CommentDto addedComment = itemController.addComment(1L, 1L, comment);

        assertNotNull(addedComment);
        assertEquals("Great item!", addedComment.getText());
        verify(commentService).create(comment, 1L, 1L);
    }

    @Test
    void search_ShouldReturnListOfItemsContainingText() {
        List<ItemDto> items = List.of(itemDto);
        when(itemService.search(anyString())).thenReturn(items);

        List<ItemDto> result = itemController.search("test").stream().toList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.getFirst().getName());
        verify(itemService).search("test");
    }

    @Test
    void deleteItem_ShouldInvokeDeleteByIdInService() {
        doNothing().when(itemService).deleteById(anyLong());

        itemController.deleteById(1L);

        verify(itemService).deleteById(1L);
    }

    @Test
    void getAllItems_ShouldReturnListOfItemDtos() {
        // Arrange
        List<ItemDto> items = List.of(itemDto);
        when(itemService.findAll()).thenReturn(items);

        // Act
        Collection<ItemDto> result = itemController.getAllItems();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.iterator().next().getName());
        verify(itemService).findAll();
    }

}
