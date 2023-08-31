package ru.emelkrist.utils;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.emelkrist.service.enums.ChatButton;

import java.util.ArrayList;
import java.util.List;

public class ButtonUtils {

    /**
     * Method for getting inline keyboard markup with
     * forward and back buttons as desired.
     *
     * @param hasForwardButton message has "Forward" button
     * @param hasBackButton    message has "Back" button
     * @return inline keyboard markup
     */
    public static InlineKeyboardMarkup getInlineKeyboardMarkup(boolean hasForwardButton, boolean hasBackButton) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        if (hasBackButton) {
            var backKeyboardButton = new InlineKeyboardButton();
            ChatButton backButton = ChatButton.BACK_BUTTON;
            backKeyboardButton.setText(backButton.getText());
            backKeyboardButton.setCallbackData(backButton.getCallbackData());
            rowInLine.add(backKeyboardButton);
        }

        if (hasForwardButton) {
            var forthKeyboardButton = new InlineKeyboardButton();
            ChatButton forwardButton = ChatButton.FORWARD_BUTTON;
            forthKeyboardButton.setText(forwardButton.getText());
            forthKeyboardButton.setCallbackData(forwardButton.getCallbackData());
            rowInLine.add(forthKeyboardButton);
        }

        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);

        return inlineKeyboardMarkup;
    }

    /**
     * Method to adding edit message buttons as desired.
     *
     * @param editMessage      edit message
     * @param hasForwardButton message has "Forward" button
     * @param hasBackButton    message has "Back" button
     * @return edit message with buttons
     */
    public static EditMessageText addEditMessageButtons(EditMessageText editMessage,
                                                        boolean hasForwardButton, boolean hasBackButton) {
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonUtils.getInlineKeyboardMarkup(hasForwardButton, hasBackButton);
        editMessage.setReplyMarkup(inlineKeyboardMarkup);

        return editMessage;
    }
}
