package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.function.Function;

public class CommentToDtoMapper implements Function<Comment, CommentDto> {
    @Override
    public CommentDto apply(Comment comment) {
        if (comment == null) {
            return null;
        }

        Item item = comment.getItem();
        User author = comment.getAuthor();

        ItemDto itemDto = (item != null) ? new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable()) : null;

        UserDto userDto = (author != null) ? new UserDto(author.getId(), author.getName(), author.getEmail()) : null;

        return new CommentDto(comment.getId(), author != null ? author.getName() : null, comment.getText(), itemDto, userDto, comment.getDateCreated());
    }

}
