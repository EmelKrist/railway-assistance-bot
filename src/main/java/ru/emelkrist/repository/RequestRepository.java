package ru.emelkrist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.emelkrist.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
}
