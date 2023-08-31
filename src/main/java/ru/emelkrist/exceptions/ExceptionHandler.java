package ru.emelkrist.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ru.emelkrist.controller.UpdateController;

import static ru.emelkrist.service.enums.ChatMessage.SCHEDULES_API_CLIENT_EXCEPTION_MESSAGE;
import static ru.emelkrist.service.enums.ChatMessage.SCHEDULES_API_SERVER_EXCEPTION_MESSAGE;

@Slf4j
@ControllerAdvice
public class ExceptionHandler {

    private UpdateController updateController;

    /**
     * Method for UpdateController injection.
     *
     * @param updateController controller of updates
     */
    public void registerUpdateController(UpdateController updateController) {
        this.updateController = updateController;
    }

    /**
     * Method to handle SchedulesApiClientException.
     *
     * @param e exception
     */
    public void handleSchedulesApiClientException(SchedulesApiClientException e) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(e.getMessage());
            String textError = rootNode.get("error").get("text").asText();
            textError = textError.replaceAll("\\w+:\\s", "");
            updateController.setChatMessageView(e.getChatId(), SCHEDULES_API_CLIENT_EXCEPTION_MESSAGE);
            log.error("Schedules API Client exception: " + e.getMessage());
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * Method to handle SchedulesApiServerException.
     *
     * @param e exception
     */
    public void handleSchedulesApiServerException(SchedulesApiServerException e) {
        updateController.setChatMessageView(e.getChatId(), SCHEDULES_API_SERVER_EXCEPTION_MESSAGE);
        log.error("Schedules API Server exception: " + e.getMessage());
    }
}
