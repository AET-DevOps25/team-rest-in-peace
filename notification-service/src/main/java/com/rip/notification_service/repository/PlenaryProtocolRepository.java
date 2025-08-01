package com.rip.notification_service.repository;

import com.rip.notification_service.model.PlenaryProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlenaryProtocolRepository extends JpaRepository<PlenaryProtocol, Integer> {

    Optional<PlenaryProtocol> findByElectionPeriodAndDocumentNumberAndPublisher(
            Integer electionPeriod, Integer documentNumber, String publisher);

    List<PlenaryProtocol> findByElectionPeriod(Integer electionPeriod);

    List<PlenaryProtocol> findByPublisher(String publisher);
}