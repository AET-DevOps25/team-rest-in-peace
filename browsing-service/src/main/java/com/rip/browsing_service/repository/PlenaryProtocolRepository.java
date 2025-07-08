package com.rip.browsing_service.repository;

import com.rip.browsing_service.model.PlenaryProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlenaryProtocolRepository extends JpaRepository<PlenaryProtocol, Integer> {

}