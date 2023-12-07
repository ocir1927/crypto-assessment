package com.assignment.crypto.util;

import com.assignment.crypto.domain.CryptoPrice;
import com.assignment.crypto.dto.CryptoInfoDTO;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class CryptoMetricsCalculatorUtil {

    public static CryptoInfoDTO calculateCryptoInfo(final List<CryptoPrice> prices) {
        // Calculate the oldest, newest, min, and max prices
        double oldestPrice = prices.stream()
                .mapToDouble(CryptoPrice::getPrice)
                .min()
                .orElse(0);
        double newestPrice = prices.stream()
                .mapToDouble(CryptoPrice::getPrice)
                .max()
                .orElse(0);
        double minPrice = prices.stream()
                .mapToDouble(CryptoPrice::getPrice)
                .min()
                .orElse(0);
        double maxPrice = prices.stream()
                .mapToDouble(CryptoPrice::getPrice)
                .max()
                .orElse(0);

        Instant oldestDate = prices
                .stream()
                .map(CryptoPrice::getPriceTimestamp)
                .min(Comparator.naturalOrder())
                .orElse(null);

        Instant newestDate = prices
                .stream()
                .map(CryptoPrice::getPriceTimestamp)
                .max(Comparator.naturalOrder())
                .orElse(null);

        // Calculate the normalized range
        double normalizedRange = (maxPrice - minPrice) / minPrice;

        // Create and return the CryptoInfo object
        return new CryptoInfoDTO(prices.get(0).getTicker(), oldestPrice, newestPrice, minPrice, maxPrice, normalizedRange, oldestDate, newestDate);
    }
}
