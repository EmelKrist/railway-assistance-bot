package ru.emelkrist.service.enums;

public enum Command {
    START("/start"),
    HELP("/help"),
    CANCEL("/cancel"),
    TIMETABLE("/timetable");
    private final String value;

    Command(String value) {
        this.value = value;
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
}
