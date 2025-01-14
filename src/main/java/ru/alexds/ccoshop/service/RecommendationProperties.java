package ru.alexds.ccoshop.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "recommendation")
@Getter
@Setter
public class RecommendationProperties {
    private String ratingsTable = "ratings";
    private String userIdColumn = "user_id";
    private String itemIdColumn = "item_id";
    private String ratingColumn = "rating";
    private String timestampColumn = "timestamp";
}
