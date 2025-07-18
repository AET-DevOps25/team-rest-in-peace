package com.rip.browsing_service.repository;

import com.rip.browsing_service.dto.PartyStatisticsDto;
import com.rip.browsing_service.dto.SpeakerStatisticDto;
import com.rip.browsing_service.dto.SpeechDto;
import com.rip.browsing_service.model.Speech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
            SELECT
                p.id as personId,
                p.first_name as firstName,
                p.last_name as lastName,
                p.party as party,
                MAX(pp.date) as lastSpeechDate,
                COUNT(s.id) as speechCount,
                SUM(ARRAY_LENGTH(REGEXP_SPLIT_TO_ARRAY(s.text_plain, '\\s+'), 1)) as totalWords
            FROM person p
            JOIN speech s ON p.id = s.person_id
            JOIN agenda_item ai ON s.agenda_item_id = ai.id
            JOIN plenary_protocol pp ON ai.plenary_protocol_id = pp.id
            GROUP BY p.id, p.first_name, p.last_name, p.party
            ORDER BY lastSpeechDate DESC NULLS LAST, totalWords DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT p.id)
                    FROM person p
                    JOIN speech s ON p.id = s.person_id
                    """,
            nativeQuery = true)
    Page<SpeakerStatisticDto> findAllSpeakerStatistics(Pageable pageable);

    @Query(value = """
            SELECT
                p.party as party,
                pp.date as protocolDate,
                ARRAY_LENGTH(REGEXP_SPLIT_TO_ARRAY(s.text_plain, '\\s+'), 1) as wordCount,
                p.first_name as firstName,
                p.last_name as lastName,
                ai.title as agendaItemTitle,
                s.text_summary as textSummary,
                s.text_plain as textPlain,
                CONCAT(pp.document_number, '. Sitzung des ', pp.election_period, '. Deutschen ',
                       CASE WHEN pp.publisher ILIKE 'BR' THEN 'Bundesrat' ELSE 'Bundestag' END) as protocolName
            FROM speech s
            JOIN person p ON s.person_id = p.id
            JOIN agenda_item ai ON s.agenda_item_id = ai.id
            JOIN plenary_protocol pp ON ai.plenary_protocol_id = pp.id
            WHERE (:partiesSize = 0 OR p.party in :parties)
              AND (:speakerIdsSize = 0 OR p.id in :speakerIds)
              AND (:plenaryProtocolId IS NULL OR pp.id = :plenaryProtocolId)
            ORDER BY pp.date DESC NULLS LAST,
                     ARRAY_LENGTH(REGEXP_SPLIT_TO_ARRAY(s.text_plain, '\\s+'), 1) DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM speech s
                    JOIN person p ON s.person_id = p.id
                    JOIN agenda_item ai ON s.agenda_item_id = ai.id
                    JOIN plenary_protocol pp ON ai.plenary_protocol_id = pp.id
                    WHERE (:partiesSize = 0 OR p.party in :parties)
                      AND (:speakerIdsSize = 0 OR p.id in :speakerIds)
                      AND (:plenaryProtocolId IS NULL OR pp.id = :plenaryProtocolId)
                    """,
            nativeQuery = true)
    Page<SpeechDto> findAllSpeechDetailsFiltered(Pageable pageable, List<String> parties, Integer partiesSize, List<Integer> speakerIds, Integer speakerIdsSize, Integer plenaryProtocolId);

    @Query(value = """
              SELECT p.party as party,
                pp.date as protocolDate,
                ARRAY_LENGTH(REGEXP_SPLIT_TO_ARRAY(s.text_plain, '\\\\s+'), 1) as wordCount,
                p.first_name as firstName,
                p.last_name as lastName,
                ai.title as agendaItemTitle,
                s.text_summary as textSummary,
                s.text_plain as textPlain,
                CONCAT(pp.document_number, '. Sitzung des ', pp.election_period, '. Deutschen ',
                       CASE WHEN pp.publisher ILIKE 'BR' THEN 'Bundesrat' ELSE 'Bundestag' END) as protocolName,
                1 - (s.text_embedding <=> CAST(:embedding AS vector)) as similarity
            FROM speech s
            JOIN person p ON s.person_id = p.id
            JOIN agenda_item ai ON s.agenda_item_id = ai.id
            JOIN plenary_protocol pp ON ai.plenary_protocol_id = pp.id
            WHERE (:partiesSize = 0 OR p.party in :parties)
              AND (:speakerIdsSize = 0 OR p.id in :speakerIds)
              AND (:plenaryProtocolId IS NULL OR pp.id = :plenaryProtocolId)
              AND (1 - (s.text_embedding <=> CAST(:embedding AS vector))) >= :similarityThreshold
            ORDER BY similarity DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM speech s
                    JOIN person p ON s.person_id = p.id
                    JOIN agenda_item ai ON s.agenda_item_id = ai.id
                    JOIN plenary_protocol pp ON ai.plenary_protocol_id = pp.id
                    WHERE (:partiesSize = 0 OR p.party in :parties)
                      AND (:speakerIdsSize = 0 OR p.id in :speakerIds)
                      AND (:plenaryProtocolId IS NULL OR pp.id = :plenaryProtocolId)
                      AND (1 - (s.text_embedding <=> CAST(:embedding AS vector))) >= :similarityThreshold
                    """,
            nativeQuery = true)
    Page<SpeechDto> findAllSpeechDetailsFilteredOrderedByEmbeddingSimilarity(Pageable pageable, List<String> parties, Integer partiesSize, List<Integer> speakerIds, Integer speakerIdsSize, Integer plenaryProtocolId, String embedding, float similarityThreshold);

    @Query(value = """
            SELECT
                p.party as party,
                COUNT(s.id) as speechCount,
                SUM(ARRAY_LENGTH(REGEXP_SPLIT_TO_ARRAY(s.text_plain, '\\s+'), 1)) as totalWords,
                COUNT(DISTINCT p.id) as personCount,
                MAX(pp.date) as lastSpeechDate
            FROM person p
            JOIN speech s ON p.id = s.person_id
            JOIN agenda_item ai ON s.agenda_item_id = ai.id
            JOIN plenary_protocol pp ON ai.plenary_protocol_id = pp.id
            WHERE p.party IS NOT NULL
            GROUP BY p.party
            ORDER BY totalWords DESC
            """,
            nativeQuery = true)
    List<PartyStatisticsDto> findAllPartyStatistics();
}
