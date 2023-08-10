package ru.emelkrist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.emelkrist.config.BotConfig;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;

    @Autowired
    public TelegramBot(BotConfig config) {
        this.config = config;
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            var userName = update.getMessage().getChat().getFirstName();
            var chatId = update.getMessage().getFrom().getId();
            String answer = "Привет, " + userName + ", добро пожаловать " +
                    "в железнодорожный бот-помощник.";
            prepareAndSendMessage(chatId, answer);
        }
    }

    /**
     * Method for preparing and sending a message with specific
     * text to the user's chat by its id.
     * @param chatId identifier of chat
     * @param textMessage text of message
     */
    public void prepareAndSendMessage(long chatId, String textMessage) {
        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textMessage);

        sendMessage(message);
    }

    /**
     * Method for execution of a message's sending to a user's chat.
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
