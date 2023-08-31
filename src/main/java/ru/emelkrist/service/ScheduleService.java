package ru.emelkrist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emelkrist.model.Response;
import ru.emelkrist.utils.DateUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ScheduleService {

    private final ResponseService responseService;
    private final YandexEncodingService yandexEncodingService;
    private final BotLockService botLockService;

    @Autowired
    public ScheduleService(ResponseService responseService, YandexEncodingService yandexEncodingService, BotLockService botLockService) {
        this.responseService = responseService;
        this.yandexEncodingService = yandexEncodingService;
        this.botLockService = botLockService;
    }

    /**
     * Method for cleaning responses made earlier than one day ago.
     */
    @Scheduled(cron = "${cron.cleaner.old-responses}")
    @Transactional
    public void cleanOldResponses() {
        botLockService.setLocked(true);
        log.info("Old responses are being cleared...");
        LocalDate oneDayAgo = DateUtils.getCurrentDateInMoscowTimeZone().minusDays(1);
        List<Response> oldResponses = responseService.findAllByDateBefore(oneDayAgo);
        for (Response response : oldResponses)
            responseService.delete(response);
        log.info("Old responses cleaning was successful!");
        botLockService.setLocked(false);
    }

    /**
     * Method to update the database of old city codes.
     */
    @Scheduled(cron = "${cron.updater.city-codes}")
    public void updateCityCodesDatabase() {
        botLockService.setLocked(true);
        log.info("The database of old city codes is being update.");
        yandexEncodingService.generateMapOfCityCodes();
        log.info("The database of old city codes updating was successful!");
        botLockService.setLocked(false);
    }
}
