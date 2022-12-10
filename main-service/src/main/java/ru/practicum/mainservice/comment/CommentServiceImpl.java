package ru.practicum.mainservice.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comment.model.CommentRequestDto;
import ru.practicum.mainservice.comment.model.CommentResponseDto;
import ru.practicum.mainservice.error.ForbiddenException;
import ru.practicum.mainservice.error.NotFoundException;
import ru.practicum.mainservice.event.EventRepository;
import ru.practicum.mainservice.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.comment.CommentMapper.toComment;
import static ru.practicum.mainservice.comment.CommentMapper.toCommentDto;
import static ru.practicum.mainservice.comment.CommentStatus.AWAITING_MODERATION;
import static ru.practicum.mainservice.comment.CommentStatus.PUBLISHED;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public CommentResponseDto createComment(Long userId, Long eventId, CommentRequestDto commentDto) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        var comment = commentRepository.save(toComment(commentDto, user, event));
        return toCommentDto(comment);
    }

    @Override
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentDto) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not exist"));

        if (!comment.getAuthor().equals(user)) {
            throw new ForbiddenException("Only author can edit comment");
        }

        comment.setText(commentDto.getText());
        comment.setStatus(AWAITING_MODERATION);
        comment.setEdited(true);

        return toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponseDto> getCommentsForModeration(PageRequest pageRequest) {
        return commentRepository.findAllByStatus(AWAITING_MODERATION, pageRequest)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void changeCommentStatus(Long commentId, CommentStatus status) {
        var comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not exist"));
        if (AWAITING_MODERATION != comment.getStatus()) {
            throw new ForbiddenException("Comment should be in awaiting moderation status");
        }
        comment.setStatus(status);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentResponseDto> getEventComments(Long eventId, PageRequest pageRequest) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        return commentRepository.findAllByStatusAndEvent_Id(PUBLISHED, eventId, pageRequest)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
