package com.rip.notification_service.repository;

import com.rip.notification_service.model.Speech;
import com.rip.notification_service.model.SpeechChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeechChunkRepository extends JpaRepository<SpeechChunk, Integer> {
    
    List<SpeechChunk> findBySpeech(Speech speech);
    
    List<SpeechChunk> findBySpeechAndType(Speech speech, String type);
    
    List<SpeechChunk> findByType(String type);
}