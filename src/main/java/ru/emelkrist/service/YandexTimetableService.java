package ru.emelkrist.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.emelkrist.dto.RequestDTO;
import ru.emelkrist.model.Timetable;

import java.util.ArrayList;

@Service
@Slf4j
public class YandexTimetableService {

    @Value("${yandex-schedules-api.timetable.between-two-stations.url}")
    private String url;
    @Value("${yandex-schedules-api.token}")
    private String token;

    /**
     * Method to get the list of train timetables between two stations.
     *
     * @param requestDTO request input data
     * @return list of train timetables
     */
    public ArrayList<Timetable> getTimetableBetweenTwoStations(RequestDTO requestDTO) {
        String urlWithData = fillUrl(requestDTO);
        ResponseEntity<String> response = sendRequestToGetTimetableBetweenTwoStations(urlWithData);
        // TODO добавить проверку json на наличие error и выводить пользователю text ошибки в чат
        ArrayList<Timetable> timetables = generateListOfTimetables(response.getBody());
        return timetables;
    }

    /**
     * Method to generate a list with train timetables between two stations.
     * Note: Receives data from json with timetable and parses it extracting
     * all the data that the applications needs.
     *
     * @param json json with timetable data
     * @return list of train timetables
     */
    private ArrayList<Timetable> generateListOfTimetables(String json) {
        ArrayList<Timetable> timetables = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode segmentsNode = rootNode.get("segments");
            if (segmentsNode.isArray()) {
                for (JsonNode segment : segmentsNode) {
                    Timetable timetable = new Timetable();

                    JsonNode threadNode = segment.get("thread");
                    timetable.setTrainNumber(threadNode.get("number").asText());
                    timetable.setTrainTitle(threadNode.get("title").asText());
                    timetable.setTrainUid(threadNode.get("uid").asText());

                    JsonNode fromNode = segment.get("from");
                    timetable.setFromStationTitle(fromNode.get("title").asText());
                    timetable.setFromStationCode(fromNode.get("code").asText());

                    JsonNode toNode = segment.get("to");
                    timetable.setToStationTitle(toNode.get("title").asText());
                    timetable.setToStationCode(toNode.get("code").asText());

                    timetable.setDeparture(segment.get("departure").asText());
                    timetable.setArrival(segment.get("arrival").asText());

                    JsonNode daysNode = segment.findValue("days");
                    if (daysNode != null) {
                        timetable.setDays(daysNode.asText());
                    }

                    timetables.add(timetable);
                }
            }

        } catch (JsonProcessingException e) {
            log.error("Parsing of timetable between two cities was failed: " + e.getMessage());
        }

        return timetables;
    }

    /**
     * Method for filling url parameters with request
     * input data from the application user.
     *
     * @param requestDTO request input data
     * @return filled url
     */
    private String fillUrl(RequestDTO requestDTO) {
        String urlWithData = String.format(url,
                token,
                requestDTO.getCodeFrom(),
                requestDTO.getCodeTo()
        );

        if (requestDTO.getDate() != null) {
            urlWithData = String.format(urlWithData + "&date=%s", requestDTO.getDate());
        }

        return urlWithData;
    }

    /**
     * Method for sending a request to the Yandex Schedules API to get
     * a timetable between two stations in JSON format.
     *
     * @return response entity with JSON data
     */
    private ResponseEntity<String> sendRequestToGetTimetableBetweenTwoStations(String urlWithData)
            throws HttpClientErrorException {
        log.debug("Sending GET request to: " + url);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(urlWithData, String.class);
    }
}
