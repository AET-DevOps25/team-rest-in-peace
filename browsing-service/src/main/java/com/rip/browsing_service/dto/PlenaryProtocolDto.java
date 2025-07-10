package com.rip.browsing_service.dto;

import java.util.List;

public record PlenaryProtocolDto(
    String id,
    String date,
    String title,
    String summary,
    int speakerCount,
    int totalWords,
    List<PlenaryProtocolPartyStatsDto> partyStats
) {}
