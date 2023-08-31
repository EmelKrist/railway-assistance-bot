package ru.emelkrist.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class YandexEncodingService {

    @Value("${yandex-schedules-api.all-stations.url}")
    private String url;
    @Value("${yandex-schedules-api.token}")
    private String token;
    private static HashMap<String, String> cityCodes;

    /**
     * Method for getting city code in Yandex Schedules encoding
     * system by city name.
     *
     * @param cityName name of the city whose code need to get
     * @return optional of city code
     */
    public Optional<String> getCityCodeByCityName(String cityName) {
        // loop through all city codes
        for (Map.Entry<String, String> cityCode : cityCodes.entrySet()) {
            if (cityCode.getKey().equals(cityName)) {
                log.debug("City code for " + cityName + " was found: " + cityCode.getValue());
                return Optional.of(cityCode.getValue());
            }
        }
        log.debug("City not found for " + cityName);
        return Optional.empty();
    }

    /**
     * Method to generating a map with supported city codes
     * in Yandex Schedules encoding.
     * Note: Receives data from the Yandex Schedules API and parses it
     * extracting the names of all cities with their yandex codes into
     * map of city codes.
     */
    public void generateMapOfCityCodes() {
        try {
            ResponseEntity<String> response = sendRequestToGetListOfStations();
            log.debug("Parsing JSON data to get the names of all " +
                    "supported cities with their yandex codes");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            HashMap<String, String> mapOfCityCodes = new HashMap<>();
            // get all countries
            JsonNode countriesNode = rootNode.get("countries");
            if (countriesNode.isArray()) {
                // loop through all countries
                for (JsonNode countryNode : countriesNode) {
                    // get all regions of current country
                    JsonNode regionsNode = countryNode.get("regions");
                    if (regionsNode.isArray()) {
                        // loop through all regions
                        for (JsonNode regionNode : regionsNode) {
                            // get all settlements of current region
                            JsonNode settlementsNode = regionNode.get("settlements");
                            if (settlementsNode.isArray()) {
                                // loop through all settlements
                                for (JsonNode settlementNode : settlementsNode) {
                                    // put all settlements and its yandex codes into map of city codes
                                    String cityName = settlementNode.get("title").asText();
                                    if (!cityName.isBlank()) {
                                        String yandexCode = settlementNode.get("codes").get("yandex_code").asText();
                                        mapOfCityCodes.put(cityName, yandexCode);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            log.info("Supported city codes was successfully initialized!");
            cityCodes = mapOfCityCodes;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Sending request was interrupted: " + e.getMessage());
        } catch (IOException e) {
            log.error("Initialization of supported city codes was failed: " + e.getMessage());
        }
    }

    /**
     * Method for sending a request to get a list of stations supported by
     * the Yandex Schedules API in JSON format.
     *
     * @return response entity with JSON data
     */
    private ResponseEntity<String> sendRequestToGetListOfStations() throws HttpServerErrorException, HttpClientErrorException {
        log.debug("Sending GET request to: " + url);
        RestTemplate restTemplate = new RestTemplate();
        String urlWithToken = String.format(url, token);
        return restTemplate.getForEntity(urlWithToken, String.class);
    }
}
