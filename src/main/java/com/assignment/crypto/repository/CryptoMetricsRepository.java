package com.assignment.crypto.repository;

import com.assignment.crypto.domain.CryptoMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoMetricsRepository extends JpaRepository<CryptoMetrics, Long> {

    List<CryptoMetrics> findAllByOrderByNormalizedRangeDesc();

    Optional<CryptoMetrics> findByTicker(String ticker);

    @Modifying
    void deleteByTicker(String ticker);

}
