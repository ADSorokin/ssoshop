/**
 * Пакет для сущностей приложения.
 */
package ru.alexds.ccoshop.entity;

/**
 * Перечисление Role представляет собой различные роли пользователей в системе.
 * Роли определяют уровень доступа и привилегии пользователя в системе.
 */
public enum Role {
    /**
     * Роль обычного пользователя. Обычные пользователи имеют ограниченный доступ к функциям системы.
     * Они могут просматривать каталог товаров, создавать заказы и управлять своими профилями.
     */
    USER,

    /**
     * Роль администратора. Администраторы имеют полный доступ ко всем функциям системы.
     * Они могут управлять каталогом товаров, обрабатывать заказы, управлять пользователями и выполнять другие административные задачи.
     */
    ADMIN;
}