package com.rip.browsing_service.repository;

import com.rip.browsing_service.model.PlenaryProtocol;
import com.rip.browsing_service.model.Person;
import com.rip.browsing_service.model.Speech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeechRepository extends JpaRepository<Speech, Integer> {

    @Query("SELECT s.textPlain FROM Speech s")
    List<String> findAllTextPlain();
}