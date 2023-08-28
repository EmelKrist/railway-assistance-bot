package ru.emelkrist.service.enums;

public enum ChatCommand {
    START("/start", "начать работу с ботом"),
    HELP("/help", "получить информацию об использовании бота"),
    CANCEL("/cancel", "отменить выполняемую команду"),
    TIMETABLE("/timetable", "получить расписание рейсов между станциями");
    private final String value;
    private final String description;

    ChatCommand(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ChatCommand fromValue(String v) {
        for (ChatCommand cmd : ChatCommand.values()) {
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
