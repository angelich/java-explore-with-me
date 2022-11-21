package ru.practicum.mainservice.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.event.EventState;
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
import java.time.Instant;

@Getter
@Setter
@Builder
@Entity
@Table(name = "event")
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    private String annotation;
    private String description;

    @Column(nullable = false)
    private Instant eventDate;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false)
    private Boolean requestModeration;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant published;

    @Enumerated(EnumType.ORDINAL)
    private EventState state;

    private Double locationLat;
    private Double locationLon;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;

    private int participantLimit;
}
