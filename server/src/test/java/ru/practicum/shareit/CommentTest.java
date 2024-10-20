package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();

        user = createUser("user@example.com", "User");
        item = createItem("Sample Item", "Sample item description", true, user);
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Comment createComment(String text, User author, Item item) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setDateCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Test
    void findById_ShouldReturnComment_WhenCommentExists() {
        Comment comment = createComment("Great item!", user, item);
        Optional<Comment> foundComment = commentRepository.findById(comment.getId());
        assertTrue(foundComment.isPresent());
        assertEquals("Great item!", foundComment.get().getText());
    }

    @Test
    void findByAuthorId_ShouldReturnCommentList_WhenCommentsExistForAuthor() {
        createComment("Great item!", user, item);
        Optional<Comment> comment = commentRepository.findByAuthorId(user.getId());
        assertTrue(comment.isPresent());
        assertEquals(user.getId(), comment.get().getAuthor().getId());
    }

    @Test
    void findByItemId_ShouldReturnCommentsForItem() {
        Comment comment1 = createComment("Great item!", user, item);
        Comment comment2 = createComment("Amazing!", user, item);

        List<Comment> comments = commentRepository.findByItemId(item.getId());
        assertEquals(2, comments.size());
        assertTrue(comments.stream().anyMatch(c -> c.getText().equals("Great item!")));
        assertTrue(comments.stream().anyMatch(c -> c.getText().equals("Amazing!")));
    }

    @Test
    void deleteById_ShouldRemoveComment_WhenCommentExists() {
        Comment comment = createComment("Great item!", user, item);
        commentRepository.deleteById(comment.getId());
        Optional<Comment> foundComment = commentRepository.findById(comment.getId());
        assertFalse(foundComment.isPresent());
    }
}
