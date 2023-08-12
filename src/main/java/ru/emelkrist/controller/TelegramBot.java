package ru.emelkrist.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.emelkrist.config.BotConfig;
import ru.emelkrist.service.UpdateProcessor;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final UpdateProcessor updateProcessor;

    @PostConstruct
    private void init() {
        updateProcessor.registerBot(this);
    }

    @Autowired
    public TelegramBot(BotConfig config, UpdateProcessor updateProcessor) {
        this.config = config;
        this.updateProcessor = updateProcessor;
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateProcessor.processUpdate(update);
    }

    /**
     * Method for execution of a message's sending to a user's chat.
     *
     * @param message prepared message for sending
     */
    public void sendMessage(BotApiMethod message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error: " + e.getMessage());
        }
    }

}
