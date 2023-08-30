package ru.emelkrist.exceptions;

public class SchedulesApiClientException extends RuntimeException {
    private long chatId;

    public SchedulesApiClientException(String apiResponseBody, long chatId) {
        super(apiResponseBody);
        this.chatId = chatId;
    }

    public long getChatId() {
        return chatId;
    }
}
