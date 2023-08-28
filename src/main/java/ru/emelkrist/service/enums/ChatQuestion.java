package ru.emelkrist.service.enums;

public enum ChatQuestion {
    FROM, TO, DATE;

    public static int getLength() {
        return ChatQuestion.values().length;
    }
}

