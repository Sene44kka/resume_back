package ru.skripov.resume_back.security.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Для получения токена используйте эндпоинты аутентификации")))
                .info(new Info()
                        .title("Resume Bot API")
                        .version("1.0")
                        .description("""
                    ## API для управления резюме
                    
                    ### 🔐 **Аутентификация**
                    
                    **Как получить токен:**
                    1. Используйте `POST /auth/register` для регистрации нового пользователя
                    2. Или `POST /auth/login` для входа существующего пользователя
                    3. Из ответа скопируйте `accessToken`
                    4. Нажмите кнопку **Authorize** и введите: `ваш_токен`
                    
                    **Эндпоинты аутентификации:**
                    - `POST /auth/register` - регистрация
                    - `POST /auth/login` - вход
                    - `POST /auth/refresh` - обновление токена
                    - `POST /auth/logout` - выход
                    - `GET /auth/state` - проверка состояния
                    - `GET /auth/me` - информация о текущем пользователе
                    
                    ---
                    
                    **После авторизации** все защищенные эндпоинты будут доступны.
                    """));
    }
}