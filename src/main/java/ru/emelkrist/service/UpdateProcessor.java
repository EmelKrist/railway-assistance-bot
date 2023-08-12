package ru.emelkrist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.emelkrist.controller.TelegramBot;
import ru.emelkrist.model.RequestData;
import ru.emelkrist.service.enums.Command;
import ru.emelkrist.service.enums.Question;
import ru.emelkrist.utils.MessageUtils;

import java.util.concurrent.ConcurrentHashMap;

@Controller
@Slf4j
public class UpdateProcessor {

    private TelegramBot telegramBot;
    private ConcurrentHashMap<Long, RequestData> requests = new ConcurrentHashMap<>();
    private final MessageUtils messageUtils;

    public UpdateProcessor(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
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
                case START -> {
                    // TODO если пользователь новый, добавить его в БД
                    setCommandStartView(chatId);
                }
                case HELP -> {
                    setCommandHelpView(chatId);
                }
                case TIMETABLE -> {
                    processStartTimetable(userId, chatId);
                }
                case CANCEL -> {
                    processCancel(userId, chatId);
                }
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
            // TODO получение кода города и проверка ввода на корректность
            request.setFrom(text);
            setToQuestionView(chatId);
        } else if (question.equals(Question.TO)) {
            // TODO получение кода города и проверка ввода на корректность
            request.setTo(text);
            setDateQuestionView(chatId);
        } else if (question.equals(Question.DATE)) {
            // TODO добавить возможность указывать или не указывать дату
            // TODO проверка даты на корректность
            request.setDate(text);
        }
        current++;
        request.setCurrent(current);

        if (current == Question.getLength()) {
            request.setInputting(false);
            log.debug("New timetable request input data received: " + request.toString());
            // TODO отправка запроса в api и получение результата
            // TODO добавить сохранение в БД
        }
    }

    /**
     * Method for setting the view for the question to get the departure date.
     *
     * @param chatId identifier of chat
     */
    private void setDateQuestionView(long chatId) {
        var message = messageUtils.generateSendMessageWithText(
                chatId,
                "Введите желаемую дату отправления в формате: YYYY-MM-DD."
        );
        setView(message);
    }

    /**
     * Method for setting the view for the question to get the city of arrival.
     *
     * @param chatId identifier of chat
     */
    private void setToQuestionView(long chatId) {
        var message = messageUtils.generateSendMessageWithText(
                chatId, "Введите название населенного пункта прибытия."
        );
        setView(message);
    }

    /**
     * Method for processing the cancel command.
     *
     * @param userId identifier of user
     * @param chatId identifier of chat
     */
    private void processCancel(long userId, long chatId) {
        requests.remove(userId);
        setCommandCancelView(chatId);
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
        setCommandTimetableView(chatId);
        setFromQuestionView(chatId);
    }

    /**
     * Method for setting the view for the question to get the departure city.
     *
     * @param chatId identifier of chat
     */
    private void setFromQuestionView(long chatId) {
        var message = messageUtils.generateSendMessageWithText(
                chatId, "Введите название населенного пункта отправления."
        );
        setView(message);
    }

    /**
     * Method for setting the view for the start command.
     *
     * @param chatId identifier of chat
     */
    private void setCommandStartView(long chatId) {
        var message = messageUtils.generateSendMessageWithText(
                chatId, "Здравствуйте! Вас приветствует железнодорожный бот-ассистент.\n" +
                        "Для получения дополнительной информации о назначении и функционале бота " +
                        "введите команду /help."
        );
        setView(message);
    }

    /**
     * Method for setting the view for the help command.
     *
     * @param chatId identifier of chat
     */
    private void setCommandHelpView(long chatId) {
        var message = messageUtils.generateSendMessageWithText(
                chatId, "Данный бот предоставляет пользователю возможность получить " +
                        "расписание рейсов между двумя станциями. \n" +
                        "Доступные команды: \n" +
                        "/start - приветственное сообщение для начала работы с ботом; \n" +
                        "/help - повторно вывести данное сообщение; \n" +
                        "/timetable - получить расписание рейсов между двумя станциями; \n" +
                        "/cancel - отменить нынешнюю команду."
        );
        setView(message);
    }

    /**
     * Method for setting the view for the timetable command.
     *
     * @param chatId identifier of chat
     */
    private void setCommandTimetableView(long chatId) {
        var message = messageUtils.generateSendMessageWithText(
                chatId,
                "Вы запустили процесс получения расписания рейсов между станциями.\n" +
                        "Пожалуйста, следуйте дальнейшим инструкциям..."
        );
        setView(message);
    }

    /**
     * Method for setting the view for the cancel command.
     *
     * @param chatId identifier of chat
     */
    private void setCommandCancelView(long chatId) {
        var message = messageUtils.generateSendMessageWithText(
                chatId, "Команда отменена."
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
