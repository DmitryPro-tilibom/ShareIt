package ru.practicum.shareit.item.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment toComment(CommentDtoText commentDtoText, Item item, User author) {
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setText(commentDtoText.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}
