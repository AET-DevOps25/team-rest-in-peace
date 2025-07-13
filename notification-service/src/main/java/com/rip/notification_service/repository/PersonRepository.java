package com.rip.notification_service.repository;

import com.rip.notification_service.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT DISTINCT p.party FROM Person p")
    Optional<List<String>> findDistinctParties();

    @Override
    Optional<Person> findById(Integer integer);
}