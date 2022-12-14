package ru.practicum.mainservice.comment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.mainservice.comment.CommentStatus;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "comment")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String text;

    @CreationTimestamp
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "author")
    private User author;

    @Enumerated(EnumType.ORDINAL)
    private CommentStatus status;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private boolean edited;
}
