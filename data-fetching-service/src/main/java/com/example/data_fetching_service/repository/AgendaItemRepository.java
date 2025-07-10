package com.example.data_fetching_service.repository;

import com.example.data_fetching_service.model.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, Integer> {

}