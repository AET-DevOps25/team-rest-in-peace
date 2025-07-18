package com.rip.browsing_service.dto;

public interface SpeechDto {
    String getParty();
    String getProtocolDate();
    Integer getWordCount();
    String getFirstName();
    String getLastName();
    String getAgendaItemTitle();
    String getTextSummary();
    String getTextPlain();
    String getProtocolName();
    Double getSimilarity();
}
