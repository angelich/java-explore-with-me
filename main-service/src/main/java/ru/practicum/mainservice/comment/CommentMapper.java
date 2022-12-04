package ru.practicum.mainservice.comment;

import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.model.CommentDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.model.User;

import static ru.practicum.mainservice.comment.CommentStatus.AWAITING_MODERATION;

public class CommentMapper {

    public static Comment toComment(CommentDto dto, User author, Event event) {
        return Comment.builder()
                .text(dto.getText())
                .status(AWAITING_MODERATION)
                .author(author)
                .event(event)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .author(comment.getAuthor())
                .build();
    }
}
