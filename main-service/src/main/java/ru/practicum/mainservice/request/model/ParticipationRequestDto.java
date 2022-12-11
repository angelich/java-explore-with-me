package ru.practicum.mainservice.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.mainservice.request.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private LocalDateTime created;
    private Long requester;
    private RequestStatus status;
}
