package ru.practicum.mainservice.error;

/**
 * Описание ошибок в приложении
 */
public enum Errors {

    /**
     * Ошибки связанные с событиями
     */
    EVENT_NOT_EXIST("Event not exist"),
    EVENT_SHOULD_BE_PUBLISHED("Only pending or canceled events can be changed"),
    EVENT_TIME_SHOULD_NOT_BE_EARLIER_THAN("Event time should not be earlier than: "),

    /**
     * Ошибки связанные с пользователями
     */
    USER_NOT_EXIST("User not exist"),
    USER_ALREADY_EXIST("The user with the specified name already exists"),

    /**
     * Ошибки связанные с категориями
     */
    CATEGORY_NOT_EXIST("Category not exist"),
    CATEGORY_ALREADY_EXIST("The category with the specified name already exists"),

    /**
     * Ошибки связанные с запросами
     */
    REQUEST_NOT_EXIST("Request not exist"),
    ONLY_ONE_REQUEST_AVAILABLE("Only one request to event available"),
    INITIATOR_CANNOT_REQUEST_OWN_EVENT("Initiator can't request specified event"),
    PARTICIPATION_LIMIT_EXCEEDED("The number of participants has been exceeded"),

    /**
     * Ошибки связанные с подборками
     */
    COMPILATION_NOT_FOUND("Compilation not found"),

    /**
     * Прочие ошибки
     */
    FORBIDDEN_MESSAGE("For the requested operation the conditions are not met");


    private final String message;

    Errors(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
