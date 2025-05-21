package com.example.data_fetching_service.repository;

import com.example.data_fetching_service.model.PlenaryProtocol;
import com.example.data_fetching_service.model.Person;
import com.example.data_fetching_service.model.Speech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeechRepository extends JpaRepository<Speech, Integer> {
    
    List<Speech> findByPlenaryProtocol(PlenaryProtocol plenaryProtocol);
    
    List<Speech> findBySpeaker(Person speaker);
    
    List<Speech> findByPlenaryProtocolAndSpeaker(PlenaryProtocol plenaryProtocol, Person speaker);
}