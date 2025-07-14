package com.rip.notification_service.repository;

import com.rip.notification_service.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testFindById() {
        // Create a test person
        Person person = new Person();
        person.setId(1);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setParty("Test Party");
        
        // Save the person
        personRepository.save(person);
        
        // Find the person by ID
        Optional<Person> foundPerson = personRepository.findById(1);
        
        // Verify the person was found
        assertTrue(foundPerson.isPresent());
        assertEquals("John", foundPerson.get().getFirstName());
        assertEquals("Doe", foundPerson.get().getLastName());
        assertEquals("Test Party", foundPerson.get().getParty());
    }
    
    @Test
    public void testFindDistinctParties() {
        // Create test persons with different parties
        Person person1 = new Person();
        person1.setId(1);
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setParty("Party A");
        
        Person person2 = new Person();
        person2.setId(2);
        person2.setFirstName("Jane");
        person2.setLastName("Smith");
        person2.setParty("Party B");
        
        Person person3 = new Person();
        person3.setId(3);
        person3.setFirstName("Bob");
        person3.setLastName("Johnson");
        person3.setParty("Party A");
        
        // Save the persons
        personRepository.saveAll(List.of(person1, person2, person3));
        
        // Find distinct parties
        Optional<List<String>> parties = personRepository.findDistinctParties();
        
        // Verify the parties were found
        assertTrue(parties.isPresent());
        assertEquals(2, parties.get().size());
        assertTrue(parties.get().contains("Party A"));
        assertTrue(parties.get().contains("Party B"));
    }
    
    // Note: Testing findDistinctPersonsByPlenaryProtocolIds would require setting up
    // more complex data with related entities (AgendaItem, PlenaryProtocol, Speech).
    // This would be better suited for an integration test with a more complete setup.
}