package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.function.Function;

public class CommentToDtoMapper implements Function<Comment, CommentDto> {
    @Override
    public CommentDto apply(Comment comment) {
        ItemDto itemDto = new ItemDto(comment.getItem().getId(), comment.getItem().getName(), comment.getItem().getDescription(), comment.getItem().getAvailable());

        UserDto userDto = new UserDto(comment.getAuthor().getId(), comment.getAuthor().getName(), comment.getAuthor().getEmail());

        return new CommentDto(comment.getId(), comment.getAuthor().getName(), comment.getText(), itemDto, userDto, comment.getDateCreated());
    }
}
