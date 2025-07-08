package com.rip.browsing_service.repository;

import com.rip.browsing_service.model.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, Integer> {

}