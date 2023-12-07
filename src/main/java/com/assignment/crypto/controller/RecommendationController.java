package com.assignment.crypto.controller;

import com.assignment.crypto.dto.CryptoInfoDTO;
import com.assignment.crypto.dto.CryptoNormalizedRangeDTO;
import com.assignment.crypto.service.CryptoPriceService;
import com.assignment.crypto.util.CryptoValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendation")
@Tag(name = "Recommendation Controller", description = "Used for crypto recommendation based on various metrics")
public class RecommendationController {

    private final CryptoPriceService cryptoPriceService;

    private final CryptoValidator cryptoValidator;

    public RecommendationController(final CryptoPriceService cryptoPriceService,
                                    final CryptoValidator cryptoValidator) {
        this.cryptoPriceService = cryptoPriceService;
        this.cryptoValidator = cryptoValidator;
    }

    @GetMapping("/normalized-ranges")
    @Operation(description = "Retrieves normalized ranges sorted descending")
    @Parameter
    public ResponseEntity<List<CryptoNormalizedRangeDTO>> getAllNormalizedRanges() {
        return ResponseEntity.ok(cryptoPriceService.getAllNormalizedRangeSortedDescending());
    }

    @GetMapping("/{symbol}")
    @Operation(description = "Retrieves oldest/newest/min/max values for a certain cryptocurrency")
    @Parameter(name = "symbol", example = "BTC", description = "Symbol of the cryptocurrency to get values for")
    public ResponseEntity<CryptoInfoDTO> getCryptoInfo(@PathVariable String symbol) {
        if(!this.cryptoValidator.isCryptoAccepted(symbol)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.of(cryptoPriceService.calculateCryptoInfoForSymbol(symbol));
    }

    @GetMapping("/highest-range/{date}")
    @Operation(description = "Retrieves the crypto with the highest normalized range for a specific day")
    @Parameter(name = "date", example = "2022-01-12", description = "The date in format yyyy-mm-dd")
    public ResponseEntity<CryptoNormalizedRangeDTO> getCryptoWithHighestRange(@PathVariable String date) {
        final LocalDate targetDate;

        try {
            targetDate = LocalDate.parse(date);
        } catch (DateTimeParseException exception){
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.of(cryptoPriceService.getCryptoWithHighestNormalizedRange(targetDate));
    }
}
