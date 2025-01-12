package ru.alexds.ccoshop.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс для представления ошибок в API
 */
@Data
@AllArgsConstructor
class ErrorResponse {
    private int status;
    private String code;
    private String message;
}
