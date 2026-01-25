package ru.skripov.resume_back.security.dto.auth.registration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class RegistrationRequestDto implements Serializable {
    @Schema(name = "firstName", description = "Имя", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(name = "lastName", description = "Фамилия", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(name = "middleName", description = "Отчество", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String middleName;

    @Schema(name = "password", description = "Пароль", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(name = "email", description = "Email", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(name = "dateOfBirth", description = "Дата рождения", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate dateOfBirth;
}
