package com.assignment.crypto.controller;

import com.assignment.crypto.service.CryptoPriceCSVLoader;
import com.assignment.crypto.service.CryptoPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Controller to import new cryptocurrency data", description = "Used to import cryptocurrency data")
public class CryptoImportController {

    private final CryptoPriceCSVLoader cryptoPriceCSVLoader;

    public CryptoImportController(final CryptoPriceCSVLoader cryptoPriceCSVLoader) {
        this.cryptoPriceCSVLoader = cryptoPriceCSVLoader;
    }

    @PostMapping("/import")
    @Operation(description = "Upload a new file containing cryptocurrencies")
    @Parameter(name = "file", description = "A CSV file with 3 columns: timestamp,symbol,price. First Column are the table headers")
    public ResponseEntity<String> uploadCryptoPrices(@RequestParam("file")MultipartFile file) {
        try {
            cryptoPriceCSVLoader.loadFromUploadedCSV(file);
            return ResponseEntity.ok("Cryptocurrencies loaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error uploading file.");
        } catch (ValidationException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cryptocurrency is not allowed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error loading cryptocurrencies.");
        }
    }

}
