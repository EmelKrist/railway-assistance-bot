package ru.emelkrist.service;

import org.springframework.stereotype.Service;
import ru.emelkrist.model.Request;
import ru.emelkrist.repository.RequestRepository;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    public void save(Request request) {
        requestRepository.save(request);
    }
}
