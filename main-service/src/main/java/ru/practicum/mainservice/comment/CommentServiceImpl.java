package ru.practicum.mainservice.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comment.model.CommentDto;
import ru.practicum.mainservice.error.NotFoundException;
import ru.practicum.mainservice.event.EventRepository;
import ru.practicum.mainservice.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mainservice.comment.CommentMapper.toComment;
import static ru.practicum.mainservice.comment.CommentMapper.toCommentDto;
import static ru.practicum.mainservice.comment.CommentStatus.AWAITING_MODERATION;
import static ru.practicum.mainservice.comment.CommentStatus.PUBLISHED;
import static ru.practicum.mainservice.comment.CommentStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, CommentDto commentDto) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not exist"));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        var comment = commentRepository.save(toComment(commentDto, user, event));
        return toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsForModeration(PageRequest pageRequest) {
        return commentRepository.findAllByStatus(AWAITING_MODERATION, pageRequest)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void publishComment(Long commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not exist"));
        comment.setStatus(PUBLISHED);
        commentRepository.save(comment);
    }

    @Override
    public void rejectComment(Long commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment not exist"));
        comment.setStatus(REJECTED);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId, PageRequest pageRequest) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not exist"));
        return commentRepository.findAllByStatusAndEvent_Id(PUBLISHED, eventId, pageRequest)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
