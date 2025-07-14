package com.rip.browsing_service;

import com.rip.browsing_service.dto.PlenaryProtocolDto;
import com.rip.browsing_service.dto.SpeakerStatisticDto;
import com.rip.browsing_service.dto.StatisticsDto;
import com.rip.browsing_service.model.PlenaryProtocol;
import com.rip.browsing_service.repository.AgendaItemRepository;
import com.rip.browsing_service.repository.PersonRepository;
import com.rip.browsing_service.repository.PlenaryProtocolRepository;
import com.rip.browsing_service.repository.SpeechRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private PlenaryProtocolRepository plenaryProtocolRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private SpeechRepository speechRepository;

    @Mock
    private AgendaItemRepository agendaItemRepository;

    private BrowsingService browsingService;

    private Controller controller;

    @BeforeEach
    void setUp() {
        browsingService = new BrowsingService();
        ReflectionTestUtils.setField(browsingService, "plenaryProtocolRepository", plenaryProtocolRepository);
        ReflectionTestUtils.setField(browsingService, "personRepository", personRepository);
        ReflectionTestUtils.setField(browsingService, "speechRepository", speechRepository);
        ReflectionTestUtils.setField(browsingService, "agendaItemRepository", agendaItemRepository);
        controller = new Controller();
        ReflectionTestUtils.setField(controller, "browsingService", browsingService);
    }

    @Test
    void getStatisticsReturnsValidStatisticsDto() {
        when(plenaryProtocolRepository.count()).thenReturn(10L);
        when(personRepository.count()).thenReturn(20L);
        when(speechRepository.findAllTextPlain()).thenReturn(List.of("word1 word2", "word3 word4 word5"));
        when(personRepository.countDistinctParties()).thenReturn(4L);

        ResponseEntity<StatisticsDto> response = controller.getStatistics();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new StatisticsDto(10, 20, 5, 4), response.getBody());
    }

    @Test
    void getAllPlenaryProtocolsReturnsSortedPageWhenValidParametersProvided() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());
        PlenaryProtocol protocol1 = new PlenaryProtocol();
        ReflectionTestUtils.setField(protocol1, "id", 1);
        ReflectionTestUtils.setField(protocol1, "date", java.sql.Date.valueOf("2023-10-01"));
        ReflectionTestUtils.setField(protocol1, "publisher", "BT");
        ReflectionTestUtils.setField(protocol1, "documentNumber", 1);
        ReflectionTestUtils.setField(protocol1, "electionPeriod", 20);

        PlenaryProtocol protocol2 = new PlenaryProtocol();
        ReflectionTestUtils.setField(protocol2, "id", 2);
        ReflectionTestUtils.setField(protocol2, "date", java.sql.Date.valueOf("2023-10-02"));
        ReflectionTestUtils.setField(protocol2, "publisher", "BT");
        ReflectionTestUtils.setField(protocol2, "documentNumber", 2);
        ReflectionTestUtils.setField(protocol2, "electionPeriod", 21);

        Page<PlenaryProtocol> mockPage = new PageImpl<>(List.of(protocol1, protocol2));
        when(plenaryProtocolRepository.findAllOrderByDateDescNullsLast(pageable)).thenReturn(mockPage);
        when(agendaItemRepository.findAllIdsByPlenaryProtocolId(1)).thenReturn(List.of(1, 2));
        when(agendaItemRepository.findAllIdsByPlenaryProtocolId(2)).thenReturn(List.of(3, 4));
        when(speechRepository.countDistinctPersonIdsByAgendaItemIds(List.of(1, 2))).thenReturn(5L);
        when(speechRepository.countDistinctPersonIdsByAgendaItemIds(List.of(3, 4))).thenReturn(3L);
        when(speechRepository.findAllTextPlainByAgendaItemIds(List.of(1, 2))).thenReturn(List.of("word1 word2", "word3"));
        when(speechRepository.findAllTextPlainByAgendaItemIds(List.of(3, 4))).thenReturn(List.of("word4 word5 word6"));

        ResponseEntity<Page<PlenaryProtocolDto>> response = controller.getAllPlenaryProtocols(0, 10, "date", "desc");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());
    }

    @Test
    void getAllSpeakerStatisticsReturnsPageWhenValidParametersProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SpeakerStatisticDto> mockPage = new PageImpl<>(List.of(mock(SpeakerStatisticDto.class), mock(SpeakerStatisticDto.class)));
        when(speechRepository.findAllSpeakerStatistics(pageable)).thenReturn(mockPage);

        ResponseEntity<Page<SpeakerStatisticDto>> response = controller.getAllSpeakerStatistics(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());
    }

    @Test
    void getAllSpeakerStatisticsReturnsEmptyPageWhenNoStatisticsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SpeakerStatisticDto> mockPage = new PageImpl<>(List.of());
        when(speechRepository.findAllSpeakerStatistics(pageable)).thenReturn(mockPage);

        ResponseEntity<Page<SpeakerStatisticDto>> response = controller.getAllSpeakerStatistics(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getContent().isEmpty());
    }
}
