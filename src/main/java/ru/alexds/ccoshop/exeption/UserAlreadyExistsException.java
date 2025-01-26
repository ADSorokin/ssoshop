package ru.alexds.ccoshop.exeption;

/**
 * Исключение, выбрасываемое при попытке создания пользователя с уже существующим email.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Создает новый экземпляр исключения с указанным email.
     *
     * @param email email, который уже существует в системе
     */
    public UserAlreadyExistsException(String email) {
        super(String.format("Пользователь с email '%s' уже существует", email));
    }

    /**
     * Создает новый экземпляр исключения с пользовательским сообщением.
     *
     * @param message пользовательское сообщение об ошибке
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создает новый экземпляр исключения с причиной.
     *
     * @param cause причина исключения
     */
    public UserAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    /**
     * Создает новый экземпляр исключения с сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause причина исключения
     * @param enableSuppression включить подавление
     * @param writableStackTrace записывать stack trace
     */
    protected UserAlreadyExistsException(String message, Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

