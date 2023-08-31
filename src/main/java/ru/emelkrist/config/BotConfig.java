package ru.emelkrist.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@PropertySource("classpath:application.properties")
@EnableScheduling
@Getter
public class BotConfig {

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

}
