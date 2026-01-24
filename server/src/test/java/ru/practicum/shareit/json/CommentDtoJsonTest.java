package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentToDtoMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.config.TestConfig;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = TestConfig.class)
public class CommentDtoJsonTest {
    @Autowired
    private CommentToDtoMapper commentMapper;

    @Test
    void commentMapping() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment text");

        CommentDto commentDto = commentMapper.apply(comment);

        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
    }
}
