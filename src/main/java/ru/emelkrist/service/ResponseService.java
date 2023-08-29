package ru.emelkrist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.emelkrist.controller.UpdateController;
import ru.emelkrist.model.Response;
import ru.emelkrist.model.Timetable;
import ru.emelkrist.repository.ResponseRepository;
import ru.emelkrist.service.enums.ChatButton;
import ru.emelkrist.utils.ButtonUtils;
import ru.emelkrist.utils.DateUtils;
import ru.emelkrist.utils.MessageUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.emelkrist.service.enums.ChatMessage.SESSION_EXPIRED_MESSAGE;

@Service
@Slf4j
@Transactional
public class ResponseService {

    private UpdateController updateController;
    private final ResponseRepository responseRepository;

    @Autowired
    public ResponseService(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    /**
     * Method for UpdateController injection.
     *
     * @param updateController controller of updates
     */
    public void registerUpdateController(UpdateController updateController) {
        this.updateController = updateController;
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
        response.getTimetables().forEach(timetable -> timetable.setResponse(response));
        responseRepository.save(response);
    }

    /**
     * Method for deleting the response from the database.
     *
     * @param response deleted response
     */
    public void delete(Response response) {
        responseRepository.delete(response);
    }

    /**
     * Method to find all responses with a date before the specified date.
     *
     * @param date date
     * @return list of responses
     */
    public List<Response> findAllByDateBefore(LocalDate date) {
        return responseRepository.findAllByDateBefore(date);
    }

    /**
     * Method for processing the response to the request.
     *
     * @param chatId     identifier of chat
     * @param timetables list of timetables
     * @return response to request
     */
    public Response processResponse(long chatId, ArrayList<Timetable> timetables) {
        Response response = createResponse(chatId, timetables);
        SendMessage message = generateMessageForResponse(response);

        int messageId = updateController.setView(message);
        response.setMessageId(messageId);

        save(response);
        return response;
    }

    /**
     * Method to create a response and to enrich it by chat id,
     * current date and list of timetables.
     *
     * @param chatId     identifier of chat
     * @param timetables list of timetables
     * @return created response
     */
    private Response createResponse(long chatId, ArrayList<Timetable> timetables) {
        Response response = new Response();
        response.setTimetables(timetables);
        response.setDate(DateUtils.getCurrentDateInMoscowTimeZone());
        response.setChatId(chatId);

        return response;
    }

    /**
     * Method for generating a message for the response.
     *
     * @param response response
     * @return message for the response
     */
    private SendMessage generateMessageForResponse(Response response) {
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

        // todo вынести получение ответа в отдельный метод
        Optional<Response> optionalResponse = findByChatIdAndMessageId(chatId, messageId);
        Response response = null;
        if (optionalResponse.isPresent()) {
            response = optionalResponse.get();
        } else {
            log.debug("Response not found in database!");
            return MessageUtils.generateEditMessageWithText(chatId, messageId,
                    SESSION_EXPIRED_MESSAGE.getText());
        }

        String callbackData = update.getCallbackQuery().getData();
        int page = response.getPage();
        List<Timetable> timetables = response.getTimetables();
        if (callbackData.equals(ChatButton.FORWARD_BUTTON.getCallbackData())) {
            if (page < timetables.size() - 1) {
                page++;
            }
        } else if (callbackData.equals(ChatButton.BACK_BUTTON.getCallbackData())) {
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
