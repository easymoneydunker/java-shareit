package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(long id);

    Optional<Comment> findByAuthorId(long authorId);

    List<Comment> findByItemId(long itemId);

    void deleteById(long id);
}
