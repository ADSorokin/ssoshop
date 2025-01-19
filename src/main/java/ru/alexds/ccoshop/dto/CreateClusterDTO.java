package ru.alexds.ccoshop.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClusterDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotEmpty(message = "Weights are required")
    private List<Double> weights;

    @NotNull(message = "Vigilance parameter is required")
    @Min(value = 0, message = "Vigilance must be >= 0")
    @Max(value = 1, message = "Vigilance must be <= 1")
    private Double vigilance;
}
