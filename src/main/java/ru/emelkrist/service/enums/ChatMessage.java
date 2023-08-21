package ru.emelkrist.service.enums;

public enum ChatMessage {
    DATE_QUESTION("Если хотите получить ближайшее расписание рейсов на любые дни, ответьте \"Да\". \n" +
            "Если же вас интересует конкретная дата, напишите ее в формате: гггг-мм-дд."),
    TO_QUESTION("Введите название населенного пункта прибытия."),
    FROM_QUESTION("Введите название населенного пункта отправления."),
    START_MESSAGE("Здравствуйте, {name}! Вас приветствует железнодорожный бот-ассистент.\n" +
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
    CANCEL_MESSAGE("Команда отменена."),
    NOT_VALID_CITY_MESSAGE("Выбранный населенный пункт не был найден!\n" +
            "Возможно, он не поддерживается нашей системой или его название было заданно некорректно.\n" +
            "Пожалуйста, проверьте правильность написания и попробуйте ввести еще раз."),
    INVALID_DATE_MESSAGE("Дата не соответствует формату гггг-мм-дд." +
            "Пожалуйста, проверьте правильность написания и попробуйте ввести еще раз. \n" +
            "Или ответьте \"Да\", если хотите получить ближайшее расписание рейсов на любые дни."),
    IMPOSSIBLE_DATE_MESSAGE("Доступен выбор даты на 30 дней назад и 11 месяцев вперед от текущей." +
            "Пожалуйста, выберете другую дату и попробуйте ввести еще раз. \n" +
            "Или ответьте \"Да\", если хотите получить ближайшее расписание рейсов на любые дни.");
    private String text;

    ChatMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
