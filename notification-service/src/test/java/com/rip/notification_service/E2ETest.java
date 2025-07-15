package com.rip.notification_service;

import com.rip.notification_service.dto.TestNotificationRequest;
import com.rip.notification_service.dto.TestSubscriptionRequest;
import com.rip.notification_service.model.AgendaItem;
import com.rip.notification_service.model.Person;
import com.rip.notification_service.model.PlenaryProtocol;
import com.rip.notification_service.model.Speech;
import com.rip.notification_service.repository.AgendaItemRepository;
import com.rip.notification_service.repository.NotificationSettingRepository;
import com.rip.notification_service.repository.PersonRepository;
import com.rip.notification_service.repository.PlenaryProtocolRepository;
import com.rip.notification_service.repository.SpeechRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * End-to-End tests for the notification service.
 * These tests simulate real user flows and verify the entire system works correctly.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(E2ETest.TestConfig.class)
public class E2ETest {

    @EnableAsync
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public EmailService emailService() {
            return Mockito.mock(EmailService.class);
        }

        @Bean
        public Executor taskExecutor() {
            ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
            exec.setCorePoolSize(1);
            exec.setMaxPoolSize(1);
            exec.afterPropertiesSet();
            return exec;
        }
    }


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private NotificationSettingRepository notificationSettingRepository;

    @Autowired
    private PlenaryProtocolRepository plenaryProtocolRepository;

    @Autowired
    private AgendaItemRepository agendaItemRepository;

    @Autowired
    private SpeechRepository speechRepository;

    @Autowired
    private EmailService emailService;

    private Person testPerson;
    private HttpHeaders headers;

    @BeforeEach
    public void setup() throws Exception {
        // Set up headers for JSON requests
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Clear any existing data
        notificationSettingRepository.deleteAll();
        speechRepository.deleteAll();
        agendaItemRepository.deleteAll();
        plenaryProtocolRepository.deleteAll();

        // Create a test person
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
        testPerson.setParty("Test Party");
        personRepository.save(testPerson);

        // Create a plenary protocol
        PlenaryProtocol plenaryProtocol = new PlenaryProtocol();
        plenaryProtocol.setId(1);
        plenaryProtocol.setElectionPeriod(20);
        plenaryProtocol.setDocumentNumber(123);
        plenaryProtocol.setPublisher("Test Publisher");
        plenaryProtocolRepository.save(plenaryProtocol);

        // Create an agenda item
        AgendaItem agendaItem = new AgendaItem();
        agendaItem.setName("Test Agenda Item");
        agendaItem.setTitle("Test Title");
        agendaItem.setPlenaryProtocol(plenaryProtocol);
        agendaItemRepository.save(agendaItem);

        // Create a speech
        Speech speech = new Speech();
        speech.setId(1);
        speech.setAgendaItem(agendaItem);
        speech.setPerson(testPerson);
        speech.setTextPlain("This is a test speech");
        speechRepository.save(speech);

        // Configure the email service mock
        doNothing().when(emailService).sendPlenaryProtocolNotification(anyString(), anyInt());
        doNothing().when(emailService).sendPartyNotification(anyString(), anySet());
        doNothing().when(emailService).sendPersonNotification(anyString(), anySet());
    }

    @Test
    public void testHealthEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/health", Map.class);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody().get("healthy"));
    }

    @Test
    public void testSubscriptionAndNotificationFlow() throws Exception {
        // 1. Subscribe to a party
        TestSubscriptionRequest partySubscription = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PARTY")
                .setParty("Test Party");

        HttpEntity<TestSubscriptionRequest> partyRequest = new HttpEntity<>(partySubscription, headers);
        ResponseEntity<Map> partyResponse = restTemplate.exchange(
                "http://localhost:" + port + "/subscribe",
                HttpMethod.POST,
                partyRequest,
                Map.class);

        assertEquals(200, partyResponse.getStatusCodeValue());
        assertTrue((Boolean) partyResponse.getBody().get("success"));

        // 2. Subscribe to a person
        TestSubscriptionRequest personSubscription = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PERSON")
                .setPersonId(1);

        HttpEntity<TestSubscriptionRequest> personRequest = new HttpEntity<>(personSubscription, headers);
        ResponseEntity<Map> personResponse = restTemplate.exchange(
                "http://localhost:" + port + "/subscribe",
                HttpMethod.POST,
                personRequest,
                Map.class);

        assertEquals(200, personResponse.getStatusCodeValue());
        assertTrue((Boolean) personResponse.getBody().get("success"));

        // 3. Verify subscriptions were created
        assertEquals(2, notificationSettingRepository.findAll().size());

        // 4. Send a notification
        TestNotificationRequest notificationRequest = new TestNotificationRequest()
                .setPlenaryProtocolIds(List.of(1, 2, 3));

        HttpEntity<TestNotificationRequest> notifyRequest = new HttpEntity<>(notificationRequest, headers);
        ResponseEntity<Map> notifyResponse = restTemplate.exchange(
                "http://localhost:" + port + "/notify",
                HttpMethod.POST,
                notifyRequest,
                Map.class);

        assertEquals(202, notifyResponse.getStatusCodeValue());

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(emailService, times(1))
                            .sendPartyNotification(eq("user@example.com"), anySet());
                    verify(emailService, times(1))
                            .sendPersonNotification(eq("user@example.com"), anySet());
                });
        // 6. Unsubscribe
        ResponseEntity<Map> unsubscribeResponse = restTemplate.exchange(
                "http://localhost:" + port + "/unsubscribe?email=user@example.com",
                HttpMethod.DELETE,
                null,
                Map.class);

        assertEquals(200, unsubscribeResponse.getStatusCodeValue());
        assertTrue((Boolean) unsubscribeResponse.getBody().get("success"));

        // 7. Verify subscriptions were deleted
        assertEquals(0, notificationSettingRepository.findAll().size());
    }
}
