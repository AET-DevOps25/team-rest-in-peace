package com.rip.browsing_service;

import com.rip.browsing_service.dto.*;
import com.rip.browsing_service.model.PlenaryProtocol;
import com.rip.browsing_service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrowsingService {

    private static final Logger logger = LoggerFactory.getLogger(BrowsingService.class);

    @Autowired
    private PlenaryProtocolRepository plenaryProtocolRepository;

    @Autowired
    private SpeechRepository speechRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SpeechChunkRepository speechChunkRepository;

    @Autowired
    private AgendaItemRepository agendaItemRepository;

    public StatisticsDto getStatistics() {
        logger.info("Fetching statistics...");

        long plenaryCount = plenaryProtocolRepository.count();
        long speakerCount = personRepository.count();
        long wordCount = speechRepository.findAllTextPlain().stream()
                .filter(Objects::nonNull)
                .mapToLong(text -> text.split("\\s+").length)
                .sum();
        long partyCount = personRepository.countDistinctParties();

        StatisticsDto statistics = new StatisticsDto((int) plenaryCount, (int) speakerCount, (int) wordCount, (int) partyCount);

        logger.info("Statistics fetched successfully: {}", statistics);
        return statistics;
    }

    public Page<PlenaryProtocolDto> getAllPlenaryProtocols(Pageable pageable) {
        Page<PlenaryProtocol> protocolsPage = plenaryProtocolRepository.findAllOrderByDateDescNullsLast(pageable);
        return protocolsPage.map(protocol -> {
            List<Integer> allAgendaItemIds = agendaItemRepository.findAllIdsByPlenaryProtocolId(protocol.getId());

            long speakerCount = speechRepository.countDistinctPersonIdsByAgendaItemIds(allAgendaItemIds);

            int totalWords = speechRepository.findAllTextPlainByAgendaItemIds(allAgendaItemIds)
                    .stream()
                    .filter(Objects::nonNull)
                    .mapToInt(text -> text.split("\\s+").length)
                    .sum();

            Map<String, Integer> partyWordCounts = accumulatePartyWordCounts(allAgendaItemIds);

            List<PlenaryProtocolPartyStatsDto> partyStats = partyWordCounts.entrySet().stream()
                    .map(entry -> {
                        String party = entry.getKey();
                        int words = entry.getValue();
                        int percentage = totalWords == 0 ? 0 : (int) Math.round(words * 100.0 / totalWords);

                        return new PlenaryProtocolPartyStatsDto(party, words, percentage);
                    })
                    .collect(Collectors.toList());

            return new PlenaryProtocolDto(
                    protocol.getId().toString(),
                    protocol.getDate() != null ?  protocol.getDate().toString() : null,
                    createProtocolName(protocol),
                    null,
                    (int) speakerCount,
                    totalWords,
                    partyStats
            );
        });
    }

    private Map<String, Integer> accumulatePartyWordCounts(List<Integer> agendaItemIds) {
        List<Object[]> results = speechRepository.findPartyToSpeechTextByAgendaItemIds(agendaItemIds);

        Map<String, Integer> partyWordCounts = new HashMap<>();

        for (Object[] row : results) {
            String party = (String) row[0];
            String text = (String) row[1];

            if (text != null && !text.isBlank()) {
                // Calculate word count (simple split by whitespace)
                int wordCount = text.trim().split("\\s+").length;

                partyWordCounts.merge(party, wordCount, Integer::sum);
            }
        }

        return partyWordCounts;
    }

    private String createProtocolName(PlenaryProtocol protocol) {
        String publisherName = protocol.getPublisher().equalsIgnoreCase("BR")
                ? "Bundesrat"
                : "Bundestag";

        return String.format("%d. Sitzung des %d. Deutschen %s",
                protocol.getDocumentNumber(),
                protocol.getElectionPeriod(),
                publisherName);
    }

    public Page<SpeakerStatisticDto> getAllSpeakerStatistics(Pageable pageable) {
        return speechRepository.findAllSpeakerStatistics(pageable);
    }

    public Page<SpeechDto> getAllSpeechDetails(Pageable pageable, String party, Integer speakerId, Integer plenaryProtocolId) {
        return speechRepository.findAllSpeechDetailsFiltered(pageable, party, speakerId, plenaryProtocolId);
    }
}
