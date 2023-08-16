package ru.emelkrist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.emelkrist.controller.TelegramBot;
import ru.emelkrist.model.RequestData;
import ru.emelkrist.service.enums.Command;
import ru.emelkrist.service.enums.ChatMessage;
import ru.emelkrist.service.enums.Question;
import ru.emelkrist.utils.MessageUtils;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static ru.emelkrist.service.enums.ChatMessage.*;

@Controller
@Slf4j
public class UpdateProcessor {

    private TelegramBot telegramBot;
    private ConcurrentHashMap<Long, RequestData> requests = new ConcurrentHashMap<>();
    private final MessageUtils messageUtils;
    private final YandexEncodingService yandexEncodingService;

    public UpdateProcessor(MessageUtils messageUtils, YandexEncodingService yandexEncodingService) {
        this.messageUtils = messageUtils;
        this.yandexEncodingService = yandexEncodingService;
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

    /**
     * Method for processing updates received by the bot.
     *
     * @param update chat update
     */
    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            processTextMessage(update);
        } else {
            log.error("Unsupported message type is received: " + update);
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
        var command = Command.fromValue(text);
        RequestData request = requests.get(userId);

        if (command != null) {
            switch (command) {
                case START -> processStart(chatId);
                case HELP -> processHelp(chatId);
                case TIMETABLE -> processStartTimetable(userId, chatId);
                case CANCEL -> processCancel(userId, chatId);
            }
        } else if (request != null && request.isInputting()) {
            processTimetableInputData(text, userId, chatId);
        } else {
            log.error("Unsupported message is received: " + update);
        }
    }

    /**
     * Method of processing the input data received from the user,
     * necessary to get the train timetable between stations.
     *
     * @param text   text of message
     * @param userId identifier of user
     * @param chatId identifier of chat
     */
    private void processTimetableInputData(String text, long userId, long chatId) {
        RequestData request = requests.get(userId);
        int current = request.getCurrent();
        Question question = Question.values()[current];

        if (question.equals(Question.FROM)) {
            processFromAnswer(request, text, chatId);
        } else if (question.equals(Question.TO)) {
            processToAnswer(request, text, chatId);
        } else if (question.equals(Question.DATE)) {
            processDateAnswer(request, text);
        }
        current = request.getCurrent();
        request.setCurrent(++current);

        if (current == Question.getLength()) {
            request.setInputting(false);
            log.debug("New timetable request input data received: " + request.toString());
            // TODO отправка запроса в api и получение результата
            // TODO добавить сохранение в БД
        }
    }

    /**
     * Method for processing the answer to the question to get the departure city.
     *
     * @param request  request data
     * @param cityName name of city (text of question's answer)
     * @param chatId   identifier of chat
     */
    private void processFromAnswer(RequestData request, String cityName, long chatId) {
        Optional<String> cityCode = yandexEncodingService.getCityCodeByCityName(cityName);
        ChatMessage answer;
        if (cityCode.isPresent()) {
            request.setFrom(cityCode.get());
            answer = TO_QUESTION;
        } else {
            request.setCurrent(request.getCurrent() - 1);
            answer = NOT_VALID_CITY_MESSAGE;
        }
        setChatMessageView(chatId, answer);
    }

    /**
     * Method for processing the answer to the question to get the city of arrival.
     *
     * @param request request data
     * @param cityName name of city (text of question's answer)
     * @param chatId  identifier of chat
     */
    private void processToAnswer(RequestData request, String cityName, long chatId) {
        Optional<String> cityCode = yandexEncodingService.getCityCodeByCityName(cityName);
        ChatMessage answer;
        if (cityCode.isPresent()) {
            request.setTo(cityCode.get());
            answer = DATE_QUESTION;
        } else {
            request.setCurrent(request.getCurrent() - 1);
            answer = NOT_VALID_CITY_MESSAGE;
        }
        setChatMessageView(chatId, answer);
    }

    /**
     * Method for processing the answer to the question to get the departure date.
     *
     * @param request request data
     * @param text    text of question's answer
     */
    private void processDateAnswer(RequestData request, String text) {
        // TODO добавить возможность указывать или не указывать дату
        // TODO проверка даты на корректность
        request.setDate(text);
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
     * @param chatId identifier of chat
     */
    private void processStart(long chatId) {
        // TODO если пользователь новый, добавить его в БД
        // TODO добавлять в приветствие имя
        setChatMessageView(chatId, START_MESSAGE);
    }

    /**
     * Method for processing the cancel command.
     *
     * @param userId identifier of user
     * @param chatId identifier of chat
     */
    private void processCancel(long userId, long chatId) {
        requests.remove(userId);
        setChatMessageView(chatId, CANCEL_MESSAGE);
    }

    /**
     * Method for processing the timetable start command.
     *
     * @param userId identifier of user
     * @param chatId identifier of chat
     */
    private void processStartTimetable(long userId, long chatId) {
        RequestData request = new RequestData();
        request.setCurrent(0);
        request.setInputting(true);
        requests.put(userId, request);
        setChatMessageView(chatId, TIMETABLE_MESSAGE);
        setChatMessageView(chatId, FROM_QUESTION);
    }

    /**
     * Method for setting the view for the question to get the departure city.
     *
     * @param chatId identifier of chat
     */
    private void setChatMessageView(long chatId, ChatMessage chatMessage) {
        var message = messageUtils.generateSendMessageWithText(
                chatId, chatMessage.getText()
        );
        setView(message);
    }

    /**
     * Method for sending the message (view) to the user.
     *
     * @param message sending message
     */
    private void setView(SendMessage message) {
        telegramBot.sendMessage(message);
    }
}
