package com.rip.browsing_service.repository;

import com.rip.browsing_service.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT COUNT(DISTINCT p.party) FROM Person p")
    long countDistinctParties();
}