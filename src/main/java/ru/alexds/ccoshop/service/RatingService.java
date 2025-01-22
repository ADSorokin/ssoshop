package ru.alexds.ccoshop.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alexds.ccoshop.dto.RatingDTO;
import ru.alexds.ccoshop.entity.Order;
import ru.alexds.ccoshop.entity.OrderItem;
import ru.alexds.ccoshop.entity.Rating;
import ru.alexds.ccoshop.repository.RatingRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    // Сохранить новый рейтинг
    public Rating saveRating(RatingDTO ratingDTO) {
        Rating rating = new Rating(null, ratingDTO.getUserId(), ratingDTO.getItemId(), ratingDTO.getRating(), System.currentTimeMillis());
        return ratingRepository.save(rating);
    }

    // Получить рейтинг пользователя
    public List<RatingDTO> getRatingsByUser(Long userId) {
        return ratingRepository.findByUserId(userId)
                .stream()
                .map(r -> new RatingDTO(r.getUserId(), r.getItemId(), r.getRating()))
                .collect(Collectors.toList());
    }


    public List<RatingDTO> getRatingsByItem(Long itemId) {
        return ratingRepository.findByItemId(itemId)
                .stream()
                .map(r -> new RatingDTO(r.getUserId(), r.getItemId(), r.getRating()))
                .collect(Collectors.toList());
    }

    public Double getAverageRatingByItem(List<RatingDTO> ratingDTOList) {
        // Проверяем, что список не пустой или null
        if (ratingDTOList == null || ratingDTOList.isEmpty()) {
            return 0.0; // Возвращаем 0.0, если нет никаких рейтингов
        }

        // Считаем средний рейтинг
        return ratingDTOList.stream()
                .mapToDouble(RatingDTO::getRating) // Преобразуем каждый объект RatingDTO в значение рейтинга
                .average() // Вычисляем среднее
                .orElse(0.0); // Возвращаем 0.0, если среднее невозможно посчитать
    }


}

