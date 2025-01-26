package ru.alexds.ccoshop.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.RatingDTO;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.RatingRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления рейтингами.
 * Обеспечивает API для сохранения, получения и вычисления среднего рейтинга.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository; // Репозиторий для хранения рейтингов

    /**
     * Сохраняет новый рейтинг.
     *
     * @param ratingDTO DTO объект с информацией о новом рейтинге (идентификатор пользователя, идентификатор товара и значение рейтинга)
     * @return Сохраненный рейтинг
     */
    public Rating saveRating(RatingDTO ratingDTO) {
        log.debug("Request to save new rating: {}", ratingDTO);
        Rating rating = new Rating(null, ratingDTO.getUserId(), ratingDTO.getItemId(), ratingDTO.getRating(), System.currentTimeMillis());
        return ratingRepository.save(rating);
    }

    /**
     * Получает все рейтинги для указанного пользователя.
     *
     * @param userId Идентификатор пользователя, чьи рейтинги необходимо получить
     * @return Список рейтингов в формате DTO
     */
    public List<RatingDTO> getRatingsByUser(Long userId) {
        log.debug("Request to get ratings by user ID: {}", userId);
        return ratingRepository.findByUserId(userId)
                .stream()
                .map(r -> new RatingDTO(r.getUserId(), r.getItemId(), r.getRating()))
                .collect(Collectors.toList());
    }

    /**
     * Получает все рейтинги для указанного товара.
     *
     * @param itemId Идентификатор товара, чьи рейтинги необходимо получить
     * @return Список рейтингов в формате DTO
     */
    public List<RatingDTO> getRatingsByItem(Long itemId) {
        log.debug("Request to get ratings by item ID: {}", itemId);
        return ratingRepository.findByItemId(itemId)
                .stream()
                .map(r -> new RatingDTO(r.getUserId(), r.getItemId(), r.getRating()))
                .collect(Collectors.toList());
    }

    /**
     * Вычисляет средний рейтинг для списка рейтингов по товару.
     *
     * @param ratingDTOList Список рейтингов для конкретного товара
     * @return Среднее значение рейтинга или 0.0, если список пустой или null
     */
    public Double getAverageRatingByItem(List<RatingDTO> ratingDTOList) {
        log.debug("Request to calculate average rating for item with ratings: {}", ratingDTOList);
        // Проверяем, что список не пустой или null
        if (ratingDTOList == null || ratingDTOList.isEmpty()) {
            log.warn("No ratings found for the given list");
            return 0.0; // Возвращаем 0.0, если нет никаких рейтингов
        }
        // Считаем средний рейтинг
        Double averageRating = ratingDTOList.stream()
                .mapToDouble(RatingDTO::getRating) // Преобразуем каждый объект RatingDTO в значение рейтинга
                .average() // Вычисляем среднее
                .orElse(0.0); // Возвращаем 0.0, если среднее невозможно посчитать
        log.debug("Calculated average rating: {}", averageRating);
        return averageRating;
    }
}