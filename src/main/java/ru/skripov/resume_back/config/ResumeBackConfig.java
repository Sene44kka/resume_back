package ru.skripov.resume_back.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"ru.skripov.resume_back"})
//@Import({
//                SecurityConfig.class,
//                TelegramBotConfig.class,
//                OpenApiConfig.class
//        })
public class ResumeBackConfig {

}
