package com.rip.browsing_service.repository;

import com.rip.browsing_service.model.PlenaryProtocol;
import com.rip.browsing_service.model.Person;
import com.rip.browsing_service.model.Speech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeechRepository extends JpaRepository<Speech, Integer> {

    @Query("SELECT s.textPlain FROM Speech s")
    List<String> findAllTextPlain();

    // Fetch textPlain for specific agendaItemIds
    @Query("SELECT s.textPlain FROM Speech s WHERE s.agendaItem.id IN :agendaItemIds")
    List<String> findAllTextPlainByAgendaItemIds(List<Integer> agendaItemIds);

    @Query("SELECT COUNT(DISTINCT s.person.id) FROM Speech s WHERE s.agendaItem.id IN :agendaItemIds")
    long countDistinctPersonIdsByAgendaItemIds(List<Integer> agendaItemIds);

    @Query("""
        SELECT
        	p.party,
        	s.textPlain
        FROM
        	Speech s,
        	Person p
        WHERE s.agendaItem.id IN :agendaItemIds
        	AND s.person.id = p.id
        """)
    List<Object[]> findPartyToSpeechTextByAgendaItemIds(List<Integer> agendaItemIds);
}