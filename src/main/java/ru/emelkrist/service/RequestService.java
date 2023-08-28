package ru.emelkrist.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.emelkrist.controller.UpdateController;
import ru.emelkrist.dto.RequestDTO;
import ru.emelkrist.model.Request;
import ru.emelkrist.model.Response;
import ru.emelkrist.model.Timetable;
import ru.emelkrist.repository.RequestRepository;
import ru.emelkrist.service.enums.ChatQuestion;
import ru.emelkrist.utils.MessageUtils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static ru.emelkrist.service.enums.ChatMessage.*;

@Service
public class RequestService {

    private static ConcurrentHashMap<Long, RequestDTO> requests = new ConcurrentHashMap<>();
    private UpdateController updateController;
    private final RequestRepository requestRepository;
    private final YandexTimetableService yandexTimetableService;
    private final ResponseService responseService;
    private final AnswerService answerService;
    private final ModelMapper modelMapper;

    @Autowired
    public RequestService(RequestRepository requestRepository, ModelMapper modelMapper, YandexTimetableService yandexTimetableService, ResponseService responseService, AnswerService answerService) {
        this.requestRepository = requestRepository;
        this.modelMapper = modelMapper;
        this.yandexTimetableService = yandexTimetableService;
        this.responseService = responseService;
        this.answerService = answerService;
    }

    /**
     * Method for UpdateController injection.
     *
     * @param updateController controller of updates
     */
    public void registerUpdateController(UpdateController updateController) {
        this.updateController = updateController;
    }

    /**
     * Method for saving the request to the database.
     *
     * @param request saved request
     */
    public void save(Request request) {
        requestRepository.save(request);
    }

    /**
     * Method of processing the input data received from the user,
     * necessary to get the train timetable between stations.
     *
     * @param text   text of message
     * @param userId identifier of user
     * @param chatId identifier of chat
     */
    public void processRequestInputData(String text, long userId, long chatId) {
        RequestDTO request = RequestService.getRequest(userId);
        int current = request.getCurrent();
        if (current < ChatQuestion.getLength()) {

            ChatQuestion chatQuestion = ChatQuestion.values()[current];
            // TODO вынести методы для обработки ответов в отдельный сервис
            if (chatQuestion.equals(ChatQuestion.FROM)) {
                answerService.processFromAnswer(request, text, chatId);
            } else if (chatQuestion.equals(ChatQuestion.TO)) {
                answerService.processToAnswer(request, text, chatId);
            } else if (chatQuestion.equals(ChatQuestion.DATE)) {
                // TODO убрать руденантную функцию для получения списка
                //  ближайших поездов по вводу слова Да (либо изменить
                //  вывод данных для корреткной работы
                answerService.processDateAnswer(chatId, request, text);
            }
            current = request.getCurrent();
            request.setCurrent(++current);

            // if questions have been answered a confirmation message is displayed
            if (current == ChatQuestion.getLength()) {
                String confirmationRequestMessage =
                        MessageUtils.generateTextOfConfirmationRequestMessage(request);
                updateController.setChatMessageView(chatId, confirmationRequestMessage);
            }
        } else { /* depending on the answer to the confirmation of the response
            process the request of cancel it */
            if (text.equals(YES_MESSAGE.getText())) {
                processRequest(request, userId, chatId);
            } else if (text.equals(NO_MESSAGE.getText())) {
                updateController.processCancel(userId, chatId);
            }
        }
    }

    /**
     * Method for processing closure of input to get train
     * timetables between two stations.
     *
     * @param requestDTO input data to send request
     * @param userId     identifier of user
     */
    public void processRequest(RequestDTO requestDTO, long userId, long chatId) {
        requestDTO.setInputting(false);
        updateController.setChatMessageView(chatId, REQUEST_PROCESSING_MESSAGE);
        ArrayList<Timetable> timetables = yandexTimetableService.getTimetableBetweenTwoStations(requestDTO);
        Request fullRequest = modelMapper.map(requestDTO, Request.class);
        fullRequest.setTelegramUserId(userId);
        if (timetables.isEmpty()) {
            updateController.setChatMessageView(chatId, EMPTY_TIMETABLES_MESSAGE);
        } else {
            updateController.setChatMessageView(chatId, TIMETABLE_WAS_RECEIVED_MESSAGE);
            Response response = responseService.processResponse(chatId, timetables);
            fullRequest.setResponse(response);
            fullRequest.setSuccessfully(true);
        }
        save(fullRequest);
        removeRequest(userId);
    }

    /**
     * Method to put the request.
     *
     * @param userId  identifier of user
     * @param request request
     */
    public static void putRequest(Long userId, RequestDTO request) {
        RequestService.requests.put(userId, request);
    }

    /**
     * Method to get the request by userId.
     *
     * @param userId identifier of user
     * @return request
     */
    public static RequestDTO getRequest(Long userId) {
        return RequestService.requests.get(userId);
    }

    /**
     * Method to remove the request by userId.
     *
     * @param userId identifier of user
     */
    public static void removeRequest(Long userId) {
        RequestService.requests.remove(userId);
    }
}
