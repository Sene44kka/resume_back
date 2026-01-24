package ru.skripov.resume_back.base_module.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortDto {
    @Schema(name = "sortOrder", description = "Направление сортировки")
    private SortOrder sortOrder;

    @Schema(name = "column", description = "Колонка, по которой сортируем")
    private String column;
}
