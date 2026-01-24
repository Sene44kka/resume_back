package ru.skripov.resume_back.base_module.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("ru.skripov.resume_back.base_module")
@PropertySource("classpath:application.properties")
public class BaseModuleConfig {
}
