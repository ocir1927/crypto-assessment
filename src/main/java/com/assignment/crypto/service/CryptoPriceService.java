package com.assignment.crypto.service;

import com.assignment.crypto.domain.CryptoPrice;
import com.assignment.crypto.dto.CryptoInfoDTO;
import com.assignment.crypto.dto.CryptoNormalizedRangeDTO;
import com.assignment.crypto.repository.CryptoMetricsRepository;
import com.assignment.crypto.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoPriceService {

    private final CryptoPriceRepository cryptoPriceRepository;

    private final CryptoMetricsRepository cryptoMetricsRepository;

    public List<CryptoNormalizedRangeDTO> getAllNormalizedRangeSortedDescending(){
        return cryptoMetricsRepository.findAllByOrderByNormalizedRangeDesc()
                .stream()
                .map(cryptoMetrics -> new CryptoNormalizedRangeDTO(cryptoMetrics.getTicker(), cryptoMetrics.getNormalizedRange()))
                .toList();
    }

    public Optional<CryptoInfoDTO> calculateCryptoInfoForSymbol(final String symbol){
        return this.cryptoMetricsRepository.findByTicker(symbol)
                .map(cryptoMetrics -> new CryptoInfoDTO(cryptoMetrics.getTicker(),
                        cryptoMetrics.getOldestPrice(),
                        cryptoMetrics.getNewestPrice(),
                        cryptoMetrics.getMinPrice(),
                        cryptoMetrics.getMaxPrice(),
                        cryptoMetrics.getNormalizedRange(),
                        cryptoMetrics.getPriceStartDate(),
                        cryptoMetrics.getPriceEndDate()));
    }

    public Optional<CryptoNormalizedRangeDTO> getCryptoWithHighestNormalizedRange(LocalDate date){
        List<CryptoPrice> cryptoPricesForDay = cryptoPriceRepository.findByDate(date);

        if (cryptoPricesForDay.isEmpty()) {
            return Optional.empty(); // No data available for the given day
        }

        Map<String, List<CryptoPrice>> cryptoPricesByTicker = cryptoPricesForDay.stream()
                .collect(Collectors.groupingBy(CryptoPrice::getTicker));

        return cryptoPricesByTicker
                .entrySet()
                .stream()
                .map(entry -> {
                    String ticker = entry.getKey();
                    List<CryptoPrice> tickerPrices = entry.getValue();
                    double minPrice = tickerPrices.stream()
                            .mapToDouble(CryptoPrice::getPrice)
                            .min()
                            .orElse(0.0);
                    double maxPrice = tickerPrices.stream()
                            .mapToDouble(CryptoPrice::getPrice)
                            .max()
                            .orElse(0.0);
                    double normalizedRange = (maxPrice - minPrice) / minPrice;
                    return new AbstractMap.SimpleEntry<>(ticker, normalizedRange);
                })
                .max(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue))
                .map(entry->new CryptoNormalizedRangeDTO(entry.getKey(), entry.getValue()));
    }


}
