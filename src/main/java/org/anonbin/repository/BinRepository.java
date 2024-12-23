package org.anonbin.repository;

import org.anonbin.model.BinModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<BinModel, Long> {
    Optional<BinModel> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Iterable<BinModel> findByExpirationTimeBeforeAndExpirationTimeIsNotNull(LocalDateTime expirationTime);
}

