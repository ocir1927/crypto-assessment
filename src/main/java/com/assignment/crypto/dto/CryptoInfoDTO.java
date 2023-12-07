package com.assignment.crypto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptoInfoDTO {

    private String symbol;
    private double oldestPrice;
    private double newestPrice;
    private double minPrice;
    private double maxPrice;
    private double normalizedRange;
    private Instant priceStartDate;
    private Instant priceEndDate;
}
