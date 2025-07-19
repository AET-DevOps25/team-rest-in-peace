package com.rip.browsing_service.dto;

public interface PartyStatisticsDto {
    String getParty();
    Long getSpeechCount();
    Long getTotalWords();
    Long getPersonCount();
    String getLastSpeechDate();
}
