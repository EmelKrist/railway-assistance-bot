package ru.emelkrist.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.emelkrist.dto.RequestDTO;
import ru.emelkrist.exceptions.ExceptionHandler;
import ru.emelkrist.service.*;
import ru.emelkrist.service.enums.ChatCommand;
import ru.emelkrist.service.enums.ChatMessage;
import ru.emelkrist.utils.MessageUtils;

import static ru.emelkrist.service.enums.ChatMessage.*;

@Controller
@Slf4j
public class UpdateController {

    private TelegramBot telegramBot;
    private final YandexEncodingService yandexEncodingService;
    private final AppUserService appUserService;
    private final RequestService requestService;
    private final ResponseService responseService;
    private final AnswerService answerService;
    private final BotLockService botLockService;
    private final ExceptionHandler exceptionHandler;

    @Autowired
    public UpdateController(YandexEncodingService yandexEncodingService, AppUserService appUserService, RequestService requestService, ResponseService responseService, AnswerService answerService, BotLockService botLockService, ExceptionHandler exceptionHandler) {
        this.yandexEncodingService = yandexEncodingService;
        this.appUserService = appUserService;
        this.requestService = requestService;
        this.responseService = responseService;
        this.answerService = answerService;
        this.botLockService = botLockService;
        this.exceptionHandler = exceptionHandler;
        this.yandexEncodingService.generateMapOfCityCodes();
    }

    /**
     * Method for TelegramBot injection.
     * Note: is needed due to the impossibility of implementing Spring
     * capabilities, because the application won't run (a vicious circle
     * will happen)
     *
     * @param telegramBot TelegramBot component.
     */
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    private void init() {
        answerService.registerUpdateController(this);
        requestService.registerUpdateController(this);
        responseService.registerUpdateController(this);
        exceptionHandler.registerUpdateController(this);
    }

    /**
     * Method for processing updates received by the bot.
     *
     * @param update chat update
     */
    public void processUpdate(Update update) {
        if (botLockService.isLocked()) {
            lockMessaging(update);
        } else {
            if (update == null) {
                log.error("Received update is null");
                return;
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                processTextMessage(update);
            } else if (update.hasCallbackQuery()) {
                processCallbackQuery(update);
            } else {
                log.error("Unsupported message type is received: " + update);
            }
        }
    }

    /**
     * Method for processing a text message received from a user.
     * Note: Distributes commands and information to process the correct
     * view (response) to the user.
     *
     * @param update chat update
     */
    private void processTextMessage(Update update) {
        var message = update.getMessage();
        var text = message.getText();
        var userId = message.getFrom().getId();
        var chatId = message.getChatId();
        var command = ChatCommand.fromValue(text);
        RequestDTO request = RequestService.getRequest(userId);

        if (command != null) {
            switch (command) {
                case START -> processStart(update);
                case HELP -> processHelp(chatId);
                case TIMETABLE -> processTimetable(userId, chatId);
                case CANCEL -> processCancel(userId, chatId);
            }
        } else if (request != null && request.isInputting()) {
            requestService.processRequestInputData(text, userId, chatId);
        } else {
            log.error("Unsupported message is received: " + update);
        }
    }

    /**
     * Method for processing callback queries.
     *
     * @param update chat update
     */
    private void processCallbackQuery(Update update) {
        var editedMessage = responseService.turnResponsePage(update);
        if (editedMessage != null) {
            editView(editedMessage);
        }
    }

    /**
     * Method to send an information message about locking the bot to the user's chat.
     *
     * @param update chat update
     */
    private void lockMessaging(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        setChatMessageView(chatId, BOT_LOCKED_MESSAGE);
    }

    /**
     * Method for processing the help command.
     *
     * @param chatId identifier of chat
     */
    private void processHelp(long chatId) {
        setChatMessageView(chatId, HELP_MESSAGE);
    }

    /**
     * Method for processing the start command.
     *
     * @param update chat update
     */
    private void processStart(Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        var appUser = appUserService.findOrSaveAppUser(update);
        START_MESSAGE.setText(START_MESSAGE.getText()
                .replace("{name}", appUser.getFirstName())
        );
        setChatMessageView(chatId, START_MESSAGE);
    }

    /**
     * Method for processing the cancel command.
     *
     * @param userId identifier of user
     * @param chatId identifier of chat
     */
    public void processCancel(long userId, long chatId) {
        RequestService.removeRequest(userId);
        setChatMessageView(chatId, CANCEL_MESSAGE);
    }

    /**
     * Method for processing the timetable start command.
     *
     * @param userId identifier of user
     * @param chatId identifier of chat
     */
    private void processTimetable(long userId, long chatId) {
        RequestDTO request = new RequestDTO();
        request.setCurrent(0);
        request.setInputting(true);
        RequestService.putRequest(userId, request);
        setChatMessageView(chatId, TIMETABLE_MESSAGE);
        setChatMessageView(chatId, FROM_QUESTION);
    }

    /**
     * Method for setting the view for the question to get the departure city.
     *
     * @param chatId identifier of chat
     */
    public void setChatMessageView(long chatId, ChatMessage chatMessage) {
        var message = MessageUtils.generateSendMessageWithText(
                chatId, chatMessage.getText()
        );
        setView(message);
    }

    /**
     * Method for setting the view for the question to get the departure city.
     *
     * @param chatId identifier of chat
     */
    public void setChatMessageView(long chatId, String chatMessage) {
        var message = MessageUtils.generateSendMessageWithText(
                chatId, chatMessage
        );
        setView(message);
    }

    /**
     * Method for sending the message (view) to the user.
     *
     * @param message sending message
     */
    public int setView(SendMessage message) {
        return telegramBot.sendMessage(message);
    }

    /**
     * Method to edit the message (view) to the user.
     *
     * @param message sending message
     */
    public void editView(EditMessageText message) {
        telegramBot.editMessage(message);
    }
}
