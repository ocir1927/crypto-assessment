package com.assignment.crypto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoNormalizedRangeDTO {

    private String symbol;

    private Double normalizedRange;
}
