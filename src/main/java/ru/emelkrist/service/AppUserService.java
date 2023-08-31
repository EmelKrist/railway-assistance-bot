package ru.emelkrist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.emelkrist.model.AppUser;
import ru.emelkrist.repository.AppUserRepository;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Method for finding a user in the database
     * or saving him if he doesn't exist in it.
     *
     * @param update update of chat
     * @return application user who sent the update
     */
    public AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        var appUser = appUserRepository.findByTelegramUserId(telegramUser.getId());

        if (appUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .build();
            return appUserRepository.save(transientAppUser);
        }
        return appUser.get();
    }

}
