package ru.emelkrist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.emelkrist.model.Response;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    Optional<Response> findByChatIdAndMessageId(long chatId, long messageId);
    List<Response> findAllByDateBefore(LocalDate date);
}
