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

    @Autowired
    public ScheduleService(ResponseService responseService) {
        this.responseService = responseService;
    }

    /**
     * Method for cleaning responses made earlier than one day ago.
     */
    @Scheduled(cron = "${cron.cleaner.old-responses}")
    @Transactional
    public void cleanOldResponses() {
        log.info("Old responses are being cleared...");
        LocalDate oneDayAgo = DateUtils.getCurrentDateInMoscowTimeZone().minusDays(1);
        List<Response> oldResponses = responseService.findAllByDateBefore(oneDayAgo);
        for (Response response : oldResponses)
            responseService.delete(response);
        log.info("Old responses cleaning was successful!");
    }
}
