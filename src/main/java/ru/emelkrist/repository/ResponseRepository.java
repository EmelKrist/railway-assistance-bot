package ru.emelkrist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.emelkrist.model.Response;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
}
