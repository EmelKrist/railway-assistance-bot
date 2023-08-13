package ru.emelkrist.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.emelkrist.config.BotConfig;
import ru.emelkrist.service.UpdateProcessor;

import java.util.ArrayList;
import java.util.List;

import static ru.emelkrist.service.enums.Command.*;

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
        setMenu();
    }

    /**
     * Method for setting menu of bot commands.
     */
    private void setMenu() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand(START.getValue(), START.getDescription()));
        commands.add(new BotCommand(HELP.getValue(), HELP.getDescription()));
        commands.add(new BotCommand(CANCEL.getValue(), CANCEL.getDescription()));
        commands.add(new BotCommand(TIMETABLE.getValue(), TIMETABLE.getDescription()));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's common list: " + e.getMessage());
        }
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
