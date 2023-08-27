package ru.emelkrist.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

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
}
