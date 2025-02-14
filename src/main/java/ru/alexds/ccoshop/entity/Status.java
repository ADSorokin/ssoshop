/**
 * Пакет для сущностей приложения.
 */
package ru.alexds.ccoshop.entity;

/**
 * Перечисление Status представляет собой различные статусы заказа в системе.
 * Статусы описывают текущее состояние заказа, начиная от его создания и заканчивая завершением или отменой.
 */
public enum Status {
    /**
     * Новый заказ. Заказ был создан, но еще не обработан.
     */
    NEW,

    /**
     * Оплаченный заказ. Клиент произвел оплату за заказ.
     */
    PAID,

    /**
     * Отправленный заказ. Заказ отправлен со склада и находится в пути к клиенту.
     */
    SHIPPED,

    /**
     * Доставленный заказ. Заказ доставлен клиенту.
     */
    DELIVERED,

    /**
     * Отмененный заказ. Заказ был отменен либо клиентом, либо администратором системы.
     */
    CANCELLED,

    /**
     * Завершенный заказ. Заказ успешно завершен и больше не требует действий.
     */
    COMPLETED
}
