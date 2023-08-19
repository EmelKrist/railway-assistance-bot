package ru.emelkrist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.emelkrist.dto.RequestDTO;

@Service
@Slf4j
public class YandexTimetableService {

    @Value("${yandex-schedules-api.timetable.between-two-stations.url}")
    private String url;
    @Value("${yandex-schedules-api.token}")
    private String token;

    /**
     * Method to get timetable data between two stations.
     *
     * @param requestDTO request input data
     * @return timetable in json format
     */
    public String getTimetableBetweenTwoStations(RequestDTO requestDTO) {
        String urlWithData = fillUrl(requestDTO);
        ResponseEntity<String> response = sendRequestToGetTimetableBetweenTwoStations(urlWithData);

        return response.getBody();
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
