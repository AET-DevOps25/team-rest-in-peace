package com.rip.browsing_service.repository;

import com.rip.browsing_service.model.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, Integer> {
    @Query("SELECT a.id FROM AgendaItem a WHERE a.plenaryProtocol.id = :plenaryProtocolId")
    List<Integer> findAllIdsByPlenaryProtocolId(Integer plenaryProtocolId);
}