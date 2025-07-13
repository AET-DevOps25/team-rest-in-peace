package com.rip.notification_service.repository;

import com.rip.notification_service.model.Speech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeechRepository extends JpaRepository<Speech, Integer> {
}