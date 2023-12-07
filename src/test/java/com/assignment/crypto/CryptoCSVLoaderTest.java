package com.assignment.crypto;

import com.assignment.crypto.domain.CryptoMetrics;
import com.assignment.crypto.domain.CryptoPrice;
import com.assignment.crypto.repository.CryptoMetricsRepository;
import com.assignment.crypto.repository.CryptoPriceRepository;
import com.assignment.crypto.repository.FileImportLogRepository;
import com.assignment.crypto.service.CryptoPriceCSVLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CryptoCSVLoaderTest {

    private final static String TEST_CRYPTO = "BNB";
    private final static String TEST_CRYPTO_FILENAME = "prices/BNB_values.csv";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CryptoPriceCSVLoader cryptoPriceCSVLoader;

    @Autowired
    private CryptoPriceRepository cryptoPriceRepository;

    @Autowired
    private CryptoMetricsRepository cryptoMetricsRepository;

    @Autowired
    private FileImportLogRepository fileImportLogRepository;

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

    @AfterEach
    public void afterEach() {
        this.cryptoPriceRepository.deleteByTicker(TEST_CRYPTO);
        this.cryptoMetricsRepository.deleteByTicker(TEST_CRYPTO);
        this.fileImportLogRepository.deleteByFileName(TEST_CRYPTO_FILENAME);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testLoadFromCSV() {
        String csvFilePath = "prices/BNB_values.csv";

        try {
            cryptoPriceCSVLoader.loadFromCSV(csvFilePath);
        } catch (Exception e) {
            Assertions.fail("Failed to load data from CSV: " + e.getMessage());
        }

        long count = cryptoPriceRepository.findAllByTicker(TEST_CRYPTO).size();
        Assertions.assertTrue(count > 0, "CSV data was not loaded into the database");
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testImportCryptoFileUpload() throws Exception {
        // Create a mock CSV file
        String csvData = "timestamp,symbol,price\n1641009600000,BNB,46813.21\n1654473600000,BNB,48813.21\n1659744000000,BNB,47813.21\n";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(csvData.getBytes()) {
            @Override
            public String getFilename() {
                return "test.csv";
            }
        });

        // Perform the file upload request
        ResponseEntity<String> response = restTemplate
                .postForEntity(createURLWithPort("/api/v1/import"),
                new HttpEntity<>(body, headers), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cryptocurrencies loaded successfully.", response.getBody());

        List<CryptoPrice> newlyUploadedData = this.cryptoPriceRepository.findAllByTicker(TEST_CRYPTO);
        Assertions.assertEquals(3, newlyUploadedData.size());

        Optional<CryptoMetrics> newCryptoMetrics = this.cryptoMetricsRepository.findByTicker(TEST_CRYPTO);
        Assertions.assertTrue(newCryptoMetrics.isPresent());
        Assertions.assertEquals(Instant.ofEpochMilli(1659744000000L),newCryptoMetrics.get().getPriceEndDate());
        Assertions.assertEquals(Instant.ofEpochMilli(1641009600000L),newCryptoMetrics.get().getPriceStartDate());

    }

    @Test
    public void testLoadFromCSV_notAllowedCryptocurrency() {
        // Create a mock CSV file
        String csvData = "timestamp,symbol,price\n1641009600000,BBLA,46813.21\n1654473600000,BBLA,48813.21\n1659744000000,BBLA,47813.21\n";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(csvData.getBytes()) {
            @Override
            public String getFilename() {
                return "test.csv";
            }
        });

        // Perform the file upload request
        ResponseEntity<String> response = restTemplate
                .postForEntity(createURLWithPort("/api/v1/import"),
                        new HttpEntity<>(body, headers), String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + this.port + uri;
    }
}
