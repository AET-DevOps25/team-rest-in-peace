package com.rip.notification_service.repository;

import com.rip.notification_service.model.NotificationSetting;
import com.rip.notification_service.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class NotificationSettingRepositoryTest {

    @Autowired
    private NotificationSettingRepository notificationSettingRepository;
    
    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testFindAllByType() {
        // Create test notification settings with different types
        NotificationSetting setting1 = new NotificationSetting("user1@example.com", "PARTY", "Party A");
        NotificationSetting setting2 = new NotificationSetting("user2@example.com", "PERSON");
        NotificationSetting setting3 = new NotificationSetting("user3@example.com", "PLENARY_PROTOCOL");
        NotificationSetting setting4 = new NotificationSetting("user4@example.com", "PARTY", "Party B");
        
        // Save the settings
        notificationSettingRepository.saveAll(List.of(setting1, setting2, setting3, setting4));
        
        // Find settings by type
        List<NotificationSetting> partySettings = notificationSettingRepository.findAllByType("PARTY");
        List<NotificationSetting> personSettings = notificationSettingRepository.findAllByType("PERSON");
        List<NotificationSetting> plenarySettings = notificationSettingRepository.findAllByType("PLENARY_PROTOCOL");
        
        // Verify the settings were found
        assertEquals(2, partySettings.size());
        assertEquals(1, personSettings.size());
        assertEquals(1, plenarySettings.size());
        
        // Verify the content of the settings
        assertTrue(partySettings.stream().anyMatch(s -> s.getEmail().equals("user1@example.com") && "Party A".equals(s.getParty())));
        assertTrue(partySettings.stream().anyMatch(s -> s.getEmail().equals("user4@example.com") && "Party B".equals(s.getParty())));
        assertEquals("user2@example.com", personSettings.get(0).getEmail());
        assertEquals("user3@example.com", plenarySettings.get(0).getEmail());
    }
    
    @Test
    public void testDeleteAllByEmail() {
        // Create test notification settings for the same email
        NotificationSetting setting1 = new NotificationSetting("user@example.com", "PARTY", "Party A");
        NotificationSetting setting2 = new NotificationSetting("user@example.com", "PLENARY_PROTOCOL");
        NotificationSetting setting3 = new NotificationSetting("other@example.com", "PARTY", "Party B");
        
        // Save the settings
        notificationSettingRepository.saveAll(List.of(setting1, setting2, setting3));
        
        // Delete all settings for one email
        notificationSettingRepository.deleteAllByEmail("user@example.com");
        
        // Find all settings
        List<NotificationSetting> allSettings = notificationSettingRepository.findAll();
        
        // Verify only the settings for the other email remain
        assertEquals(1, allSettings.size());
        assertEquals("other@example.com", allSettings.get(0).getEmail());
        assertEquals("Party B", allSettings.get(0).getParty());
    }
    
    @Test
    public void testPersonNotificationSetting() {
        // Create a test person
        Person person = new Person();
        person.setId(1);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setParty("Test Party");
        
        // Save the person
        personRepository.save(person);
        
        // Create a notification setting for the person
        NotificationSetting setting = new NotificationSetting("user@example.com", "PERSON", person);
        
        // Save the setting
        notificationSettingRepository.save(setting);
        
        // Find settings by type
        List<NotificationSetting> personSettings = notificationSettingRepository.findAllByType("PERSON");
        
        // Verify the setting was found
        assertEquals(1, personSettings.size());
        assertEquals("user@example.com", personSettings.get(0).getEmail());
        assertNotNull(personSettings.get(0).getPerson());
        assertEquals(1, personSettings.get(0).getPerson().getId());
        assertEquals("John", personSettings.get(0).getPerson().getFirstName());
        assertEquals("Doe", personSettings.get(0).getPerson().getLastName());
    }
}