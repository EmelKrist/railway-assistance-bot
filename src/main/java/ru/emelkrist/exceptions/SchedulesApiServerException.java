package ru.emelkrist.exceptions;

public class SchedulesApiServerException extends RuntimeException {
    private long chatId;

    public SchedulesApiServerException(String apiResponseBody, long chatId) {
        super(apiResponseBody);
        this.chatId = chatId;
    }

    public long getChatId() {
        return chatId;
    }
}
