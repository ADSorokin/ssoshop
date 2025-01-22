package ru.alexds.ccoshop.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexds.ccoshop.entity.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUserId(Long userId); // Найти все рейтинги, выставленные пользователем
    List<Rating> findByItemId(Long itemId); // Найти все рейтинги для определенного товара





}

