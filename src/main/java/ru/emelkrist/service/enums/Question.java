package ru.emelkrist.service.enums;

public enum Question {
    FROM, TO, DATE;

    public static int getLength() {
        return Question.values().length;
    }
}

