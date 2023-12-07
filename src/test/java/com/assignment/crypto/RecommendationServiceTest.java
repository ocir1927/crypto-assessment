package com.assignment.crypto;

import com.assignment.crypto.domain.CryptoMetrics;
import com.assignment.crypto.domain.CryptoPrice;
import com.assignment.crypto.dto.CryptoInfoDTO;
import com.assignment.crypto.dto.CryptoNormalizedRangeDTO;
import com.assignment.crypto.repository.CryptoMetricsRepository;
import com.assignment.crypto.repository.CryptoPriceRepository;
import com.assignment.crypto.service.CryptoPriceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecommendationServiceTest {

    @Autowired
    private CryptoPriceService cryptoService;

    @Autowired
    private CryptoPriceRepository cryptoPriceRepository;

    @Autowired
    private CryptoMetricsRepository cryptoMetricsRepository;

    private TestRestTemplate restTemplate;

    private static final String BASE_URL_PREFIX = "http://localhost:";

    protected String baseUrl;

    @LocalServerPort
    private Long port;

    @BeforeEach
    public void setUp() {
        this.baseUrl = BASE_URL_PREFIX + this.port;
        restTemplate = new TestRestTemplate();
    }

    @Test
    public void testGetAllNormalizedRanges() {
        ResponseEntity<List<CryptoNormalizedRangeDTO>> response = restTemplate.exchange(
                createURLWithPort("/api/v1/recommendation/normalized-ranges"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());

        List<CryptoNormalizedRangeDTO> normalizedRanges = response.getBody();
        Assertions.assertNotNull(normalizedRanges);
        Assertions.assertEquals(5,response.getBody().size());

        boolean isSorted = true;
        for (int i = 1; i < normalizedRanges.size(); i++) {
            if (normalizedRanges.get(i - 1).getNormalizedRange() < normalizedRanges.get(i).getNormalizedRange()) {
                isSorted = false;
                break;
            }
        }

        // Assert that the list is sorted
        Assertions.assertTrue(isSorted);
    }

    @Test
    public void testGetCryptoInfo(){
        final String queryThicker = "BTC";

        final CryptoMetrics expectedResult = this.cryptoMetricsRepository.findByTicker(queryThicker).orElseThrow();

        ResponseEntity<CryptoInfoDTO> response = restTemplate.exchange(
                createURLWithPort("/api/v1/recommendation/" + queryThicker),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        final CryptoInfoDTO result = response.getBody();
        Assertions.assertNotNull(result);

        Assertions.assertEquals(expectedResult.getMaxPrice(), result.getMaxPrice());
        Assertions.assertEquals(expectedResult.getMinPrice(), result.getMinPrice());
        Assertions.assertEquals(expectedResult.getOldestPrice(), result.getOldestPrice());
        Assertions.assertEquals(expectedResult.getNewestPrice(), result.getNewestPrice());

    }

    @Test
    public void testGetCryptoInfo_notAllowedCrypto_fails(){
        final String queryThicker = "BLA";

        ResponseEntity<CryptoInfoDTO> response = restTemplate.exchange(
                createURLWithPort("/api/v1/recommendation/" + queryThicker),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

    }

    @Test
    public void testGetHighestNormalizedRangePerDay(){
        final LocalDate date = LocalDate.of(2022,1,12);

        final List<CryptoPrice> cryptoPricesForDate = this.cryptoPriceRepository.findByDate(date);

        Map<String, List<CryptoPrice>> cryptoPricesByTicker = cryptoPricesForDate.stream()
                .collect(Collectors.groupingBy(CryptoPrice::getTicker));

        var expectedMaxNormalizedRange = cryptoPricesByTicker
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

        ResponseEntity<CryptoNormalizedRangeDTO> response = restTemplate.exchange(
                createURLWithPort("/api/v1/recommendation/highest-range/" + "2022-01-12"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        final CryptoNormalizedRangeDTO result = response.getBody();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(expectedMaxNormalizedRange.isPresent());
        Assertions.assertEquals(expectedMaxNormalizedRange.get().getSymbol(), result.getSymbol());
        Assertions.assertEquals(expectedMaxNormalizedRange.get().getNormalizedRange(), result.getNormalizedRange());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + this.port + uri;
    }
}
