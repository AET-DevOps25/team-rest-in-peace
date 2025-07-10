package com.rip.browsing_service.repository;

import com.rip.browsing_service.model.PlenaryProtocol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlenaryProtocolRepository extends JpaRepository<PlenaryProtocol, Integer> {
    @Query(value = """
    SELECT * FROM plenary_protocol
    ORDER BY date DESC NULLS LAST
    """, nativeQuery = true)
    Page<PlenaryProtocol> findAllOrderByDateDescNullsLast(Pageable pageable);
}