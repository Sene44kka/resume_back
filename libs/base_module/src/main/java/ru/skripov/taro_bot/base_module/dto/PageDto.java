package ru.skripov.resume_back.base_module.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
    @Schema(name = "max", description = "Максимальное количество элементов на странице")
    private Integer max = 10;

    @Schema(name = "offset", description = "Смещение для пагинации")
    private Integer offset = 0;
}