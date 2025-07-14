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

    @Query(
            value = """
                    SELECT DISTINCT p.*
                    FROM speech s
                      JOIN agenda_item ai  ON s.agenda_item_id     = ai.id
                      JOIN plenary_protocol pp ON ai.plenary_protocol_id = pp.id
                      JOIN person p        ON s.person_id           = p.id
                    WHERE pp.id IN (:plenaryProtocolIds)
                      AND p.party IS NOT NULL
                    """,
            nativeQuery = true
    )
    List<Person> findDistinctPersonsByPlenaryProtocolIds(@Param("plenaryProtocolIds") List<Integer> plenaryProtocolIds);

}