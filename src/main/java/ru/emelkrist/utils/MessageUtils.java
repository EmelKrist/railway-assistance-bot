package ru.emelkrist.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class MessageUtils {

    /**
     * Method for generating a send message with specific text.
     *
     * @param chatId      identifier of chat
     * @param textMessage text of message
     * @return generated message
     */
    public SendMessage generateSendMessageWithText(long chatId, String textMessage) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textMessage);

        return sendMessage;
    }
}
