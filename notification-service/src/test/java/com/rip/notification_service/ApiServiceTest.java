package com.rip.notification_service;

import com.rip.notification_service.dto.TestSubscriptionRequest;
import com.rip.notification_service.model.NotificationSetting;
import com.rip.notification_service.model.Person;
import com.rip.notification_service.repository.NotificationSettingRepository;
import com.rip.notification_service.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiServiceTest {

    @Mock
    private NotificationSettingRepository notificationSettingRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ApiService apiService;

    private Person testPerson;
    private List<String> testParties;

    @BeforeEach
    public void setup() {
        testPerson = new Person();
        testPerson.setId(1);
        testPerson.setFirstName("John");
        testPerson.setLastName("Doe");
        testPerson.setParty("Test Party");

        testParties = List.of("Party A", "Party B", "Test Party");
    }

    @Test
    public void testAddSubscription_Party_Success() {
        // Arrange
        TestSubscriptionRequest request = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PARTY")
                .setParty("Party A");

        when(personRepository.findDistinctParties()).thenReturn(Optional.of(testParties));

        // Act
        ApiService.Result result = apiService.addSubscription(request);

        // Assert
        assertTrue(result.success());
        assertNull(result.errorMessage());
        verify(notificationSettingRepository).save(any(NotificationSetting.class));
    }

    @Test
    public void testAddSubscription_Party_InvalidParty() {
        // Arrange
        TestSubscriptionRequest request = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PARTY")
                .setParty("Invalid Party");

        when(personRepository.findDistinctParties()).thenReturn(Optional.of(testParties));

        // Act
        ApiService.Result result = apiService.addSubscription(request);

        // Assert
        assertFalse(result.success());
        assertEquals("Unknown or missing party: Invalid Party", result.errorMessage());
        verify(notificationSettingRepository, never()).save(any(NotificationSetting.class));
    }

    @Test
    public void testAddSubscription_Person_Success() {
        // Arrange
        TestSubscriptionRequest request = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PERSON")
                .setPersonId(1);

        when(personRepository.findById(1)).thenReturn(Optional.of(testPerson));

        // Act
        ApiService.Result result = apiService.addSubscription(request);

        // Assert
        assertTrue(result.success());
        assertNull(result.errorMessage());
        verify(notificationSettingRepository).save(any(NotificationSetting.class));
    }

    @Test
    public void testAddSubscription_Person_InvalidPerson() {
        // Arrange
        TestSubscriptionRequest request = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PERSON")
                .setPersonId(999);

        when(personRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        ApiService.Result result = apiService.addSubscription(request);

        // Assert
        assertFalse(result.success());
        assertEquals("No person found with ID: 999", result.errorMessage());
        verify(notificationSettingRepository, never()).save(any(NotificationSetting.class));
    }

    @Test
    public void testAddSubscription_PlenaryProtocol_Success() {
        // Arrange
        TestSubscriptionRequest request = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PLENARY_PROTOCOL");

        // Act
        ApiService.Result result = apiService.addSubscription(request);

        // Assert
        assertTrue(result.success());
        assertNull(result.errorMessage());
        verify(notificationSettingRepository).save(any(NotificationSetting.class));
    }

    @Test
    public void testAddSubscription_InvalidType() {
        // Arrange
        TestSubscriptionRequest request = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("INVALID_TYPE");

        // Act
        ApiService.Result result = apiService.addSubscription(request);

        // Assert
        assertFalse(result.success());
        assertEquals("Unsupported subscription type: INVALID_TYPE", result.errorMessage());
        verify(notificationSettingRepository, never()).save(any(NotificationSetting.class));
    }

    @Test
    public void testDeleteAllSubscriptions_Success() {
        // Arrange
        String email = "user@example.com";
        doNothing().when(notificationSettingRepository).deleteAllByEmail(email);

        // Act
        ApiService.Result result = apiService.deleteAllSubscriptions(email);

        // Assert
        assertTrue(result.success());
        assertNull(result.errorMessage());
        verify(notificationSettingRepository).deleteAllByEmail(email);
    }

    @Test
    public void testDeleteAllSubscriptions_Error() {
        // Arrange
        String email = "user@example.com";
        doThrow(new RuntimeException("Database error")).when(notificationSettingRepository).deleteAllByEmail(email);

        // Act
        ApiService.Result result = apiService.deleteAllSubscriptions(email);

        // Assert
        assertFalse(result.success());
        assertEquals("Database error", result.errorMessage());
        verify(notificationSettingRepository).deleteAllByEmail(email);
    }

    @Test
    public void testNotifyAll_Async_Success() throws Exception {
        // Arrange
        List<Integer> plenaryProtocolIds = List.of(1, 2, 3);
        List<Person> speakers = List.of(testPerson);
        List<NotificationSetting> plenarySettings = List.of(
                new NotificationSetting("user1@example.com", "PLENARY_PROTOCOL")
        );
        List<NotificationSetting> partySettings = List.of(
                new NotificationSetting("user2@example.com", "PARTY", "Test Party")
        );
        List<NotificationSetting> personSettings = List.of(
                new NotificationSetting("user3@example.com", "PERSON", testPerson)
        );

        when(personRepository.findDistinctPersonsByPlenaryProtocolIds(plenaryProtocolIds)).thenReturn(speakers);
        when(notificationSettingRepository.findAllByType("PLENARY_PROTOCOL")).thenReturn(plenarySettings);
        when(notificationSettingRepository.findAllByType("PARTY")).thenReturn(partySettings);
        when(notificationSettingRepository.findAllByType("PERSON")).thenReturn(personSettings);
        doNothing().when(emailService).sendPlenaryProtocolNotification(anyString(), anyInt());
        doNothing().when(emailService).sendPartyNotification(anyString(), anySet());
        doNothing().when(emailService).sendPersonNotification(anyString(), anySet());

        // Act
        apiService.notifyAllAsync(plenaryProtocolIds);

        // Assert
        verify(emailService).sendPlenaryProtocolNotification("user1@example.com", 3);
        verify(emailService).sendPartyNotification(eq("user2@example.com"), anySet());
        verify(emailService).sendPersonNotification(eq("user3@example.com"), anySet());
    }
}
