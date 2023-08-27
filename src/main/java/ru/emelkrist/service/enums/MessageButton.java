package ru.emelkrist.service.enums;

public enum MessageButton {
    BACK_BUTTON("BACK_BUTTON", "Назад"),
    FORWARD_BUTTON("FORWARD_BUTTON", "Вперед");
    private String callbackData;
    private String text;

    MessageButton(String callbackData, String text) {
        this.callbackData = callbackData;
        this.text = text;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public String getText() {
        return text;
    }
}
