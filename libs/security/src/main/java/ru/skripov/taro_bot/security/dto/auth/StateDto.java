package ru.skripov.resume_back.security.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class StateDto implements Serializable {
    @Schema(name = "auth", description = "Признак активности сессии")
    private Boolean auth;
}