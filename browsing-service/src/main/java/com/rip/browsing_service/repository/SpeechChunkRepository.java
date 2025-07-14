package com.rip.browsing_service.repository;

import com.rip.browsing_service.model.Speech;
import com.rip.browsing_service.model.SpeechChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeechChunkRepository extends JpaRepository<SpeechChunk, Integer> {
}