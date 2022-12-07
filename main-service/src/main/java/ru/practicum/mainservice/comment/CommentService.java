package ru.practicum.mainservice.comment;

import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.comment.model.CommentDto;

import javax.validation.constraints.NotNull;
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
  @NotNull CommentDto createComment(Long userId, Long eventId, @NotNull CommentDto commentDto);

  /**
   * @param pageRequest Параметр пагинации
   * @return Список комментариев требующих модерации
   */
  @NotNull List<CommentDto> getCommentsForModeration(PageRequest pageRequest);

  /**
   * Изменение статуса комментария админом
   *
   * @param commentId Идентификатор комментария
   */
  void changeCommentStatus(Long commentId, CommentStatus status);

  /**
   * @param eventId     Идентификатор события
   * @param pageRequest Параметр пагинации
   * @return Список комментариев конкретного события
   */
  @NotNull List<CommentDto> getEventComments(Long eventId, PageRequest pageRequest);
}
