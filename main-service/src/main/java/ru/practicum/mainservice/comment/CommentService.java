package ru.practicum.mainservice.comment;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.comment.model.CommentRequestDto;
import ru.practicum.mainservice.comment.model.CommentResponseDto;

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
  @NotNull CommentResponseDto createComment(Long userId, Long eventId, @NotNull CommentRequestDto commentDto);

  /**
   * Обновление комментария к событию
   *
   * @param userId     Идентификатор пользователя
   * @param commentId  Идентификатор комментария
   * @param commentDto Тело запроса на создание комментария
   * @return Обновленный комментарий
   */
  @NotNull CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentDto);

  /**
   * @param pageRequest Параметр пагинации
   * @return Список комментариев требующих модерации
   */
  @NotNull List<CommentResponseDto> getCommentsForModeration(PageRequest pageRequest);

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
  @NotNull List<CommentResponseDto> getEventComments(Long eventId, PageRequest pageRequest);
}
