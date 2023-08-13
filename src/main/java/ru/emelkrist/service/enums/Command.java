package ru.emelkrist.service.enums;

public enum Command {
    START("/start", "начать работу с ботом"),
    HELP("/help", "получить информацию об использовании бота"),
    CANCEL("/cancel", "отменить выполняемую команду"),
    TIMETABLE("/timetable", "получить расписание рейсов между станциями");
    private final String value;
    private final String description;

    Command(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Command fromValue(String v) {
        for (Command cmd : Command.values()) {
            if (cmd.value.equals(v)) {
                return cmd;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
