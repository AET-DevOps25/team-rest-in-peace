package com.rip.browsing_service;

import com.rip.browsing_service.dto.StatisticsDto;
import com.rip.browsing_service.repository.PersonRepository;
import com.rip.browsing_service.repository.PlenaryProtocolRepository;
import com.rip.browsing_service.repository.SpeechRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BrowsingService {

    private static final Logger logger = LoggerFactory.getLogger(BrowsingService.class);

    @Autowired
    private PlenaryProtocolRepository plenaryProtocolRepository;

    @Autowired
    private SpeechRepository speechRepository;

    @Autowired
    private PersonRepository personRepository;

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

}
