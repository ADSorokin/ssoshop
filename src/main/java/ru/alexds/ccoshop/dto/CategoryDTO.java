package ru.alexds.ccoshop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alexds.ccoshop.entity.Category;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    @NotEmpty(message = "Category name cannot be empty")
    private String name;
    private List<Long> productIds;  // IDs продуктов для добавления в категорию


}
