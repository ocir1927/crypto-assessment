package com.assignment.crypto.service;

import com.assignment.crypto.domain.CryptoMetrics;
import com.assignment.crypto.domain.CryptoPrice;
import com.assignment.crypto.domain.FileImportLog;
import com.assignment.crypto.dto.CryptoInfoDTO;
import com.assignment.crypto.repository.CryptoMetricsRepository;
import com.assignment.crypto.repository.CryptoPriceRepository;
import com.assignment.crypto.repository.FileImportLogRepository;
import com.assignment.crypto.util.CryptoMetricsCalculatorUtil;
import com.assignment.crypto.util.CryptoValidator;
import com.assignment.crypto.util.FileHashUtil;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class CryptoPriceCSVLoader {

    private final CryptoPriceRepository cryptoPriceRepository;

    private final CryptoMetricsRepository cryptoMetricsRepository;

    private final FileImportLogRepository fileImportLogRepository;

    private final CryptoValidator cryptoValidator;

    public CryptoPriceCSVLoader(final CryptoPriceRepository cryptoPriceRepository,
                                final CryptoMetricsRepository cryptoMetricsRepository,
                                final FileImportLogRepository fileImportLogRepository,
                                final CryptoValidator cryptoValidator) {
        this.cryptoPriceRepository = cryptoPriceRepository;
        this.cryptoMetricsRepository = cryptoMetricsRepository;
        this.fileImportLogRepository = fileImportLogRepository;
        this.cryptoValidator = cryptoValidator;
    }

    public void loadFromCSV(String filePath) throws IOException {
        String fileHash = "";
        FileImportLog importLog = new FileImportLog();

        try {
            fileHash = FileHashUtil.calculateFileHash(filePath);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(fileImportLogRepository.existsByFileHash(fileHash).isPresent()){
            log.error("File was already imported. Skipping...");
            return;
        }

        importLog.setFileHash(fileHash);
        importLog.setFileName(filePath);

        log.info("Reading data from: {}", filePath);
        List<CryptoPrice> cryptoPrices = new ArrayList<>();
        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                Instant timestamp = Instant.ofEpochMilli(Long.parseLong(record.get("timestamp")));
                String symbol = record.get("symbol");
                double price = Double.parseDouble(record.get("price"));
                CryptoPrice cryptoPrice = new CryptoPrice(timestamp, symbol, price);
                cryptoPrices.add(cryptoPrice);
            }
            this.cryptoPriceRepository.saveAll(cryptoPrices);
            this.calculateAndStoreCryptoMetrics(cryptoPrices);
            importLog.setDateTime(Instant.now());
            this.fileImportLogRepository.save(importLog);
        }
    }

    public void loadFromUploadedCSV(MultipartFile file) throws IOException {
        boolean valid = false;

        File tempFile = File.createTempFile("temp", ".csv");
        file.transferTo(tempFile);

        List<CryptoPrice> cryptoPrices = new ArrayList<>();
        try (Reader reader = new FileReader(tempFile);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                Instant timestamp = Instant.ofEpochMilli(Long.parseLong(record.get("timestamp")));
                String symbol = record.get("symbol");
                double price = Double.parseDouble(record.get("price"));
                CryptoPrice cryptoPrice = new CryptoPrice(timestamp, symbol, price);
                if(!valid){
                    if(this.cryptoValidator.isCryptoAccepted(symbol)){
                        valid = true;
                    }else {
                        throw new ValidationException("Crypto not allowed");
                    }
                }
                cryptoPrices.add(cryptoPrice);
            }
            this.cryptoPriceRepository.saveAll(cryptoPrices);
            this.calculateAndStoreCryptoMetrics(cryptoPrices);
        }
    }

    private void calculateAndStoreCryptoMetrics(final List<CryptoPrice> valuesForOneCrypto){
        final CryptoInfoDTO cryptoInfoDto = CryptoMetricsCalculatorUtil.calculateCryptoInfo(valuesForOneCrypto);
        CryptoMetrics metrics= new CryptoMetrics();
        metrics.setTicker(cryptoInfoDto.getSymbol());
        metrics.setMaxPrice(cryptoInfoDto.getMaxPrice());
        metrics.setMinPrice(cryptoInfoDto.getMinPrice());
        metrics.setOldestPrice(cryptoInfoDto.getOldestPrice());
        metrics.setNewestPrice(cryptoInfoDto.getNewestPrice());
        metrics.setNormalizedRange(cryptoInfoDto.getNormalizedRange());
        metrics.setPriceStartDate(cryptoInfoDto.getPriceStartDate());
        metrics.setPriceEndDate(cryptoInfoDto.getPriceEndDate());
        cryptoMetricsRepository.save(metrics);
    }

}
