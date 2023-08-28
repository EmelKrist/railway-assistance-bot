package ru.emelkrist.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.emelkrist.dto.RequestDTO;
import ru.emelkrist.model.Response;
import ru.emelkrist.model.Timetable;

@Component
public class MessageUtils {

    /**
     * Method for generating a send message with specific text.
     *
     * @param chatId      identifier of chat
     * @param textMessage text of message
     * @return generated message
     */
    public static SendMessage generateSendMessageWithText(long chatId, String textMessage) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textMessage);

        return sendMessage;
    }

    /**
     * Method for generating an edit message with text.
     *
     * @param chatId         identifier of chat
     * @param messageId      identifier of message
     * @param newTextMessage new text of message
     * @return generated message
     */
    public static EditMessageText generateEditMessageWithText(long chatId, long messageId, String newTextMessage) {
        var editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId((int) messageId);
        editMessage.setText(newTextMessage);

        return editMessage;
    }

    /**
     * Method to generate a text of message with page of timetable list.
     *
     * @param response response for which the text is generated
     * @return text of message
     */
    public static String generateMessageTextWithPageOfTimetable(Response response) {
        StringBuilder messageText = new StringBuilder();
        int timetablePage = response.getPage();
        Timetable timetable = response.getTimetables().get(timetablePage);

        messageText
                .append("Поезд №").append(timetable.getTrainNumber()).append(": ")
                .append(timetable.getTrainTitle()).append("\n")
                .append("Станция отправления: ")
                .append(timetable.getFromStationTitle()).append("\n")
                .append("Станция прибытия: ")
                .append(timetable.getToStationTitle()).append("\n");

        if (timetable.getDays() != null) {
            messageText.append("Дни: ").append(timetable.getDays()).append("\n");
        }

        messageText
                .append("Отправление: ")
                .append(DateUtils.getFormattedDateTime(timetable.getDeparture())).append("\n")
                .append("Прибытие: ")
                .append(DateUtils.getFormattedDateTime(timetable.getArrival())).append("\n")
                .append("\n").append("* указано местное время *");

        return messageText.toString();
    }

    /**
     * Method to generate text of confirmation request message.
     *
     * @param requestDTO request data
     * @return text of message with request data
     */
    public static String generateTextOfConfirmationRequestMessage(RequestDTO requestDTO) {
        StringBuilder confirmationRequestMessage = new StringBuilder();

        confirmationRequestMessage
                .append("Данные запроса были получены.").append("\n")
                .append("1. Пункт отправления: ").append(requestDTO.getFromCity()).append(";\n")
                .append("2. Пункт прибытия: ").append(requestDTO.getToCity()).append(";\n")
                .append("3. Дата рейса: ")
                .append(requestDTO.getDate() == null ? "все дни" : requestDTO.getDate()).append(".\n")
                .append("Для подтверждения получения расписания ответьте \"Да\".\n")
                .append("В противном случае ответьте \"Нет\" или пропишите команду /cancel.");

        return confirmationRequestMessage.toString();
    }
}
