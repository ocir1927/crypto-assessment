package com.assignment.crypto.repository;

import com.assignment.crypto.domain.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {

    @Query("""
            SELECT cp FROM CryptoPrice cp
            WHERE DATE(cp.priceTimestamp) = :date
            """)
    List<CryptoPrice> findByDate(@Param("date") LocalDate date);

    List<CryptoPrice> findAllByTicker(@Param("ticker") String ticker);

    @Modifying
    void deleteByTicker(String ticker);
}
