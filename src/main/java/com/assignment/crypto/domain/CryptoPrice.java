package com.assignment.crypto.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "crypto_price")
@NoArgsConstructor
@Getter
@Setter
public class CryptoPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price_timestamp")
    private Instant priceTimestamp;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "price")
    private Double price;

    public CryptoPrice(Instant priceTimestamp, String ticker, Double price) {
        this.priceTimestamp = priceTimestamp;
        this.ticker = ticker;
        this.price = price;
    }
}
