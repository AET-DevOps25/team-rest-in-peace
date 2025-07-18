package com.rip.browsing_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rip.browsing_service.dto.*;
import com.rip.browsing_service.model.PlenaryProtocol;
import com.rip.browsing_service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    @Value("${genai.service.baseurl}")
    private String genaiBaseUrl;

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
                    protocol.getDate() != null ? protocol.getDate().toString() : null,
                    createProtocolName(protocol),
                    protocol.getSummary() != null ? protocol.getSummary() : "",
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

    public Page<SpeechDto> getAllSpeechDetails(Pageable pageable, String party, List<Integer> speakerIds, Integer plenaryProtocolId, String searchText, float searchSimilarityThreshold) {
        if (searchText != null && !searchText.isBlank()) {
            try {
                URI genaiEmbeddingUrlPath = UriComponentsBuilder.fromUriString(genaiBaseUrl).path("embedding").queryParam("text", searchText).build().toUri();
                HttpRequest request = HttpRequest.newBuilder(genaiEmbeddingUrlPath).GET().header("Accept", "application/json").build();
                var client = HttpClient.newHttpClient();

                var response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check if request was successful
                if (response.statusCode() == 200) {
                    var objectMapper = new ObjectMapper();
                    var responseJson = objectMapper.readTree(response.body());
                    String embedding = responseJson.get("embedding").toString();

                    return speechRepository.findAllSpeechDetailsFilteredOrderedByEmbeddingSimilarity(
                            pageable, party, speakerIds, plenaryProtocolId, embedding, searchSimilarityThreshold);
                } else {
                    logger.error("Failed to get embedding from genAI service: {}", response.body());
                }
            } catch (Exception e) {
                logger.error("Error while getting embedding from genAI service", e);
            }
        }
        return speechRepository.findAllSpeechDetailsFiltered(pageable, party, speakerIds, plenaryProtocolId);
    }

    public String getPlenaryProtocolName(int id) {
        PlenaryProtocol protocol = plenaryProtocolRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plenary protocol not found with id: " + id));

        return createProtocolName(protocol);
    }

    public String getSpeakerName(int id) {
        return personRepository.findById(id)
                .map(person -> {
                    String party = person.getParty() != null ? person.getParty() : "Unbekannt";
                    return person.getFirstName() + " " + person.getLastName() + " (" + party + ")";
                })
                .orElseThrow(() -> new NoSuchElementException("Person not found with id: " + id));
    }
}
