package ru.emelkrist.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.emelkrist.config.BotConfig;
import ru.emelkrist.service.BotLockService;
import ru.emelkrist.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

import static ru.emelkrist.service.enums.ChatCommand.*;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UpdateController updateController;

    @PostConstruct
    private void init() {
        updateController.registerBot(this);
    }

    @Autowired
    public TelegramBot(BotConfig config, UpdateController updateController) {
        this.config = config;
        this.updateController = updateController;
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
            updateController.processUpdate(update);
    }

    /**
     * Method for execution of a message's sending to a user's chat.
     *
     * @param message prepared message for sending
     */
    public int sendMessage(SendMessage message) {
        try {
            return execute(message).getMessageId();
        } catch (TelegramApiException e) {
            log.error("Error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Method for execution of a message's editing to a user's chat.
     *
     * @param message prepared message for editing
     */
    public void editMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error: " + e.getMessage());
        }
    }

}
