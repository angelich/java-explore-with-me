package ru.practicum.mainservice.comment;


/**
 * Статусы жизненного цикла комментария
 */
public enum CommentStatus {
    /**
     * Ожидает модерации
     */
    AWAITING_MODERATION,

    /**
     * Опубликован
     */
    PUBLISHED,

    /**
     * Отклонен модератором
     */
    REJECTED
}
