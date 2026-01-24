package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.function.Function;

public class CommentToDtoMapper implements Function<Comment, CommentDto> {
    @Override
    public CommentDto apply(Comment comment) {
        if (comment == null) {
            return null;
        }

        User author = comment.getAuthor();

        String authorName = (author != null) ? author.getName() : null;

        return new CommentDto(
                comment.getId(),
                authorName,
                comment.getText(),
                comment.getDateCreated()
        );
    }
}
