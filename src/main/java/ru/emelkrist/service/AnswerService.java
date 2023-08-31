package ru.emelkrist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.emelkrist.controller.UpdateController;
import ru.emelkrist.dto.RequestDTO;
import ru.emelkrist.service.enums.ChatMessage;
import ru.emelkrist.utils.CityUtils;
import ru.emelkrist.utils.DateUtils;

import java.util.Optional;

import static ru.emelkrist.service.enums.ChatMessage.*;
import static ru.emelkrist.service.enums.ChatMessage.INVALID_DATE_MESSAGE;

@Service
public class AnswerService {

    private UpdateController updateController;
    private final YandexEncodingService yandexEncodingService;

    @Autowired
    public AnswerService(YandexEncodingService yandexEncodingService) {
        this.yandexEncodingService = yandexEncodingService;
    }

    /**
     * Method for UpdateController injection.
     * @param updateController controller of updates
     */
    public void registerUpdateController(UpdateController updateController) {
        this.updateController = updateController;
    }

    /**
     * Method for processing the answer to the question to get the departure city.
     *
     * @param request  request data
     * @param cityName name of city (text of question's answer)
     * @param chatId   identifier of chat
     */
    public void processFromAnswer(RequestDTO request, String cityName, long chatId) {
        cityName = CityUtils.formatCityName(cityName);
        Optional<String> cityCode = yandexEncodingService.getCityCodeByCityName(cityName);
        ChatMessage answer;
        if (cityCode.isPresent()) {
            request.setFromCity(cityName);
            request.setCodeFrom(cityCode.get());
            answer = TO_QUESTION;
        } else {
            request.setCurrent(request.getCurrent() - 1);
            answer = NOT_VALID_CITY_MESSAGE;
        }
        updateController.setChatMessageView(chatId, answer);
    }

    /**
     * Method for processing the answer to the question to get the city of arrival.
     *
     * @param request  request data
     * @param cityName name of city (text of question's answer)
     * @param chatId   identifier of chat
     */
    public void processToAnswer(RequestDTO request, String cityName, long chatId) {
        cityName = CityUtils.formatCityName(cityName);
        Optional<String> cityCode = yandexEncodingService.getCityCodeByCityName(cityName);
        ChatMessage answer;
        if (cityCode.isPresent()) {
            request.setToCity(cityName);
            request.setCodeTo(cityCode.get());
            answer = DATE_QUESTION;
        } else {
            request.setCurrent(request.getCurrent() - 1);
            answer = NOT_VALID_CITY_MESSAGE;
        }
        updateController.setChatMessageView(chatId, answer);
    }

    /**
     * Method for processing the answer to the question to get the departure date.
     *
     * @param chatId  identifier of chat
     * @param request request data
     * @param answer  text of question's answer
     */
    public void processDateAnswer(long chatId, RequestDTO request, String answer) {
        if (answer.equals("Да")) return;

        ChatMessage message;
        if (DateUtils.isValid(answer)) {
            if (DateUtils.isGreaterThanNow(answer)) {
                request.setDate(DateUtils.format(answer));
                return;
            } else message = IMPOSSIBLE_DATE_MESSAGE;
        } else message = INVALID_DATE_MESSAGE;

        request.setCurrent(request.getCurrent() - 1);
        updateController.setChatMessageView(chatId, message);
    }
}
