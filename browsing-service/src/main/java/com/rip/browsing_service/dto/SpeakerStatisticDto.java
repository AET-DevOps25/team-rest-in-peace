package com.rip.browsing_service.dto;

public interface SpeakerStatisticDto {
    Integer getPersonId();
    String getFirstName();
    String getLastName();
    String getParty();
    String getLastSpeechDate();
    Long getSpeechCount();
    Long getTotalWords();
}
