package com.rip.notification_service.repository;

import com.rip.notification_service.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT DISTINCT p.party FROM Person p")
    Optional<List<String>> findDistinctParties();

    @Override
    Optional<Person> findById(Integer integer);

    @Query("""
                SELECT DISTINCT s.person
                FROM Speech s
                JOIN s.agendaItem ai
                JOIN ai.plenaryProtocol pp
                WHERE pp.id IN :plenaryProtocolIds
                  AND s.person.party IS NOT NULL
            """)
    List<Person> findDistinctPersonsByPlenaryProtocolIds(@Param("plenaryProtocolIds") List<Integer> plenaryProtocolIds);
}