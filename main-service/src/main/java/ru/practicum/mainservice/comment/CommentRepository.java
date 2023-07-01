package ru.practicum.mainservice.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.comment.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByStatus(CommentStatus status, PageRequest pageRequest);

    Page<Comment> findAllByStatusAndEvent_Id(CommentStatus status, Long eventId, PageRequest pageRequest);
}
