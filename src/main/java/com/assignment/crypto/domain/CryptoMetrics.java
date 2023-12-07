package com.assignment.crypto.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "crypto_metrics")
@Getter
@Setter
@NoArgsConstructor
public class CryptoMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "oldest_price")
    private Double oldestPrice;

    @Column(name = "newest_price")
    private double newestPrice;

    @Column(name = "min_price")
    private double minPrice;

    @Column(name = "max_price")
    private double maxPrice;

    @Column(name = "normalized_range")
    private double normalizedRange;

    @Column(name = "price_start_date")
    private Instant priceStartDate;

    @Column(name = "price_end_date")
    private Instant priceEndDate;

}
