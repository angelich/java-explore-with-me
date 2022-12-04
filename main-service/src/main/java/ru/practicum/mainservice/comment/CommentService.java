package ru.practicum.mainservice.comment;

import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.comment.model.CommentDto;

import java.util.List;

/**
 * Сервис для работы с комментариями
 */
public interface CommentService {
    /**
     * Создание комментария к событию
     *
     * @param userId     Идентификатор пользователя
     * @param eventId    Идентификатор события
     * @param commentDto Тело запроса на создание комментария
     * @return Созданный комментарий
     */
    CommentDto createComment(Long userId, Long eventId, CommentDto commentDto);

    /**
     * @param pageRequest Параметр пагинации
     * @return Список комментариев требующих модерации
     */
    List<CommentDto> getCommentsForModeration(PageRequest pageRequest);

    /**
     * Публикация комментария админом
     *
     * @param commentId Идентификатор комментария
     */
    void publishComment(Long commentId);

    /**
     * Отклонение комментария админом
     *
     * @param commentId Идентификатор комментария
     */
    void rejectComment(Long commentId);

    /**
     * @param eventId     Идентификатор события
     * @param pageRequest Параметр пагинации
     * @return Список комментариев конкретного события
     */
    List<CommentDto> getEventComments(Long eventId, PageRequest pageRequest);
}
