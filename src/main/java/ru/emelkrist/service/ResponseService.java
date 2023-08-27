package ru.emelkrist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.emelkrist.model.Response;
import ru.emelkrist.model.Timetable;
import ru.emelkrist.repository.ResponseRepository;
import ru.emelkrist.service.enums.MessageButton;
import ru.emelkrist.utils.ButtonUtils;
import ru.emelkrist.utils.DateUtils;
import ru.emelkrist.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class ResponseService {

    private final ResponseRepository responseRepository;

    @Autowired
    public ResponseService(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    /**
     * Method for finding a response by identifiers of chat and message.
     *
     * @param chatId    identifier of chat
     * @param messageId identifier of message
     * @return response or null
     */
    public Optional<Response> findByChatIdAndMessageId(long chatId, long messageId) {
        return responseRepository.findByChatIdAndMessageId(chatId, messageId);
    }

    /**
     * Method for saving the response to the database.
     *
     * @param response saved response
     */
    public void save(Response response) {
        responseRepository.save(response);
    }

    /**
     * Method to create a response and to enrich it by chat id,
     * current date and list of timetables.
     *
     * @param chatId     identifier of chat
     * @param timetables list of timetables
     * @return created response
     */
    public Response createResponse(long chatId, ArrayList<Timetable> timetables) {
        Response response = new Response();
        response.setTimetables(timetables);
        response.setDate(DateUtils.getStringCurrentDateInMoscowTimeZone());
        response.setChatId(chatId);

        return response;
    }

    /**
     * Method for generating a message for the response.
     *
     * @param response response
     * @return message for the response
     */
    public SendMessage generateMessageForResponse(Response response) {
        String messageText = MessageUtils.generateMessageTextWithPageOfTimetable(response);
        var message = MessageUtils.generateSendMessageWithText(response.getChatId(), messageText);

        if (response.getTimetables().size() > 1) {
            InlineKeyboardMarkup inlineKeyboardMarkup = ButtonUtils.getInlineKeyboardMarkup(true, false);
            message.setReplyMarkup(inlineKeyboardMarkup);
        }

        return message;
    }

    /**
     * Method for turning a response page.
     *
     * @param update chat update
     * @return message to edit the message with new data
     */
    public EditMessageText turnResponsePage(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();

        Optional<Response> optionalResponse = findByChatIdAndMessageId(chatId, messageId);
        Response response = null;
        if (optionalResponse.isPresent()) {
            response = optionalResponse.get();
        } else {
            log.debug("Request not found in database!");
            return null;
            // TODO добавить сообщение об устаревании или отсутствии данных
            //  * добавить периодическую очитку слишком старых расписаний из БД *
            //  * подумать над тем, чтобы помимо отчистки БД очищать еще и личку
            //  бота с определенной периодичностью, чтобы доступа к старым сообщениям
            //  не было вовсе *
        }

        String callbackData = update.getCallbackQuery().getData();
        int page = response.getPage();
        List<Timetable> timetables = response.getTimetables();
        if (callbackData.equals(MessageButton.FORWARD_BUTTON.getCallbackData())) {
            if (page < timetables.size() - 1) {
                page++;
            }
        } else if (callbackData.equals(MessageButton.BACK_BUTTON.getCallbackData())) {
            if (page > 0) {
                page--;
            }
        } else {
            log.debug("Unsupported callback query data was received!");
            return null;
        }

        boolean hasForwardButton = true, hasBackButton = true;
        if (page == 0) hasBackButton = false;
        if (page == timetables.size() - 1) hasForwardButton = false;

        response.setPage(page);
        save(response);

        String newText = MessageUtils.generateMessageTextWithPageOfTimetable(response);
        var editMessage = MessageUtils.generateEditMessageWithText(chatId, messageId, newText);
        editMessage = ButtonUtils.addEditMessageButtons(editMessage, hasForwardButton, hasBackButton);

        return editMessage;
    }

}
