package ru.emelkrist.service.enums;

public enum ChatMessage {
    DATE_QUESTION("Введите желаемую дату отправления в формате: YYYY-MM-DD."),
    TO_QUESTION("Введите название населенного пункта прибытия."),
    FROM_QUESTION("Введите название населенного пункта отправления."),
    START_MESSAGE("Здравствуйте! Вас приветствует железнодорожный бот-ассистент.\n" +
            "Для получения дополнительной информации о назначении и функционале бота " +
            "введите команду /help."),
    HELP_MESSAGE("Данный бот предоставляет пользователю возможность получить " +
            "расписание рейсов между двумя станциями. \n" +
            "Доступные команды: \n" +
            "/start - приветственное сообщение для начала работы с ботом; \n" +
            "/help - повторно вывести данное сообщение; \n" +
            "/timetable - получить расписание рейсов между двумя станциями; \n" +
            "/cancel - отменить нынешнюю команду."),
    TIMETABLE_MESSAGE("Вы запустили процесс получения расписания рейсов между станциями.\n" +
            "Пожалуйста, следуйте дальнейшим инструкциям..."),
    CANCEL_MESSAGE("Команда отменена.");
    private final String text;

    ChatMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}